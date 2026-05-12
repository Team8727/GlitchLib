import static edu.wpi.first.units.Units.Seconds;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Robot;

public class LEDPatterns {

    // LEDPatterns, generally speaking, shouldn't require their own class UNLESS you are using an interface like LEDArrayPattern or TwoDArrayPattern.
    // We're going to start with the basics first though, the tools that WPILib provides themselves.
    // For a better and more in-depth explanation, click on the link here: https://docs.wpilib.org/en/stable/docs/software/hardware-apis/misc/addressable-leds.html

    // The most basic pattern is a solid color.
    LEDPattern solidRed = LEDPattern.solid(Color.kRed);
    // This creates a pattern that is completely red, no matter how long the strip is.

    // A little more complicated is the gradient pattern.
    LEDPattern redToGreen = LEDPattern.gradient(GradientType.kContinuous, Color.kRed, Color.kGreen);
    // This creates a pattern that starts red but gradually turns green as it progresses towards the end of the strip.
    // Gradients come in two types: Continuous, which wraps around the end of the strip, and Discontinuous, which has a sharp cutoff.
    // It generally doesn't matter all that much which one you pick unless you scroll it, which is what we'll be doing next.

    // The fun thing about LEDPatterns is that you can chain their effects, like so:
    LEDPattern scrolling = redToGreen.scrollAtRelativeSpeed(0.25);
    // Methods like scrollAtRelativeSpeed and scrollAtAbsoluteSpeed return a new pattern that applies the scrolling effect to the original pattern, so you can chain them together as much as you want.
    // This particular pattern will scroll redToGreen's colors across the strip at a rate of a quarter of its length per second.
    // Slightly different is scrollAtAbsoluteSpeed, which scrolls at a set speed regardless of the length of the strip.

    // This is a pattern that is made up of steps instead of a smooth gradient.
    // Each step is a different solid color taking up some fraction of the whole strip.
    LEDPattern redGreenAndBlue = LEDPattern.steps(0, Color.kRed, 0.25, Color.kGreen, 0.5, Color.kBlue);
    // This will create a pattern that is red for the first quarter of the strip, green up to halfway, and then blue until the end.

    // Then we have the progress mask.
    LEDPattern halfLit = LEDPattern.progressMaskLayer(0.5);
    // Basically the way this works is that you supply it with some decimal value between zero and one (generally corresponding to, say, the progress of a linear mechanism)
    // and it will generate a white and black pattern whose length of lit LEDs corresponds to the fraction you supplied.
    // This is generally used in combination with a mask.

    // Any pattern can be reversed, like so:
    LEDPattern reversedRedToGreen = redToGreen.reversed();

    // Any pattern can also be offset, like so:
    LEDPattern offsetRedToGreen = redToGreen.offsetBy(2);
    // Which basically just shifts the "starting position" of the pattern by the number of LEDs indicated, which can also be negative and wraps around the strip.

    // You can also make patterns breathe, which just periodially dims and then brightens it over a set sinusoidal interval.
    LEDPattern pulsingRedToGreen = redToGreen.breathe(Seconds.of(2));
    // Note: the breathe modifier cannot make a pattern brighter than it started.

    // Alternatively you can make the pattern blink, which can be manipulated in a number of ways.
    LEDPattern quickBlink = redToGreen.blink(Seconds.of(0.5));
    LEDPattern slowBlink = redToGreen.blink(Seconds.of(0.5), Seconds.of(1));
    LEDPattern realBlink = redToGreen.synchronizedBlink(Robot.isReal());
    // quickBlink will switch from on to off and vice versa every half second.
    // slowBlink will turn on for half a second and then turn off for a full second.
    // realBlink only turns on if the Robot is not in simulation mode.
    // ...Not really the best example. But basically it takes in a boolean value and only turns on if that value is currently true.

    // Masks are strange and I barely understand them.
    LEDPattern maskedRedToGreen = redToGreen.mask(halfLit);
    // This should, in theory, show only the half of redToGreen that is illuminated in halfLit.
    // The reasons for this are strange.
    // As far as I can tell, mask only shows the RGB values shared by both patterns. So if halfLit were instead solid orange or something, then only the red parts of redToGreen would show.
    // Overlay masks are slightly simpler.
    LEDPattern halfLitHalfGreen = halfLit.overlayOn(redToGreen);
    // The way overlay works is that any color that isn't black on the top pattern (pattern overlayOn is being called to) will override anything on the base pattern. (parameter of overlayOn)
    // But any black LEDs will be basically transparent, and allow the colors from the lower pattern to pass through.

    // Whew. That was a lot.
    // You may be thinking to yourself: "That's both a lot of features and also way less than I thought there would be."
    // And you would be right!
    // This is far from all you can do with LEDPatterns.
    // To pull off the really interesting stuff, we have to either use interfaces or reader writer lambda expressions.

    // We'll start with interfaces for now.

    // While you can create your own custom interface, we have already made a basic framework that should be sufficient for all of your LED needs.

    // This is likely what you'll be looking at when using the interface LEDArrayPattern

    public class MyPattern implements LEDArrayPattern {

        public ArrayList<Color> colorList(ArrayList<Color> colorList) {
            for (int i = 0; i < colorList.size(); i++) {
                int b = (colorList.get(i).blue + 1) % 255;
                colorList.set(i, new Color(i, 255 - i, b));
            }
            return colorList;
        }
    }

    // So. What does any of that actually mean?
    // Since LEDArrayPattern is an interface, it has a method called colorList that basically just has a return type and a parameter.
    // It is your job to figure out what to do with that parameter inside of the method.
    // You need to create the method for any of this to work.
    // The reason why the method is important is because LEDArrayPattern will take whatever the result from calling colorList is and then make it into an LEDPattern.
    // So now we need to talk about what you're actually working with here.
    // colorList both returns and takes as parameter the type ArrayList<Color>
    // ArrayList<Color> is, somewhat unsurprisingly, a list of the type Color.
    // The type Color can be created using numerous methods that I highly recommend you explore and tinker with on your own but for our purposes we will use the RGB 0-255 scheme.
    // The ArrayList<Color> colorList is a stand-in for the digital LED strip, which is also basically just a list of Color.
    // So what the current MyPattern class is doing is that when it is supplied to a Section it iterates over every LED in that Section and sets its red value equal to the
    // index of the LED in that Section, the green value equal to the index relative to the end of that Section, and the blue value equal to whatever it was before plus one.
    // Unless, of course, that results in a blue value higher than 255, in which case it will return to zero due to the modulus operator, which returns the remainder of dividing
    // b + 1 / 255, which will always be equal to b + 1 unless b + 1 is greater than zero, in which case it is equal to b + 1 - 255.
    // In theory this will give us some lovely blending between red from the left, green from the right, and blue everywhere.
    // But to actually use it you'll need to create a new instance of MyPattern just like any other class, and like magic that instance can be treated just like a normal LEDPattern.

    // Alright. So that's the VERY bare bones basics of how interfaces work, but I did mention something about lambdas?
    // Indeed I did. It's significantly less scary than it sounds, don't worry.

    // A fun thing that took me a lot of scrounging around in the actual LEDPattern class to figure out is that LEDPattern is basically two different classes smushed together,
    // those being named LEDReader and LEDWriter.
    // LEDReader is the last state of the pattern, and LEDWriter is what the next state will be.
    // When you combine those things together, you get one whole pattern.
    // But how do you use this, exactly?
    // Great question!

    LEDPattern examplePattern = (reader, writer) -> {
        reader.forEach((i, r, g, b) -> {
            writer.setRGB(i, i, 255 - i, (b + 1) % 255);
        });
    };

    // This is a recreation of what the interface pattern I showed you earlier would look like in the (reader, writer) format.
    // Let's walk through the steps, shall we?
    // First, this only works if the variable type is LEDPattern, or else Java is going to have no clue what (reader, writer) could possibly mean.
    // Next, the (reader, writer) -> {} establishes that we are running a function based off of the reader writer properties of the pattern itself.
    // Then, I used reader.forEach((i, r, g, b) -> {}) instead of a normal for loop because 1) Its faster to write and 2) r, g, and b are the current
    // RGB values for the LED at index i! So the format takes a lot of the tedium of grabbing RGB values from the current LED out of the equation.
    // Once inside the second lambda expression, I used writer.setRGB(i, r, g, b) to directly set the RGB values of the current LED, 
    // though it doesn't limit you to that.
    // That done, I just recreated the same logic as before and BAM! LEDPattern!
    // You can even do this in method form!

    LEDPattern methodPattern(int blue) {
        return (reader, writer) -> {
            reader.forEach((i, r, g, b) -> {
                writer.setRGB(i, i, 255 - i, (b + blue) % 255);
            });
        };
    }

    // The best part is that you can actually make these patterns static, so you don't have to create new instances just to run them and they can be used everywhere!
    // public static LEDPattern methodPattern(int blue) {
    //    return (reader, writer) -> {reader.forEach((i, r, g, b) -> {writer.setRGB(i, i, 255 - i, (b + blue) % 255);});};
    //}
    // This would work I just don't want to make it uncommented because really all the static LEDPatterns should be stored in GlitchLEDPatterns and not here in sample code.

    // But that should be everything!
    // All the other fine details, things like special logic and interface methods, are things that you really don't need to worry about for LEDs.
    // Unless you really wanna get into the weeds, like I did, and use derivative calculus to figure out whether or not a flame is rising or falling at a given moment.

    // But yeah! Have fun out there!
    // Oh, and never forget to check in with the driveteam frequently to make sure you're actually doing what they want you to do.
    // They are the ones you're making this stuff for, after all.
}