package Glitch.Lib.BaseMechanisms;

import Glitch.Lib.Motors.Motor;
import Glitch.Lib.NetworkTableLogger;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public abstract class SimplePivot extends SubsystemBase {

  private final Motor motor;

  private double targetPosition = 0;

  private final ArmFeedforward pivotFeedforward;

  public final NetworkTableLogger logger;

  private final double zeroedAngelFromHorizontal;

  private final double allowedError;

  /**
   * Creates a new SimplePivot.
   *
   * @param motor                     The motor to use for the pivot.
   * @param zeroedAngelFromHorizontal The angle from horizontal to zero the pivot at
   * @param allowedError              The allowed error for the pivot in degrees
   * @param ks                        The static gain of the pivot
   * @param kg                        The gravity gain of the pivot
   * @param kv                        The velocity gain of the pivot
   * @param ka                        The acceleration gain of the pivot
   */
  public SimplePivot(
    Motor motor,
    double zeroedAngelFromHorizontal,
    double allowedError,
    double ks,
    double kg,
    double kv,
    double ka) {
    logger = new NetworkTableLogger(this.getName());

    pivotFeedforward = new ArmFeedforward(ks, kg, kv, ka);

    this.allowedError = allowedError;

    this.zeroedAngelFromHorizontal = zeroedAngelFromHorizontal;

    this.motor = motor;
  }

  /**
   * Creates a new SimplePivot.
   *
   * @param motor                     The motor to use for the pivot.
   * @param zeroedAngelFromHorizontal The angle from horizontal to zero the pivot at
   * @param allowedError              The allowed error for the pivot in degrees
   */
  public SimplePivot(
    Motor motor,
    double zeroedAngelFromHorizontal,
    double allowedError) {
    this(motor, zeroedAngelFromHorizontal, allowedError, 0, 0, 0, 0);
  }

  /**
   * Sets the pivot position.
   *
   * @param angleDegrees The angle in degrees to set the pivot to.
   */
  public void setPosition(double angleDegrees) {
    targetPosition = angleDegrees;
  }

  /**
   * Creates a command to set the pivot position to the specified angle in degrees.
   *
   * @param angleDegrees The angle in degrees to set the pivot to.
   * @return A command that sets the pivot position and waits until the pivot reaches the setpoint.
   */
  public Command setPositionCommand(double angleDegrees) {
    return new InstantCommand(() -> setPosition(angleDegrees))
      .andThen(new WaitUntilCommand(this::isAtSetpoint));
  }

  // set pivot position
  private void goToSetpoint() {
    // Calculate feedforward based on position (holding voltage)
    // Assuming standard ArmFeedforward.calculate(angleRadians, velocityRadiansPerSec)
    double angleRadians = Math.toRadians(zeroedAngelFromHorizontal - (getPosition() * 360));
    motor.setPosition(
      targetPosition / 360,
      pivotFeedforward.calculate(angleRadians, 0)
    );
  }

  public void setDutyCycle(double speed) {
    motor.setDutyCycle(speed);
  }

  public void zeroEncoder(){
    motor.zeroPosition();
  }

  /**
   * Checks if the pivot is at the setpoint.
   *
   * @return True if the pivot is within the allowed error of the goal position, false otherwise.
   */
  public boolean isAtSetpoint() {
    return Math.abs(targetPosition - getPosition() * 360) <= allowedError;
  }

  /**
   * Gets the current position of the pivot.
   *
   * @return The current position of the pivot.
   */
  public double getPosition() {
    return motor.getPosition();
  }

  /**
   * Gets the current applied to the motor.
   *
   * @return The current applied to the motor.
   */
  public double getCurrent() {
    return motor.getCurrent();
  }

  // This method will be called once per scheduler run
  @Override
  public void periodic() {
    logger.logDouble("position", motor.getPosition() * 360);
    logger.logDouble("targetPosition", targetPosition);

    goToSetpoint();
  }
}