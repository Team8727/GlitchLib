package Glitch.Lib.LEDS;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;

public class FakeLEDS extends AbstractLEDS {

    public final Section shortSide;
    public final Section shortReversed;
    public final Section longSide;

    public FakeLEDS() {
        super(100, 25, -25, 50);

        shortSide = getSections().get(0);
        shortSide.setBase(GlitchLEDPatterns.fire(GlitchLEDPatterns.funGradient, Color.kRed));

        shortReversed = getSections().get(1);
        shortReversed.setBase(GlitchLEDPatterns.fire(GlitchLEDPatterns.funGradient, Color.kGreen));

        longSide = getSections().get(2);
        longSide.setBase(GlitchLEDPatterns.fire(GlitchLEDPatterns.funGradient, Color.kBlue));
    }

    public void start() {
        shortSide.setPattern(LEDPattern.solid(Color.kPurple), 2);
        shortReversed.setPattern(LEDPattern.solid(Color.kPurple), 2);
        longSide.setPattern(LEDPattern.solid(Color.kPurple), 2);
    }
}
