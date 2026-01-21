// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package Glitch.Lib;

import java.util.List;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.AddressableLEDBufferView;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class LEDS extends SubsystemBase {
  private final AddressableLED lightStrip;
  public final AddressableLEDBuffer stripBuffer;
  private final List<Section> sectionList;

  public static final double infiniteDurationSeconds = -1.0;

  /**
   * Represents a section of the LED strip with a specific pattern and duration.
   */
  public class Section {
    private final AddressableLEDBufferView bufferView;
    private LEDPattern pattern = LEDPattern.kOff;

    private static final double infiniteDurationSeconds = -1.0;
    private double durationSeconds = infiniteDurationSeconds;
    private double elapsedSeconds = 0.0;

    private LEDPattern basePattern;

    private Section(int startIndex, int endIndex) {
      bufferView = stripBuffer.createView(startIndex, endIndex);
    }

    /**
     * Sets the pattern for a duration in seconds.
     * @param pattern The pattern to set.
     * @param durationSeconds The duration in seconds to set the pattern to before setting the strip to the default pattern.
     */
    public void setPattern(LEDPattern pattern, double durationSeconds) {
      this.pattern = pattern;
      this.durationSeconds = durationSeconds;
      this.elapsedSeconds = 0.0;
    }

    /**
     * Sets the pattern for an infinite duration.
     * @param pattern The pattern to set.
     */
    public void setPattern(LEDPattern pattern) {
      setPattern(pattern, infiniteDurationSeconds);
    }

    /**
     * Sets the pattern that the section will return once a timed pattern has completed.
     * @param pattern
     */
    public void setBase(LEDPattern pattern) {
      if (pattern == null) {
        pattern = LEDPattern.kOff;
      } else {
        this.basePattern = pattern;
      }
    }

    /**
     * Updates the section's pattern if it has a finite duration.
     * Applies the current pattern to the buffer view.
     * 
     * @param deltaTimeSeconds The time since the last update in seconds.
     * @param animPattern The pattern that the animation will overlay.
     * @param animBool A boolean that decides if the animation plays on the section.
     */

    public void update(double deltaTimeSeconds) {
      if (durationSeconds != infiniteDurationSeconds) {
        elapsedSeconds += deltaTimeSeconds;
        if (elapsedSeconds >= durationSeconds) {
          Section.this.setPattern(basePattern);
        }
      }

      pattern.applyTo(this.bufferView);
    }

    /**
     * Returns the buffer view for this section.
     * @return
     */
    public AddressableLEDBufferView getBufferView() {
      return this.bufferView;
    }

    /**
     * Returns the length of this section.
     * @return
     */
    public int getLength() {
      return this.bufferView.getLength();
    }
  }
  
  /** Creates a new LEDSubsystem.
   * 
   * @param port The PWM port the LED strip is connected to.
   * @param length The total number of LEDs in the strip.
   * @param sectionLengths The lengths of each section in the strip. Positive values indicate normal order, negative values indicate reversed order.
   * The sections will be created in the order they are provided.
   */
  protected LEDS(int port, int length, int... sectionLengths) {
    // LED setup and port configuration
    lightStrip = new AddressableLED(port);
    stripBuffer = new AddressableLEDBuffer(length);
    sectionList = new java.util.ArrayList<Section>();

    int index = 0;
  
    for (int sectionLength: sectionLengths) {
      if (sectionLength == 0) {
        throw new IllegalArgumentException("You can't have a section length of 0.");
      }
      if ((index += sectionLength) > length || (index -= sectionLength) > length) {
        throw new IllegalArgumentException("Section lengths exceed total strip length.");
      }
      if (sectionLength < 0) {
        sectionList.add(new Section(index - sectionLength - 1, index));
        index -= sectionLength;
      } else {
        sectionList.add(new Section(index, index + sectionLength - 1));
        index += sectionLength;
      }
    }

    lightStrip.setLength(stripBuffer.getLength());

    lightStrip.setData(stripBuffer);
    lightStrip.start();
  }

  /**
   * Gets the list of sections in the LED strip.
   */
  public List<Section> getSections() {
    return sectionList;
  }

  
  /**
   * Updates all LED sections and applies the data buffer to the LED strip.
   */
  @Override
  public void periodic() {
    final double deltaTimeSeconds = 0.02; // TODO: Is there a way to ensure this is accurate even with overruns?

    for (Section section : sectionList) {
      section.update(deltaTimeSeconds);
    }
    lightStrip.setData(stripBuffer);
  }
}