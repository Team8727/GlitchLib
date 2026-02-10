package Glitch.Lib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import Glitch.Lib.LEDS.AbstractLEDS;
import Glitch.Lib.LEDS.FakeLEDS;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LEDTests {

    private FakeLEDS testInstance = new FakeLEDS();

    private AddressableLEDBuffer stripBuffer = testInstance.stripBuffer;

    private AbstractLEDS.Section shortSide = testInstance.shortSide;
    private AbstractLEDS.Section shortReversed = testInstance.shortReversed;
    private AbstractLEDS.Section longSide = testInstance.longSide;
    private void periodic() {
        testInstance.periodic();
    }

    @BeforeEach
    public void setup() {
        shortSide.setPattern(LEDPattern.kOff);
        shortReversed.setPattern(LEDPattern.kOff);
        longSide.setPattern(LEDPattern.kOff);
        periodic();
    }

    @Test
    public void testLEDStripHasLength() {
        assertNotEquals(null, stripBuffer);
        assertNotEquals(0, stripBuffer.getLength());
    }

    @Test
    public void testOffMeansOff() {
        for (int i = 0; i < stripBuffer.getLength(); i++) {
            assertEquals(0, stripBuffer.getRed(i), 
                "LED " + " should be off. It's current red value is " + stripBuffer.getRed(i));
            assertEquals(0, stripBuffer.getGreen(i),
                "LED " + " should be off. It's current green value is " + stripBuffer.getGreen(i));
            assertEquals(0, stripBuffer.getBlue(i),
                "LED " + " should be off. It's current blue value is " + stripBuffer.getBlue(i));
        }
    }

    @Test
    public void testSetPatternAppliesPatternToStrip() {
        LEDPattern pattern = LEDPattern.solid(Color.kRed);
        shortSide.setPattern(pattern);
        shortReversed.setPattern(pattern);
        longSide.setPattern(pattern);
        periodic();

        for (int i = 0; i < stripBuffer.getLength(); i++) {
            assertEquals(255, stripBuffer.getRed(i), 
                "LED " + i + " should be red. It's current red value is " + stripBuffer.getRed(i));
            assertEquals(0, stripBuffer.getGreen(i),
                "LED " + i + " should be red. It's current green value is " + stripBuffer.getGreen(i));
            assertEquals(0, stripBuffer.getBlue(i),
                "LED " + i + " should be red. It's current blue value is " + stripBuffer.getBlue(i));
        }
    }

    @Test
    public void testLightStripConstructs() {
        testInstance.initializeLEDS(0);

        if (testInstance.isStripReal()) {
            assertEquals(true, testInstance.isStripReal());
            testInstance.disableLEDS();
        } else {
            assertEquals(true, testInstance.isStripReal(), "LED strip should be real, but is not.");
        }
    }

    @Test
    public void testLightStripDisables() {
        testInstance.disableLEDS();
        assertEquals(false, testInstance.isStripReal(), "LED strip should not be real, but is.");
    }
}
