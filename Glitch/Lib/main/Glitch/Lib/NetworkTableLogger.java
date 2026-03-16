package Glitch.Lib;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.*;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.struct.Struct;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom NetworkTable logger created by Glitch 2.0. This logger is used to log values to
 * the network table (can be seen using AdvantageScope, Glass, Elastic, etc.). ALL log() methods 
 * for different types of values must be placed in a periodically updating part of code for the 
 * subsystem (like periodic()) in order to update properly.
 */
public class NetworkTableLogger {
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
  
  // For Field2d and other Sendable support
  private static final Map<String, Sendable> tablesToData = new HashMap<>();

  /**
   * @param subsystemFor the subsystem this logger will log values for
   */
  public NetworkTableLogger(String subsystemFor) {
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    table = inst.getTable(subsystemFor);
  }

  /**
   * Get the network table that the logger is logging to
   *
   * @return the network table
   */
  public NetworkTable getNetworkTable() {
    return table;
  }

  // --- Primitives ---

  /**
   * Log a double value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, double value) {
    doublePublishers.computeIfAbsent(key, k -> table.getDoubleTopic(k).publish()).set(value);
  }

  /**
   * Log an integer value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, int value) {
    integerPublishers.computeIfAbsent(key, k -> table.getIntegerTopic(k).publish()).set(value);
  }
  
  /**
   * Log a long value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, long value) {
    integerPublishers.computeIfAbsent(key, k -> table.getIntegerTopic(k).publish()).set(value);
  }

  /**
   * Log a boolean value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, boolean value) {
    booleanPublishers.computeIfAbsent(key, k -> table.getBooleanTopic(k).publish()).set(value);
  }

  /**
   * Log a String value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, String value) {
    stringPublishers.computeIfAbsent(key, k -> table.getStringTopic(k).publish()).set(value);
  }
  
  /**
   * Reusable buffers for converting int[] to long[] to reduce GC pressure.
   * One buffer is kept per distinct input length.
   */
  private final Map<Integer, long[]> intArrayBuffers = new HashMap<>();

  /**
   * Log a float value
   * @param key the key to log to
   * @param value the value to log
   */
  public void log(String key, float value) {
    floatPublishers.computeIfAbsent(key, k -> table.getFloatTopic(k).publish()).set(value);
  }

  // --- Primitive Arrays ---

  public void log(String key, double[] value) {
    doubleArrayPublishers.computeIfAbsent(key, k -> table.getDoubleArrayTopic(k).publish()).set(value);
  }

  public void log(String key, int[] value) {
    long[] longArray = intArrayBuffers.computeIfAbsent(value.length, len -> new long[len]);
    for (int i = 0; i < value.length; i++) {
      longArray[i] = value[i];
    }
    integerArrayPublishers.computeIfAbsent(key, k -> table.getIntegerArrayTopic(k).publish()).set(longArray);
  }
  
  public void log(String key, long[] value) {
    integerArrayPublishers.computeIfAbsent(key, k -> table.getIntegerArrayTopic(k).publish()).set(value);
  }

  public void log(String key, boolean[] value) {
    booleanArrayPublishers.computeIfAbsent(key, k -> table.getBooleanArrayTopic(k).publish()).set(value);
  }

  public void log(String key, String[] value) {
    stringArrayPublishers.computeIfAbsent(key, k -> table.getStringArrayTopic(k).publish()).set(value);
  }
  
  public void log(String key, float[] value) {
    floatArrayPublishers.computeIfAbsent(key, k -> table.getFloatArrayTopic(k).publish()).set(value);
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
    ((StructPublisher<T>) structPublishers.computeIfAbsent(key, k -> table.getStructTopic(k, struct).publish())).set(value);
  }

  /**
   * Log an array of values using their Struct representation.
   * @param key the key to log to
   * @param value the value array to log
   * @param struct the struct definition for the type
   */
  @SuppressWarnings("unchecked")
  public <T> void log(String key, T[] value, Struct<T> struct) {
    ((StructArrayPublisher<T>) structArrayPublishers.computeIfAbsent(key, k -> table.getStructArrayTopic(k, struct).publish())).set(value);
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
    if (!tablesToData.containsKey(key)) {
        tablesToData.put(key, sendable);
        // Field2d and other Sendables in NT4 are often handled by SmartDashboard.putData
        // but for custom tables we might need more logic here if they aren't automatically published.
    }
  }
}
