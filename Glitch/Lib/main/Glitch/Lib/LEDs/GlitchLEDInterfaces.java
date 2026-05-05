package Glitch.Lib.LEDs;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

public class GlitchLEDInterfaces {
  // Preserving my nonsense down here for now until I can save it properly
    public interface ColorPattern extends LEDPattern {
    public Color color();

    public default void applyTo(LEDReader reader, LEDWriter writer) {
         int bufLen = reader.getLength();

         for(int led = 0; led < bufLen; ++led) {
            writer.setLED(led, color());
         }
    }
  }

  public class MyTestPattern implements ColorPattern {

    int i;

    public MyTestPattern() {
        i = 0;
    }
    public Color color() {
        i += 1;
        return new Color(i, i, i);
    }
  }

  // public LEDPattern pattern = new MyTestPattern();
  // public LEDPattern lambdaPattern = (ColorPattern) () -> new Color(0.5, 0.5, 0.5);
}
