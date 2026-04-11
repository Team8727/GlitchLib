// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package Glitch.Lib.LEDs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.util.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

import static edu.wpi.first.units.Units.*;

/** 
 * This class contains all the premade LED patterns ever used by Team Glitch 2.0 from 2025 onwards.
 * Feel free to add more!
 */
public class GlitchLEDPatterns {

  /**
   * Solid purple pattern (2025)
   */
  public static final LEDPattern purple = LEDPattern.solid(GlitchColors.purple);
  
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
      LEDPattern.gradient(GradientType.kContinuous, GlitchColors.purple, Color.kGreen)
          .scrollAtRelativeSpeed(
            Percent.per(Second).of(15));

  /**
   * The sunset aro-ace flag because it looks lovely (2026)
   */
  public static final LEDPattern sunsetAce = 
    LEDPattern.steps(
      Map.of(
        0.0, Color.kOrangeRed,
        0.2, Color.kOrange,
        // 0.4, Color.kWhiteSmoke, //Note: The strip hates the color green. Maybe try a weird purple instead.
        0.4, new Color(0.75, 6, 4),
        0.6, Color.kSkyBlue,
        0.8, GlitchColors.darkBlue
      ));

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
   * Coral pickup pattern (2025)
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
    GlitchColors.purple,
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

    /**
     * Looks really neat when paired with the 2026 fire pattern (2026)
     */
  public static final LEDPattern funGradient = LEDPattern.gradient(
    GradientType.kContinuous, 
    Color.kRed, 
    Color.kOrange, 
    Color.kYellow, 
    Color.kGreen, 
    Color.kBlue, 
    GlitchColors.purple)
      .scrollAtRelativeSpeed(Percent.per(Second).of(10));

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
  public static LEDPattern oldFire(LEDPattern pattern, double updateTime) {
    return (reader, writer) -> {

      AddressableLEDBuffer tempBuffer = new AddressableLEDBuffer(reader.getLength());
      pattern.applyTo(tempBuffer);

      double randomOffset = (Math.random() * Math.PI * 2) + 5;
      long actualUpdateTime = (long) Seconds.of(updateTime).in(Microseconds);
      long updateLimit = (long) Seconds.of(0.039).in(Microseconds);
      long robotTime = RobotController.getTime();

      if (robotTime % actualUpdateTime < updateLimit 
          && robotTime % actualUpdateTime > 0) {

        reader.forEach( (index, red, green, blue) -> {
          if ((1.5 * (Math.sin(randomOffset - Math.random())) + (index / (double) reader.getLength())) > 1.275) {
            writer.setRGB(index, 0, 0, 0);
          } else {
            writer.setRGB(index, tempBuffer.getRed(index), tempBuffer.getGreen(index), tempBuffer.getBlue(index));
          }
        });
      }
    };
  }

  /**
   * This pattern creates a fire overlay that makes the given pattern look like it's made of fire. (2025)
   * @param pattern The pattern that the fire overlay applies to.
   */
  public static LEDPattern oldFire(LEDPattern pattern) {
    return oldFire(pattern, 0.11);
  }

  /**
   * This pattern creates a significantly more complex fire overlay than the previous method, 
   * and it also allows you to shift the color towards Red, Green, or Blue toward the flame's tip. (2026)
   * @param pattern The pattern the fire overlays.
   * @param updateTime The time between updates of the overlay in seconds.
   * @param shiftColor The color the fire shifts towards (putting in a color other than red, green, or blue shifts it to gray).
   * @param speed How fast the wave function of the flame moves (higher is faster, lower is slower, 1 is normal).
   * @return The fire pattern.
   */
  public static LEDPattern fire(LEDPattern pattern, double updateTime, Color shiftColor, double speed) {
    return (reader, writer) -> {

      AddressableLEDBuffer tempBuffer = new AddressableLEDBuffer(reader.getLength());
      pattern.applyTo(tempBuffer);

      // double randomOffset = 0.3 + (Math.random()/(1/0.3));
      double randomOffset = 0.45 + (0.005 - Math.random() * 0.01);
      long actualUpdateTime = (long) Seconds.of(updateTime).in(Microseconds);
      long updateLimit = (long) Seconds.of(0.039).in(Microseconds);
      long robotTime = RobotController.getTime();
      double usefulTime = speed * Math.asin(Math.sin((double) RobotController.getTime() / 1000000));

      if (robotTime % actualUpdateTime < updateLimit 
          && robotTime % actualUpdateTime > 0) {

        reader.forEach( (index, red, green, blue) -> {

          int r;
          int g;
          int b;

          // int r = tempBuffer.getRed(index);
          // int g = tempBuffer.getGreen(index);
          // int b = tempBuffer.getBlue(index);

          int realRed = reader.getRed(index);
          int realGreen = reader.getGreen(index);
          int realBlue = reader.getBlue(index);

          double fadeConstant = 10;

          if (realRed < tempBuffer.getRed(index) / Math.pow(fadeConstant, 2)) {
            realRed = 0;
            r = tempBuffer.getRed(index);
          } else {
            r = (tempBuffer.getRed(index) + (9*realRed)) /10;
          }
          if (realGreen < tempBuffer.getGreen(index) / Math.pow(fadeConstant, 2)) {
            realGreen = 0;
            g = tempBuffer.getGreen(index);
          } else {
            g = (tempBuffer.getGreen(index) + (9*realGreen)) / 10;
          }
          if (realBlue < tempBuffer.getBlue(index) / Math.pow(fadeConstant, 2)) {
            realBlue = 0;
            b = tempBuffer.getBlue(index);
          } else {
            b = (tempBuffer.getBlue(index) + (9*realBlue)) / 10;
          }

          if (r < 10) {
            r = 0;
          }
          if (g < 10) {
            g = 0;
          }
          if (b < 10) {
            b = 0;
          }
          
          if (shiftColor != null) {
            if (shiftColor == Color.kRed) {
              g = (int) Math.min(g, (0.5 * reader.getLength() * g) / (index + 1));
              b = (int) Math.min(b, (0.5 * reader.getLength() * b) / (index + 1));
            } else if (shiftColor == Color.kGreen) {
              r = (int) Math.min(r, (0.5 * reader.getLength() * r) / (index + 1));
              b = (int) Math.min(b, (0.5 * reader.getLength() * b) / (index + 1));
            } else if (shiftColor == Color.kBlue) {
              r = (int) Math.min(r, (0.5 * reader.getLength() * r) / (index + 1));
              g = (int) Math.min(g, (0.5 * reader.getLength() * g) / (index + 1));
            } else {
              r = (int) Math.min(r, (0.8 * reader.getLength() * r) / (index + 1));
              g = (int) Math.min(g, (0.8 * reader.getLength() * g) / (index + 1));
              b = (int) Math.min(b, (0.8 * reader.getLength() * b) / (index + 1));
            }
          }

          boolean isLit = false;
          boolean nearLow = false;
          boolean nearHigh = false;
          boolean midLow = false;
          boolean midHigh = false;
          boolean farLow = false;
          boolean farHigh = false;
          
          double flame = ((Math.sin(usefulTime) 
              + Math.sin(usefulTime / randomOffset) 
              + Math.pow(Math.sin(usefulTime), 2) 
              + 3*(Math.pow(Math.sin(usefulTime / randomOffset), 2))
              + (usefulTime / 6)
              + 1) 
              * reader.getLength() / 8) + 0.1;

          double energy = Math.cos(usefulTime) 
                        + (Math.cos(usefulTime / randomOffset) * 1/randomOffset) 
                        + (2 * Math.sin(usefulTime) * Math.cos(usefulTime))
                        + (6 * Math.sin(usefulTime / randomOffset) * Math.cos(usefulTime / randomOffset) * 1/randomOffset)
                        + 1/6;

          if (r + g + b != 0) {
            isLit = true;
          }
          if (index < reader.getLength() - 1 && reader.getRed(index+1) + reader.getGreen(index+1) + reader.getBlue(index+1) != 0) {
            nearHigh = true;
          }
          if (index < reader.getLength() - 2 && reader.getRed(index+2) + reader.getGreen(index+2) + reader.getBlue(index+2) != 0) {
            midHigh = true;
          }
          if (index < reader.getLength() - 3 && reader.getRed(index+3) + reader.getGreen(index+3) + reader.getBlue(index+3) != 0) {
            farHigh = true;
          }
          if (index > 2 && (reader.getRed(index-1) + reader.getGreen(index-1) + reader.getBlue(index-1)) != 0) {
            nearLow = true;
          }
          if (index > 3 && (reader.getRed(index-2) + reader.getGreen(index-2) + reader.getBlue(index-2)) != 0) {
            midLow = true;
          }
          if (index > 4 && (reader.getRed(index-3) + reader.getGreen(index-3) + reader.getBlue(index-3)) != 0) {
            farLow = true;
          }

          if (index < flame) {
            if (energy > 0 && Math.random() < 0.8) {
              writer.setRGB(index, r, g, b);
            } else if (energy > 0) {
              // writer.setRGB(index, 0, 0, 0);
              writer.setRGB(index, (int) (r/fadeConstant), (int) (g/fadeConstant), (int) (b/fadeConstant));
            } else if (energy < 0 && Math.random() < 0.9) {
              writer.setRGB(index, r, g, b);
            } else {
              // writer.setRGB(index, 0, 0, 0);
              writer.setRGB(index, (int) (r/fadeConstant), (int) (g/fadeConstant), (int) (b/fadeConstant));
            }
          }/*this is when it is outside the main flame body*/ else if (energy > 0 && Math.random() > 0.5 
              && ((!isLit && (nearHigh || midHigh || farHigh))
              || (isLit && (nearLow && midLow && farLow)))) {
            writer.setRGB(index, r, g, b);
          } else if (energy > 0) {
            // writer.setRGB(index, 0, 0, 0);
            writer.setRGB(index, (int) (r/fadeConstant), (int) (g/fadeConstant), (int) (b/fadeConstant));
          } else if (energy < 0 && Math.random() * reader.getLength() * 2 < flame) {
            writer.setRGB(index, r, g, b);
          } else {
            // writer.setRGB(index, 0, 0, 0);
            writer.setRGB(index, (int) (r/fadeConstant), (int) (g/fadeConstant), (int) (b/fadeConstant));
          }
        });
      }
    };
  }

  /**
   * This pattern creates a significantly more complex fire overlay than the previous method. (2026)
   * @param pattern The pattern the fire overlays.
   * @param updateTime The time between updates of the overlay in seconds.
   * @return The fire pattern.
   */
  public static LEDPattern fire(LEDPattern pattern, double updateTime) {
    return fire(pattern, updateTime, null, 0.5);
  }

  /**
   * This pattern creates a significantly more complex fire overlay than the previous method, 
   * and it also allows you to shift the color towards Red, Green, or Blue toward the flame's tip. (2026)
   * @param pattern The pattern the fire overlays.
   * @param shiftColor The color the fire shifts towards (putting in a color other than red, green, or blue shifts it to gray).
   * @return The fire pattern.
   */
  public static LEDPattern fire(LEDPattern pattern, Color shiftColor) {
    return fire(pattern, 0.11, shiftColor, 0.5);
  }

  /**
   * This pattern creates a significantly more complex fire overlay than the previous method. (2026)
   * @param pattern The pattern the fire overlays.
   * @return The fire pattern.
   */
  public static LEDPattern fire(LEDPattern pattern) {
    return fire(pattern, 0.11, null, 0.5);
  }

  /** 
  * This method creates a fun random noise overlay pattern utilizing cellular automata that took way too long to make. (2026)
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

  /**
   * Creates a overlay pattern that kinda looks like ripples moving through water. (2026)
   * @param pattern The pattern the ripples overlay.
   * @param updateTime The time between updates of the overlay in seconds.
   * @param frequency The rate at which the ripples move.
   * @param wavelength The size of an individual ripple and the distance between them. (Larger numbers mean smaller ripples)
   * @return The ripple pattern.
   */
  public static LEDPattern ripple(LEDPattern pattern, double updateTime, double frequency, double wavelength) {

    double randomOffset = Math.random() * 100;

    return (reader, writer) -> {

      AddressableLEDBuffer tempBuffer = new AddressableLEDBuffer(reader.getLength());
      pattern.applyTo(tempBuffer);

      long actualUpdateTime = (long) Seconds.of(updateTime).in(Microseconds);
      long updateLimit = (long) Seconds.of(0.039).in(Microseconds);

      double usefulTime = (double) ((frequency * RobotController.getTime()) / 1000000);

      double multiplier = tempBuffer.getLength() / wavelength;

      if (RobotController.getTime() % actualUpdateTime < updateLimit 
          && RobotController.getTime() % actualUpdateTime > 0) {
            reader.forEach((i, red, green, blue) -> {

              int r = tempBuffer.getRed(i);
              int g = tempBuffer.getGreen(i);
              int b = tempBuffer.getBlue(i);

              if (Math.sin(((((i * Math.PI / 6) + (usefulTime * Math.PI / 12))) / multiplier) + randomOffset) >= 0.8) {
                writer.setRGB(i, r, g, b);
              } else if (Math.sin(((((i * Math.PI / 6) - (usefulTime * Math.PI / 12))) / multiplier) + randomOffset) >= 0.8) {
                writer.setRGB(i, r, g, b);
              } else {
                writer.setRGB(i, 0, 0, 0);
              }
            });
          }
    };
  }

  /**
   * Creates a overlay pattern that kinda looks like ripples moving through water. (2026)
   * @param pattern The pattern the ripples overlay.
   * @param frequency The rate at which the ripples move.
   * @param wavelength The size of an individual ripple and the distance between them.
   * @return The ripple pattern.
   */
  public static LEDPattern ripple(LEDPattern pattern, double frequency, double wavelength) {
    return ripple(pattern, 0.11, frequency, wavelength);
  }

  /**
   * Creates a overlay pattern that kinda looks like ripples moving through water. (2026)
   * @param pattern The pattern the ripples overlay.
   * @return The ripple pattern.
   */
  public static LEDPattern ripple(LEDPattern pattern) {
    return ripple(pattern, 0.11, 7, 14);
  }

  /**
   * Creates a pattern that should look like raindrops. (2026) DO NOT USE YET IT ISN'T READY
   * @param pattern The pattern the ripples overlay.
   * @param intensity The speed at which new ripples form (cannot be negative or zero).
   * @param impact The maximum size of each ripple before it disappears.
   * @return The pattern of raindrop ripples.
   */
  public static LEDPattern rainDrops(LEDPattern pattern, int intensity, int impact) {

    long actualUpdateTime = (long) Seconds.of(0.21).in(Microseconds);
    long updateLimit = (long) Seconds.of(0.039).in(Microseconds);

    List<Integer> dropIndices = new ArrayList<>(0);
    List<Long> dropDurations = new ArrayList<>(0);
    List<Double> dropLuminosities = new ArrayList<>(0);

    DoubleUnaryOperator abstractedMathFunction = (x) -> 
      Math.cos((x/impact)/2*Math.PI) 
      - Math.cos(0.5);

    DoubleUnaryOperator overallBehaviorFunction = (x) ->
      (abstractedMathFunction.applyAsDouble(Math.PI * x) / abstractedMathFunction.applyAsDouble(0.0));

    return (reader, writer) -> {
      AddressableLEDBuffer tempBuffer = new AddressableLEDBuffer(reader.getLength());
      pattern.applyTo(tempBuffer);

      if (dropDurations.size() != reader.getLength()) {
        reader.forEach((i, r, g, b) -> {
          dropDurations.add(AbstractLEDS.getTime());
        });
      }
      if (dropLuminosities.size() != reader.getLength()) {
        reader.forEach((i, r, g, b) -> {
          dropLuminosities.add(0.0);
        });
      } else {
        reader.forEach((i, r, g, b) -> {
          dropLuminosities.set(i, 0.0);
        });
      }

      if (RobotController.getTime() % actualUpdateTime < updateLimit 
          && RobotController.getTime() % actualUpdateTime > 0) {

        for (int indexIterator = 0; indexIterator < dropIndices.size(); indexIterator++) {
          for (int i = (impact * -1); i < impact + 1; i++) {
            int dropIndex = dropIndices.get(indexIterator);
            int ledIndex;

            if (dropIndex + i < 0) {
              ledIndex = dropIndex + i + reader.getLength();
            } else if (dropIndex + i >= reader.getLength()) {
              ledIndex = (dropIndex + i) - reader.getLength();
            } else {
              ledIndex = dropIndex + i;
            }

            double durationTime = (2 * impact) - Microseconds.of(AbstractLEDS.getTime() - dropDurations.get(ledIndex)).in(Seconds);

            double calcLuminosity;

            if (dropDurations.get(ledIndex) + Seconds.of(Math.abs(i)).in(Microseconds) <= AbstractLEDS.getTime() || i == 0) {
              calcLuminosity = overallBehaviorFunction.applyAsDouble((durationTime * (Math.abs(i) + 1)) - impact) * overallBehaviorFunction.applyAsDouble(i) / 1 + Math.abs(i);
            } else {
              calcLuminosity = 0.0;
            }

            dropLuminosities.set(ledIndex, Math.min(1.0, dropLuminosities.get(ledIndex) + calcLuminosity));

            if (dropLuminosities.get(ledIndex) == 1.0 && !dropIndices.contains(ledIndex)) {
              dropIndices.add(ledIndex);
              dropDurations.set(ledIndex, (long) (AbstractLEDS.getTime() - Seconds.of(3 * intensity).in(Microseconds)));
            }
          }

          if (AbstractLEDS.getTime() - dropDurations.get(dropIndices.get(indexIterator)) >= Seconds.of(2.0 * impact).in(Microseconds)) {
            dropIndices.remove(indexIterator);
          }
        }

        reader.forEach((i, red, green, blue) -> {
          if ((int) (Math.random() * reader.getLength()) == i && Math.random() * 10 < intensity) {
            writer.setRGB(i, tempBuffer.getRed(i), tempBuffer.getGreen(i), tempBuffer.getBlue(i));
                if (!dropIndices.contains(i)) {
                  dropIndices.add(i);
                }
                dropDurations.set(i, AbstractLEDS.getTime());
          }

          writer.setRGB(i, 
            (int) (tempBuffer.getRed(i) * dropLuminosities.get(i)), 
            (int) (tempBuffer.getGreen(i) * dropLuminosities.get(i)), 
            (int) (tempBuffer.getBlue(i) * dropLuminosities.get(i)));
        });

        // for (int i = 0; i < dropIndices.size(); i++) {
        //   writer.setRGB(dropIndices.get(i), 255, 255, 255);
        // }
      }
    };
  }
}
