package Glitch.Lib.LEDs.Interfaces;

import static edu.wpi.first.units.Units.Microseconds;
import static edu.wpi.first.units.Units.Seconds;

import java.util.ArrayList;

import Glitch.Lib.LEDs.AbstractLEDS;
import edu.wpi.first.wpilibj.util.Color;

public class FunkyNonsensePattern implements LEDArrayPattern {
        ArrayList<Color> colors = new ArrayList<Color>();

        public ArrayList<Color> colorList(ArrayList<Color> colorList) {

            for (int i = 0; i < colorList.size(); i++) {

                int r = (int) (colorList.get(i).red * 255) + i;
                int g = (int) ((colorList.get(i).green * 255) + Microseconds.of(AbstractLEDS.getTime()).in(Seconds));
                int b = (int) (colorList.get(i).blue * 255) + 3;

                if (r > 255) r = r - 255;
                if (g > 255) g = g % 255;
                if (b > 255) b = b - 255;

                if (colors.size() > i) {
                    colors.set(i, new Color(r, g, b));
                } else {
                    colors.add(i, new Color(r, g, b));
                }
            }
            return colors;
        }
    }
