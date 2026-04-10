package Glitch.Lib.BaseMechanisms;

import Glitch.Lib.Motors.Motor;
import Glitch.Lib.NetworkTableLogger;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Roller extends SubsystemBase {

  private final Motor motor;
  public final NetworkTableLogger logger;

  public enum ControlMode {
    FEEDFORWARD,
    PID,
    FF_AND_PID
  }

  /**
   * Creates a new Roller.
   *
   * @param motor The motor to use for the pivot.
   */
  public Roller(
      Motor motor) {
    logger = new NetworkTableLogger(this.getName());
    this.motor = motor;
  }

  /**
   * Sets the speed of the roller motor in duty cycle mode.
   *
   * @param speed The speed to set the motor to, as a percentage (0.0 to 1.0)
   */
  public void setDutyCycle(double speed) {
    motor.setDutyCycle(speed);
    logger.logDouble("set cycle", speed);
  }

  /**
   * Sets the voltage of the roller motor in voltage mode.
   *
   * @param volts The voltage to set the motor to
   */
  public void setVoltage(double volts) {
    motor.setVoltage(volts);
  }

  /**
   * Sets the speed of the roller motor in velocity mode, using one of the three control modes (PID, FF, FF_AND_PID).
   *
   * @param speed The speed to set the motor to, in RPS
   * @param mode The control mode to use when setting the velocity
   * @implNote The control modes are as follows:
   * <p> - PID: Uses the motor's built-in PID controller to achieve the target velocity </p>
   * <p> - FEEDFORWARD: Uses feedforward control to achieve the target velocity, without relying on the motor's built-in PID controller </p>
   * <p> - FF_AND_PID: Uses feedforward control to get close to the target velocity, and then uses the motor's built-in PID controller to fine-tune and maintain the target velocity </p>
   * <p> - Note: The feedforward constants used in the FEEDFORWARD and FF_AND_PID modes are determined by the motor's configuration. They are found using WPILib SimpleMotorFeedforward </p>
   */
  public void setVelocity(double speed, ControlMode mode) {
    motor.setVelocity(speed);
    logger.logDouble("set velocity", speed);
  }

  public void setVelocity(double speed) {
    this.setVelocity(speed, ControlMode.PID);
    logger.logDouble("set velocity", speed);
  }

  /**
   * Sets the speed of the roller motor in position mode.
   *
   * @param position The position to set the motor to, in encoder rotations
   */
  public void setPosition(double position) {
    motor.setPosition(position, 0);
  }

  /**
   * gets the position of the roller motor in encoder rotations.
   *
   * @return The position of the motor in encoder rotations
   */
  public double getPosition() {
    return  motor.getPosition();
  }

  /**
   * gets the velocity of the roller motor in RPM.
   *
   * @return The velocity of the motor in RPM
   */
  public double getVelocity() {
    return motor.getVelocity();
  }

  /**
   * gets the current of the roller motor in Amps.
   *
   * @return The current of the motor in Amps
   */
  public double getCurrent() {
    return motor.getCurrent();
  }

  /**
   * gets the forward limit switch state of the roller motor.
   *
   * @return The limit switch state of the motor
   */
  public boolean getForwardLimitSwitch() {
    return motor.getForwardLimitSwitch();
  }

  /**
   * gets the reverse limit switch state of the roller motor.
   *
   * @return The limit switch state of the motor
   */
  public boolean getReverseLimitSwitch() {
    return motor.getReverseLimitSwitch();
  }

  /** Gets the motor **/
  public Motor getMotor() {
    return motor;
  }

  // This method will be called once per scheduler run
  @Override
  public void periodic() {
    logger.logDouble("position", motor.getPosition());
    logger.logDouble("velocity", motor.getVelocity());
    logger.logDouble("current", motor.getCurrent());
    logger.logBoolean("forward limit switch", motor.getForwardLimitSwitch());
    logger.logBoolean("reverse limit switch", motor.getReverseLimitSwitch());
    logger.logBoolean("is running", motor.getCurrent() > 2);
  }
}