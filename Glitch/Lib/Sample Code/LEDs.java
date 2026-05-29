// Here we will walk you through the basics of LED code with GlitchLib!

// Normally we would have a package declaration up here but since this is just a sample code file we will leave it out for now.

// AbstractLEDS is the base class for all LED subsystems in GlitchLib.
// It contains all the code necessary for handling patterns, Sections, and updating the LED strip.
import Glitch.Lib.LEDs.AbstractLEDS;

// GlitchLEDPatterns is a class that contains a bunch of pre-made LED patterns that you can use.
// All of the patterns in GlitchLEDPatterns are static, so you can use them without creating a new instance of GlitchLEDPatterns.
import Glitch.Lib.LEDs.GlitchLEDPatterns;

// Here is the class declaration for a LED subsystem that extends AbstractLEDS.
public class LEDs extends AbstractLEDS {

    // We are creating our Section variable outside of the constructor so we can use it throughout the whole class.
    // Section is just a fancy version of AddressableLEDBufferView that allows us to give it some extra properties like a base pattern and a duration for patterns.
    Section ledStrip;

    // Here is where we will put the class constructor. We will initialize the LED strip and set up the sections here.
    private LEDs() {
        // The first number inside of this super constructor is the length of the whole LED strip.
        // The second number is the lengths of any Sections you want to create. In this case we are creating one Section that is the length of the whole strip.
        // You can also create Sections that are in reverse by giving them a negative length.
        super(100, 100);

        // To set our Section variable, we have to first get it from the list of Sections that we provided to AbstractLEDS in the constructor.
        ledStrip = getSections().get(0);
        // Then, we can give it a base pattern. The base pattern of a Section is an LEDPattern that the Section will return to after other patterns end.
        ledStrip.setBase(GlitchLEDPatterns.theCoolerGreen);
        

        // Final step of the constructor. We need to actually initialize the digital LED strip we have created to the physical strip connected to a PWM port.
        // Normally I put this in Robot (using an instance of LEDs instead of the super method), but for the sake of simplicity we will just put it here.
        super.initializeLEDS(0);
    }

    // This method will prevent multiple instances of the LED subsystem from being created and will allow us to access the subsystem from other classes.
    // This is necessary for LED subsystems because if you don't do this and you try to create more than one instance of LEDs, then the code will crash.
    // For whatever reason WPILib just doesn't allow more than one initialized LED strip to exist at once.
    private static LEDs instance;

    // For the reasons stated above, you'll want to call this instead of the normal class constructor when grabbing an instance of the LED subsystem from Robot or other classes.
    public static LEDs getInstance() {
        if (instance == null) {
            instance = new LEDs();
        }
        return instance;
    }

    // Subsystems have periodic methods where you put all the stuff that needs to be done every cycle of the code.
    // Fortunately for you, all the business of updating the LED strip with the most recent patterns is already handled by AbstractLEDs,
    // so you can actually just delete the entire periodic method, @Override included, and the code will still work perfectly fine.
    // But if you really need to add some extra logic that needs to run every loop, you'll need to include super.periodic() in your periodic method.
    @Override
    public void periodic() {
        super.periodic();
    }
}

// To set a new LEDPattern to a Section, you simply use the setPattern() method.
// For example, if we wanted to set the pattern of our ledStrip Section to a solid red color for 2 seconds, we would do this:
        // ledStrip.setPattern(LEDPattern.solid(Color.kRed), 2);
// If you don't specify a duration for the pattern, it will just stay on forever until you change it. So this would work for an infinite duration:
        // ledStrip.setPattern(LEDPattern.solid(Color.kRed));