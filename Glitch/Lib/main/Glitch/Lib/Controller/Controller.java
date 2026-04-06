package Glitch.Lib.Controller;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/**
 * Base type for robot driver/operator controller profiles backed by a
 * {@link CommandXboxController}.
 *
 * <p>This class centralizes shared setup for command-based input devices:
 *
 * <ul>
 *   <li>Validates constructor input.
 *   <li>Creates the WPILib {@link CommandXboxController} instance.
 * </ul>
 *
 * <p>Subclasses are responsible for implementing {@link #configureBindings()} to map buttons,
 * triggers, and sticks to commands for a specific driver/operator layout.
 */
public abstract class Controller {
  /** Underlying WPILib controller used by subclasses when registering bindings. */
  protected CommandXboxController controller;

  /**
   * Creates a controller wrapper for the given driver station port.
   *
   * @param port HID port index from the driver station (must be {@code >= 0})
   * @throws IllegalArgumentException if {@code port < 0}
   */
  protected Controller(int port) {
    if (port < 0) {
      throw new IllegalArgumentException("Controller port must be >= 0");
    }
    controller = new CommandXboxController(port);
  }

  /**
   * Configures button/trigger bindings for this controller.
   *
   * <p>Implementations should map all commands needed for this controller profile and be invoked
   * exactly once during robot initialization for that profile.
   */
  protected abstract void configureBindings();
}
