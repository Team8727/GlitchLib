package Glitch.Lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.*;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom NetworkTable logger created by Glitch 2.0. This logger is used to log values to
 * the network table (can be seen using AdvantageScope, Glass, Elastic, etc.). ALL log() methods 
 * for different types of values must be placed in a periodically updating part of code for the 
 * subsystem (like periodic()) in order to update properly.
 */
public class NetworkTableLogger {
  private static final double MAX_LOGGING_RATE_HZ = 50.0;

  // NetworkTable to log to
  private final NetworkTable table;

  // Publishers for each supported type
  private final ConcurrentHashMap<String, BooleanPublisher> booleanPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, DoublePublisher> doublePublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, IntegerPublisher> integerPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, StringPublisher> stringPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, FloatPublisher> floatPublishers = new ConcurrentHashMap<>();
  
  private final ConcurrentHashMap<String, BooleanArrayPublisher> booleanArrayPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, DoubleArrayPublisher> doubleArrayPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, IntegerArrayPublisher> integerArrayPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, StringArrayPublisher> stringArrayPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, FloatArrayPublisher> floatArrayPublishers = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, StructPublisher<?>> structPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, StructArrayPublisher<?>> structArrayPublishers = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, SendableBuilderImpl> sendableBuilders = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Sendable> sendablesByKey = new ConcurrentHashMap<>();

  // Last values that were actually published.
  private final ConcurrentHashMap<String, Double> lastDoubleValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Long> lastIntegerValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Boolean> lastBooleanValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String> lastStringValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Float> lastFloatValues = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, double[]> lastDoubleArrayValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, long[]> lastIntArrayValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, long[]> lastLongArrayValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, boolean[]> lastBooleanArrayValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, String[]> lastStringArrayValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, float[]> lastFloatArrayValues = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, Object> lastStructValues = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Object[]> lastStructArrayValues = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<String, PublishGate> publishGates = new ConcurrentHashMap<>();
  private volatile double loggingRateHz;
  private volatile long minPublishPeriodNanos;

  private static class PublishGate {
    private long lastPublishNanos = 0L;
  }

  /**
   * @param subsystemFor the subsystem this logger will log values for
   */
  public NetworkTableLogger(String subsystemFor) {
    this(subsystemFor, MAX_LOGGING_RATE_HZ);
  }

  /**
   * @param subsystemFor the subsystem this logger will log values for
   * @param loggingRateHz publish rate in Hz (0, 50]
   */
  public NetworkTableLogger(String subsystemFor, double loggingRateHz) {
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    table = inst.getTable(subsystemFor);
    setLoggingRateHz(loggingRateHz);
  }

  /**
   * Set the max publish rate for this logger.
   *
   * @param loggingRateHz publish rate in Hz (0, 50]
   */
  public final void setLoggingRateHz(double loggingRateHz) {
    if (!Double.isFinite(loggingRateHz) || loggingRateHz <= 0.0 || loggingRateHz > MAX_LOGGING_RATE_HZ) {
      throw new IllegalArgumentException("loggingRateHz must be > 0 and <= 50");
    }

    this.loggingRateHz = loggingRateHz;
    minPublishPeriodNanos = Math.max(1L, (long) (1_000_000_000.0 / loggingRateHz));
  }

  /**
   * @return current max publish rate in Hz
   */
  public double getLoggingRateHz() {
    return loggingRateHz;
  }

  /**
   * Get the network table that the logger is logging to
   *
   * @return the network table
   */
  public NetworkTable getNetworkTable() {
    return table;
  }

  private boolean shouldPublish(String key) {
    PublishGate gate = publishGates.computeIfAbsent(key, k -> new PublishGate());
    long now = System.nanoTime();

    synchronized (gate) {
      if (now - gate.lastPublishNanos < minPublishPeriodNanos) {
        return false;
      }

      gate.lastPublishNanos = now;
      return true;
    }
  }

  private static long[] toLongArray(int[] value) {
    long[] longArray = new long[value.length];
    for (int i = 0; i < value.length; i++) {
      longArray[i] = value[i];
    }
    return longArray;
  }

  private static boolean equalsIntToLongArray(int[] intArray, long[] longArray) {
    if (intArray.length != longArray.length) {
      return false;
    }

    for (int i = 0; i < intArray.length; i++) {
      if (intArray[i] != longArray[i]) {
        return false;
      }
    }

    return true;
  }

  // --- Primitives ---

  /**
   * Log a double value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, double value) {
    Double lastValue = lastDoubleValues.get(key);
    if (lastValue != null && Double.compare(lastValue, value) == 0) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    doublePublishers.computeIfAbsent(key, k -> table.getDoubleTopic(k).publish()).set(value);
    lastDoubleValues.put(key, value);
  }

  /**
   * Log an integer value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, int value) {
    log(key, (long) value);
  }

  /**
   * Log a long value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, long value) {
    Long lastValue = lastIntegerValues.get(key);
    if (lastValue != null && lastValue == value) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    integerPublishers.computeIfAbsent(key, k -> table.getIntegerTopic(k).publish()).set(value);
    lastIntegerValues.put(key, value);
  }

  /**
   * Log a boolean value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, boolean value) {
    Boolean lastValue = lastBooleanValues.get(key);
    if (lastValue != null && lastValue == value) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    booleanPublishers.computeIfAbsent(key, k -> table.getBooleanTopic(k).publish()).set(value);
    lastBooleanValues.put(key, value);
  }

  /**
   * Log a String value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, String value) {
    String lastValue = lastStringValues.get(key);
    if (Objects.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    stringPublishers.computeIfAbsent(key, k -> table.getStringTopic(k).publish()).set(value);
    lastStringValues.put(key, value);
  }

  /**
   * Log a float value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, float value) {
    Float lastValue = lastFloatValues.get(key);
    if (lastValue != null && Float.compare(lastValue, value) == 0) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    floatPublishers.computeIfAbsent(key, k -> table.getFloatTopic(k).publish()).set(value);
    lastFloatValues.put(key, value);
  }

  // --- Primitive Arrays ---

  public void log(String key, double[] value) {
    double[] lastValue = lastDoubleArrayValues.get(key);
    if (lastValue != null && Arrays.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    doubleArrayPublishers.computeIfAbsent(key, k -> table.getDoubleArrayTopic(k).publish()).set(value);
    lastDoubleArrayValues.put(key, value.clone());
  }

  public void log(String key, int[] value) {
    long[] lastValue = lastIntArrayValues.get(key);
    if (lastValue != null && equalsIntToLongArray(value, lastValue)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    long[] longArray = toLongArray(value);
    integerArrayPublishers.computeIfAbsent(key, k -> table.getIntegerArrayTopic(k).publish()).set(longArray);
    lastIntArrayValues.put(key, longArray);
  }

  public void log(String key, long[] value) {
    long[] lastValue = lastLongArrayValues.get(key);
    if (lastValue != null && Arrays.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    integerArrayPublishers.computeIfAbsent(key, k -> table.getIntegerArrayTopic(k).publish()).set(value);
    lastLongArrayValues.put(key, value.clone());
  }

  public void log(String key, boolean[] value) {
    boolean[] lastValue = lastBooleanArrayValues.get(key);
    if (lastValue != null && Arrays.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    booleanArrayPublishers.computeIfAbsent(key, k -> table.getBooleanArrayTopic(k).publish()).set(value);
    lastBooleanArrayValues.put(key, value.clone());
  }

  public void log(String key, String[] value) {
    String[] lastValue = lastStringArrayValues.get(key);
    if (lastValue != null && Arrays.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    stringArrayPublishers.computeIfAbsent(key, k -> table.getStringArrayTopic(k).publish()).set(value);
    lastStringArrayValues.put(key, value.clone());
  }

  public void log(String key, float[] value) {
    float[] lastValue = lastFloatArrayValues.get(key);
    if (lastValue != null && Arrays.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    floatArrayPublishers.computeIfAbsent(key, k -> table.getFloatArrayTopic(k).publish()).set(value);
    lastFloatArrayValues.put(key, value.clone());
  }

  // --- Generic Struct Support ---

  /**
   * Log a value using its Struct representation.
   * @param key the key to log to
   * @param value the value to log
   * @param struct the struct definition for the type
   */
  @SuppressWarnings("unchecked")
  public <T> void log(String key, T value, Struct<T> struct) {
    Object lastValue = lastStructValues.get(key);
    if (Objects.equals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    ((StructPublisher<T>) structPublishers.computeIfAbsent(key, k -> table.getStructTopic(k, struct).publish())).set(value);
    lastStructValues.put(key, value);
  }

  /**
   * Log an array of values using their Struct representation.
   * @param key the key to log to
   * @param value the value array to log
   * @param struct the struct definition for the type
   */
  @SuppressWarnings("unchecked")
  public <T> void log(String key, T[] value, Struct<T> struct) {
    Object[] lastValue = lastStructArrayValues.get(key);
    if (lastValue != null && Arrays.deepEquals(lastValue, value)) {
      return;
    }

    if (!shouldPublish(key)) {
      return;
    }

    ((StructArrayPublisher<T>) structArrayPublishers.computeIfAbsent(key, k -> table.getStructArrayTopic(k, struct).publish())).set(value);
    lastStructArrayValues.put(key, value.clone());
  }

  // --- Common WPILib Types Overloads ---

  public void log(String key, Pose2d value) {
    log(key, value, Pose2d.struct);
  }

  public void log(String key, Pose3d value) {
    log(key, value, Pose3d.struct);
  }

  public void log(String key, ChassisSpeeds value) {
    log(key, value, ChassisSpeeds.struct);
  }

  public void log(String key, SwerveModuleState[] value) {
    log(key, value, SwerveModuleState.struct);
  }

  // --- Sendable Support ---

  /**
   * Logs a Sendable to the network table.
   *
   * @param key the key, a string, that will represent the value
   * @param sendable the value (Sendable) that will be logged
   */
  public void logSendable(String key, Sendable sendable) {
    Sendable existingSendable = sendablesByKey.get(key);

    if (existingSendable == null || existingSendable != sendable) {
      SendableBuilderImpl oldBuilder = sendableBuilders.remove(key);
      if (oldBuilder != null) {
        oldBuilder.close();
      }

      SendableBuilderImpl builder = new SendableBuilderImpl();
      builder.setTable(table.getSubTable(key));
      SendableRegistry.publish(sendable, builder);
      builder.startListeners();
      builder.update();

      sendablesByKey.put(key, sendable);
      sendableBuilders.put(key, builder);
      return;
    }

    if (!shouldPublish(key + "__sendable")) {
      return;
    }

    SendableBuilderImpl builder = sendableBuilders.get(key);
    if (builder != null) {
      builder.update();
    }
  }
}
