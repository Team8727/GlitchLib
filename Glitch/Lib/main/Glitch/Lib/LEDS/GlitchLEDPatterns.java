// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package Glitch.Lib.LEDS;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.util.Color;

import java.util.Map;

import static edu.wpi.first.units.Units.Microseconds;
import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;

/** 
 * This class contains all the premade LED patterns ever used by Team Glitch 2.0
 * Feel free to add more!
 */
public class GlitchLEDPatterns {

  /**
   * Solid purple pattern (2025)
   */
  public static final LEDPattern purple = LEDPattern.solid(Color.kPurple);
  
  /**
   * Rainbow pattern with a scrolling mask (2025) 
   * */
  public static final LEDPattern rainbow = LEDPattern.rainbow(
    256, 
    256)
    .scrollAtRelativeSpeed(
      Percent.per(Second).of(15))
      .reversed()
      .mask(
        LEDPattern.steps(
          Map.of(
              0.0, Color.kWhite,
              0.25, Color.kBlack,
              0.75, Color.kWhite))
      .scrollAtRelativeSpeed(
        Percent.per(Second).of(20)));

  /**
   * A blue to green scrolling gradient pattern (2025)
   */
  public static final LEDPattern blue =
      LEDPattern.gradient(LEDPattern.GradientType.kContinuous, Color.kBlue, Color.kGreen)
          .scrollAtRelativeSpeed(
            Percent.per(Second).of(15));

  /**
   * A purple to green scrolling gradient pattern (2025)
   * ...................................................
   * PRIDE MONTH WOOOOOOOOOOOOOOO
   */
  public static final LEDPattern ace =
      LEDPattern.gradient(GradientType.kContinuous, Color.kPurple, Color.kGreen)
          .scrollAtRelativeSpeed(
            Percent.per(Second).of(15));

  /**
   * Solid green pattern (2025).
   * 
   * This used to be our 2025 default pattern but I (Griffin) changed it to a fiery version of theCoolerGreen because that was WAY cooler.
   */
  public static final LEDPattern green = LEDPattern.solid(Color.kGreen);

  /**
   * Blinking green pattern (2025)
   */
  public static final LEDPattern blinkyGreen = LEDPattern.solid(Color.kGreen).blink(Second.of(0.1));
  
  /**
   * A slightly cooler scrolling green gradient pattern featured on our 2025 robot "Sponge"
   * all the name suggestions were equally bad that year
   * personally i think it should've been called Pelican but whatever
   */
  public static final LEDPattern theCoolerGreen = LEDPattern.gradient(
    GradientType.kDiscontinuous, 
    Color.kGreen,
    Color.kForestGreen,
    Color.kDarkGreen)
    .scrollAtRelativeSpeed(Percent.per(Second).of(25 * Math.sin(Math.random() * 3)));

    /**
     * A dark green to green gradient pattern (2025)
     */
  public static final LEDPattern darkGreen = LEDPattern.gradient(
    GradientType.kDiscontinuous,
    Color.kGreen,
    Color.kDarkGreen);

  /**
   * Creates and returns a linear progress bar overlay on top of the given pattern.
   * @param pattern The pattern the progress bar will overlay
   * @param currentProgress The current progress value (e.g., current height of the elevator)
   * @param maxProgress The maximum progress value (e.g., maximum height of the elevator)
   */
  public static LEDPattern linearProgress(LEDPattern pattern, double currentProgress, double maxProgress) {
    return pattern.mask(LEDPattern.progressMaskLayer(() -> currentProgress / maxProgress));
  }

  /**
   * Elevator progress pattern (2025)
   */
  public static final LEDPattern elevatorProgress = LEDPattern.gradient(
    GradientType.kDiscontinuous, 
    Color.kGreen, 
    Color.kYellow, 
    Color.kOrange, 
    Color.kRed);
  /**
   * Colral pickup pattern (2025)
   */
  public static final LEDPattern coralPickup = LEDPattern.gradient(
    GradientType.kDiscontinuous, 
    Color.kGreen, 
    Color.kPink, 
    Color.kYellow, 
    Color.kRed)
      .blink(Second.of(0.5));

  /**
   * Algae pickup pattern (2025)
   * 
   * this did not get used a lot because the algae arm kept breaking
   */
  public static final LEDPattern algaePickup = LEDPattern.gradient(
    GradientType.kDiscontinuous,
    Color.kGreen,
    Color.kPurple,
    Color.kOrange,
    Color.kRed)
      .blink(Second.of(0.5));

  /**
   * Classic orange-red fire pattern (2025)
   * 
   * Never actually used on the robot but it's cool so I'll leave it in.
   */
  public static final LEDPattern fire = LEDPattern.gradient(
    GradientType.kDiscontinuous, 
    Color.kWhite,
    Color.kYellow,
    Color.kOrange,
    Color.kRed);

  /*
   * Deeply experimental and untested Enzo patterns (2025)
   * 
   * These were designed with the 2025 robot in mind, so they may not look as good on other robots.
   * But if your robot also has two parallel vertical strips of LEDs, then go for it!
   */
  public static enum enzoMap {
    NORMAL(Map.of(
    0.0, Color.kBlack,
    0.08, Color.kGreen,
    0.48, Color.kWhite,
    0.56, Color.kBlack,
    0.72, Color.kWhite,
    0.80, Color.kGreen,
    0.92, Color.kBlack)),

    STARTLED(Map.of(
    0.0, Color.kBlack,
    0.08, Color.kGreen,
    0.32, Color.kWhite,
    0.48, Color.kBlack,
    0.64, Color.kWhite,
    0.80, Color.kGreen,
    0.92, Color.kBlack)),

    DISAPPOINTED(Map.of(
    0.0, Color.kBlack,
    0.08, Color.kGreen,
    0.48, Color.kWhite,
    0.56, Color.kBlack,
    0.72, Color.kGreen,
    0.92, Color.kBlack)),

    HAPPY(Map.of(
    0.0, Color.kBlack,
    0.08, Color.kGreen,
    0.56, Color.kBlack,
    0.72, Color.kWhite,
    0.80, Color.kGreen,
    0.92, Color.kBlack)),

    BLINKING(Map.of(
    0.0, Color.kBlack,
    0.08, Color.kGreen,
    0.92, Color.kBlack));

    private final Map<Number, Color> map;

    enzoMap(Map<Number, Color> map) {
      this.map = map;
    }

    /**
     * Returns the LEDPattern corresponding to the enzoMap.
     * @return The LEDPattern for the current enzoMap.
     */
    public LEDPattern getEnzoMap() {
      return LEDPattern.steps(this.map);
    }
  }

  // LEDPattern modifying methods is a mouthful!

  /**
   * This pattern creates a fire overlay that makes the given pattern look like it's made of fire. (2025)
   * @param pattern The pattern that the fire overlay applies to.
   * @param updateTime The time in seconds between updates of the fire overlay.
   */
  public static LEDPattern fire(LEDPattern pattern, double updateTime) {
    return (reader, writer) -> {

      double randomOffset = Math.random() * 1.33;
      long actualUpdateTime = (long) Seconds.of(updateTime).in(Microseconds);
      long updateLimit = (long) Seconds.of(0.039).in(Microseconds);

      if (RobotController.getTime() % actualUpdateTime < updateLimit 
          && RobotController.getTime() % actualUpdateTime > 0) {

        pattern.applyTo(reader, writer);

        reader.forEach( (index, red, green, blue) -> {
          if ((1.5 * (Math.sin(randomOffset - Math.random())) + (index / (double) reader.getLength())) > 1.275) {
            writer.setRGB(index, 0, 0, 0);
          } else {
            writer.setRGB(index, red, green, blue);
          }
        });
      }
    };
  }

  /**
   * This pattern creates a fire overlay that makes the given pattern look like it's made of fire. (2025)
   * @param pattern The pattern that the fire overlay applies to.
   */
  public static LEDPattern fire(LEDPattern pattern) {
    return fire(pattern, 0.07);
  }

  /** 
  * This method creates a fun random noise overlay pattern that took way too long to make. (2025)
  *
  * @param pattern The pattern that the random noise overlays.
  * @param updateTime The time in seconds between updates of the random noise overlay.
  * @return The random noise pattern.
  */
  public static LEDPattern randomNoise(LEDPattern pattern, double updateTime) {
    return (reader, writer) -> {

      AddressableLEDBuffer tempBuffer = new AddressableLEDBuffer(reader.getLength());
      pattern.applyTo(tempBuffer);

      int ledsOn = 0;
      long actualUpdateTime = (long) Seconds.of(updateTime).in(Microseconds);
      long updateLimit = (long) Seconds.of(0.039).in(Microseconds);

      if (RobotController.getTime() % actualUpdateTime < updateLimit 
          && RobotController.getTime() % actualUpdateTime > 0) {
            for (int i = 0; i < reader.getLength(); i ++) {
              if(!(reader.getRed(i) == 0 && reader.getGreen(i) == 0 && reader.getBlue(i) == 0)) {
                ledsOn += 1;
              }
            }
            
            if (ledsOn != 0) {

              double thisRandom = Math.random();

              reader.forEach((i, r, g, b) -> {
                if (i != 0 && i != reader.getLength() - 1 && i != reader.getLength()) {
                  Color left = reader.getLED(i-1);
                  Color mid = reader.getLED(i);
                  Color right = reader.getLED(i+1);

                  Color avg = new Color(
                    (reader.getRed(i-1) + reader.getRed(i) + reader.getRed(i+1)) / 3,
                    (reader.getGreen(i-1) + reader.getGreen(i) + reader.getGreen(i+1)) / 3,
                    (reader.getBlue(i-1) + reader.getBlue(i) + reader.getBlue(i+1)) / 3
                  );

                  int population = 0;

                  if (!left.equals(Color.kBlack)) {
                    population += 1;
                  }
                  if (!mid.equals(Color.kBlack)) {
                    population += 2;
                  }
                  if (!right.equals(Color.kBlack)) {
                    population += 4;
                  }

                  if (population == 3 || population == 1 || population == 6 || population == 4) {
                    writer.setRGB(i, tempBuffer.getRed(i), tempBuffer.getGreen(i), tempBuffer.getBlue(i));
                  } else if (population == 0 || population == 7 || population == 5 || population == 2) {
                    writer.setRGB(i, 0, 0, 0);
                  } else {
                    writer.setRGB(i, (int) avg.red*256, (int) avg.green*256, (int) avg.blue*256);
                  }
                }
              });

              writer.setRGB((int)(thisRandom * reader.getLength()), 
                tempBuffer.getRed((int)(thisRandom * reader.getLength())), 
                tempBuffer.getGreen((int)(thisRandom * reader.getLength())), 
                tempBuffer.getBlue((int)(thisRandom * reader.getLength())));
                
            } else {
              pattern.applyTo(reader, writer);
            } 
            // TODO: Make sure that this works the way you want it and change the default pattern back to normal once you're done
          }
    };
  }

  /**
   * This method creates a fun random noise overlay pattern that took way too long to make. (2025)
   * @param pattern The pattern that the random noise overlays.
   * @return The random noise pattern.
   */
  public static LEDPattern randomNoise(LEDPattern pattern) {
    return randomNoise(pattern, 0.05);
  }
}
