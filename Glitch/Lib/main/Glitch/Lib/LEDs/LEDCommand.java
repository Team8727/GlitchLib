package Glitch.Lib.LEDs;

import Glitch.Lib.LEDs.AbstractLEDS.Section;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.Command;

// Note: Still not entirely functional. Doesn't seem to work properly with onTrue triggers yet.
public class LEDCommand extends Command {

    private final Section section;
    private final LEDPattern pattern;

    public LEDCommand(Section section, LEDPattern pattern) {
        this.section = section;
        this.pattern = pattern;
    }

    @Override
    public void initialize() {
        section.setPattern(pattern);
    }

    @Override
    public void end(boolean interrupted) {
        section.setPattern(pattern, 0);
    }
}
