package Glitch.Lib.LEDs.Interfaces;

import static edu.wpi.first.units.Units.Microseconds;
import static edu.wpi.first.units.Units.Seconds;

import java.util.ArrayList;

import Glitch.Lib.LEDs.AbstractLEDS;
import edu.wpi.first.wpilibj.util.Color;

public class OrbitalNonsense extends TwoDArrayPattern {
        public OrbitalNonsense(int width, int height, StartPosition physicalStart, StartPosition intendedOrigin, Alignment alignment, boolean serpentine) {
            super(width, height, physicalStart, intendedOrigin, alignment, serpentine);
        }

        public ArrayList<Color> colorList() {
            double slowTime = Microseconds.of(AbstractLEDS.getTime()).in(Seconds);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color thisColor = get2DColor(x, y);
                    int thisR = (int) (thisColor.red * 255);
                    int thisG = (int) (thisColor.green * 255);
                    int thisB = (int) (thisColor.blue * 255);
                    int rX = (int) ((width/2) + (x*Math.cos(slowTime/2)/2));
                    int rY = (int) ((height/2) + (y*Math.cos(slowTime/2)/2));
                    int gX = (int) ((width/2) + (x*Math.sin(slowTime)/2));
                    int gY = (int) ((height/2) + (y*Math.cos(slowTime)/2));
                    int bX = (int) ((width/2) + (x*Math.cos(slowTime*3)/2));
                    int bY = (int) ((height/2) + (y*Math.sin(slowTime*3)/2));
                    int r = 0;
                    int g = 0;
                    int b = 0;
                    if ((int) (Math.sqrt(thisR) + rX*rY*2) > 255) {
                        r = r % 255;
                    } else {
                        r = (int) (Math.sqrt(thisR) + (rX*rY*2));
                    }
                    if ((int) (Math.sqrt(thisG) + (gX*gY*2)) > 255) {
                        g = g % 255;
                    } else {
                        g = (int) (Math.sqrt(thisG) + (gX*gY*2));
                    }
                    if ((int) (Math.sqrt(thisB) + (bX*bY*2)) > 255) {
                        b = b % 255;
                    } else {
                        b = (int) (Math.sqrt(thisB) + (bX*bY*2));
                    }
                    set2DColor(x, y, new Color(r, g, b));
                }
            }
            return twoDToOneDConverter(this.arrayList);
        }
    }
// public static OrbitalNonsense orbitalNonsensePattern = new OrbitalNonsense(
//     10, 10,
//     TwoDArrayPattern.StartPosition.TOP_LEFT,
//     TwoDArrayPattern.StartPosition.TOP_LEFT,
//     TwoDArrayPattern.Alignment.ROW_MAJOR,
//     true
// );