package Glitch.Lib.LEDs.Interfaces;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

public interface LEDArrayPattern extends LEDPattern {
    public ArrayList<Color> colorList(ArrayList<Color> colorList);


    public default void applyTo(LEDReader reader, LEDWriter writer) {
      ArrayList<Color> arrayColors = new ArrayList<Color>(reader.getLength());
      
      int bufLen = reader.getLength();
      if (arrayColors.size() != bufLen) {
        arrayColors.clear();
        for (int i = 0; i < bufLen; i++) {
          arrayColors.add(reader.getLED(i));
        }
      } else {
        for (int i = 1; i < reader.getLength(); i++) {
          arrayColors.add(reader.getLED(i));
        }
      }

      arrayColors = colorList(arrayColors);

      for (int i = 0; i < reader.getLength(); i++) {
        writer.setLED(i, arrayColors.get(i));
      }
    }
  }
