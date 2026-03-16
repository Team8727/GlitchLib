package Glitch.Lib.Motors;

import com.revrobotics.PersistMode;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.*;
import com.revrobotics.spark.config.SparkMaxConfig;

import java.util.Set;

import static Glitch.Lib.Motors.SparkConfigurator.getSparkMax;

public class SparkMaxMotor implements Motor{
  private final SparkMax motor;
  private final SparkClosedLoopController motorController;
  private final RelativeEncoder encoder;
  private final SparkAbsoluteEncoder absoluteEncoder;
  private final boolean hasAbsoluteEncoder;

  public SparkMaxMotor(SparkMaxConfig config, int CANID, FeedbackSensor encoderType) {
    motor = getSparkMax(
      CANID,
      SparkLowLevel.MotorType.kBrushless,
      false,
      Set.of(),
      Set.of(
        SparkConfigurator.LogData.POSITION,
        SparkConfigurator.LogData.VELOCITY,
        SparkConfigurator.LogData.VOLTAGE,
        SparkConfigurator.LogData.CURRENT));

    motor.configure(
      config,
      ResetMode.kNoResetSafeParameters,
      PersistMode.kNoPersistParameters);

    motorController = motor.getClosedLoopController();
    encoder = motor.getEncoder();
    absoluteEncoder = motor.getAbsoluteEncoder();

    hasAbsoluteEncoder = encoderType == FeedbackSensor.kAbsoluteEncoder;
  }

  @Override
  public void setVelocity(double velocity) {
    motorController.setSetpoint(velocity, SparkBase.ControlType.kVelocity);
  }

  @Override
  public void setDutyCycle(double dutyCycle) {
    motorController.setSetpoint(dutyCycle, SparkBase.ControlType.kDutyCycle);
  }

  @Override
  public void setVoltage(double volts) {
    motor.setVoltage(volts);
  }

  @Override
  public void setPosition(double position, double feedforward) {
    motorController.setSetpoint(
      position,
      SparkBase.ControlType.kPosition,
      ClosedLoopSlot.kSlot0,
      feedforward);
  }

  @Override
  public void setPosition(double position) {
    motorController.setSetpoint(
      position,
      SparkBase.ControlType.kPosition);
  }

  @Override
  public void zeroPosition() {
    if (!hasAbsoluteEncoder) {
      encoder.setPosition(0);
    }
  }

  @Override
  public double getPosition() {
    if (hasAbsoluteEncoder) {
      return absoluteEncoder.getPosition();
    } else {
      return encoder.getPosition();
    }
  }

  @Override
  public double getCurrent() {
    return motor.getOutputCurrent();
  }

  @Override
  public double getVelocity() {
    if (hasAbsoluteEncoder) {
      return absoluteEncoder.getVelocity();
    } else {
      return encoder.getVelocity();
    }
  }

  @Override
  public boolean getForwardLimitSwitch() {
    return motor.getForwardLimitSwitch().isPressed();
  }

  @Override
  public boolean getReverseLimitSwitch() {
    return motor.getReverseLimitSwitch().isPressed();
  }
}