package Glitch.Lib.Controller;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/**
 * Base class for robot controllers with role-specific bindings.
 */
public abstract class Controller {
  public enum Operator {
    MAIN,
    ASSIST,
  }

  private final Operator m_operator;

  private CommandXboxController m_controller;

  protected Controller(Operator operator) {
    m_operator = operator;
    initController();
  }

  private int getPort() {
    return switch (m_operator) {
      case MAIN -> 0;
      case ASSIST -> 1;
    };
  }

  private void initController() {
    m_controller = new CommandXboxController(getPort());
  }

  protected abstract void configureBindings();

  public final void clearBindings() {
    // Re-creating the controller instance effectively clears trigger bindings.
    initController();
  }

  protected final CommandXboxController getController() {
    return m_controller;
  }
}
