import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.util.Color;

public class LEDPatterns {

    // LEDPatterns, generally speaking, shouldn't require their own class UNLESS you are using an interface like LEDArrayPattern or TwoDArrayPattern.
    // We're going to start with the basics first though, the tools that WPILib provides themselves.

    // The most basic pattern is a solid color.
    LEDPattern pattern1 = LEDPattern.solid(Color.kRed);
    // This creates a pattern that is completely red, no matter how long the strip is.

    // A little more complicated is the gradient pattern.
    LEDPattern pattern2 = LEDPattern.gradient(GradientType.kContinuous, Color.kRed, Color.kGreen);
    // This creates a pattern that starts red but gradually turns green as it progresses towards the end of the strip.
    // Gradients come in two types: Continuous, which wraps around the end of the strip, and Discontinuous, which has a sharp cutoff.
    // It generally doesn't matter all that much which one you pick unless you scroll it, which is what we'll be doing next.

    // The fun thing about LEDPatterns is that you can chain their effects, like so:
    LEDPattern pattern3 = pattern2.scrollAtRelativeSpeed(0.25);
    // Methods like scrollAtRelativeSpeed and scrollAtAbsoluteSpeed return a new pattern that applies the scrolling effect to the original pattern, so you can chain them together as much as you want.
    // This particular pattern will scroll pattern2's colors across the strip at a rate of a quarter of its length per second.
    // Slightly different is scrollAtAbsoluteSpeed, which scrolls at a set speed regardless of the length of the strip.

    // This is a pattern that is made up of steps instead of a smooth gradient.
    // Each step is a different solid color taking up some fraction of the whole strip.
    LEDPattern pattern4 = LEDPattern.steps(0, Color.kRed, 0.25, Color.kGreen, 0.5, Color.kBlue);
    // This will create a pattern that is red for the first quarter of the strip, green up to halfway, and then blue until the end.
}
