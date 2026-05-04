package Glitch.Lib.LEDs;

import static edu.wpi.first.units.Units.Microseconds;
import static edu.wpi.first.units.Units.Seconds;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDReader;
import edu.wpi.first.wpilibj.LEDWriter;
import edu.wpi.first.wpilibj.util.Color;

public class GlitchLEDInterfaces {
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

  public static class TwoDArrayPattern implements LEDArrayPattern {
    public enum StartPosition {
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT
    }

    public enum Alignment {
      ROW_MAJOR,
      COLUMN_MAJOR
    }

    public int width;
    public int height;
    public StartPosition physicalStart;
    public StartPosition intendedOrigin;
    public Alignment alignment;
    public boolean serpentine;
    public ArrayList<ArrayList<Color>> arrayList;

    public TwoDArrayPattern(int width, int height, StartPosition physicalStart, StartPosition intendedOrigin, Alignment alignment, boolean serpentine) {
      this.width = width;
      this.height = height;
      this.physicalStart = physicalStart;
      this.intendedOrigin = intendedOrigin;
      this.alignment = alignment;
      this.serpentine = serpentine;

      arrayList = new ArrayList<ArrayList<Color>>();

      if (alignment == Alignment.ROW_MAJOR) {
        arrayList = new ArrayList<ArrayList<Color>>(height);
        for (int i = 0; i < height; i++) {
          arrayList.add(new ArrayList<Color>(width));
          for (int I = 0; I < width; I++) {
            arrayList.get(i).add(Color.kBlack);
          }
        }
      }
      if (alignment == Alignment.COLUMN_MAJOR) {
        arrayList = new ArrayList<ArrayList<Color>>(width);
        for (int i = 0; i < width; i++) {
          arrayList.add(new ArrayList<Color>(height));
          for (int I = 0; I < height; I++) {
            arrayList.get(i).add(Color.kBlack);
          }
        }
      }
    }

    public static ArrayList<Color> twoDToOneDConverter(ArrayList<ArrayList<Color>> colorArray) {
      ArrayList<Color> flatList = new ArrayList<Color>();
      for (ArrayList<Color> row : colorArray) {
        flatList.addAll(row);
      }
      return flatList;
    }

    public Color get2DColor(int x, int y) {
      if (x < 0 || x >= width) {
        x = x % width;
      }
      if (y < 0 || y >= height) {
        y = y % height;
      }
      if (physicalStart != intendedOrigin) {
        if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_LEFT) || (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.BOTTOM_RIGHT)) {
          y = height - 1 - y;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.TOP_RIGHT) || (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT)) {
          x = width - 1 - x;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT) || (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.TOP_RIGHT)) {
          x = width - 1 - x;
          y = height - 1 - y;
        }
      }
      if (serpentine && ((y % 2 == 1 && alignment == Alignment.ROW_MAJOR) || (x % 2 == 1 && alignment == Alignment.COLUMN_MAJOR))) {
        if (alignment == Alignment.ROW_MAJOR) {
          return arrayList.get(y).get(width - 1 - x);
        } else /*if Alignment.COLUMN_MAJOR*/ {
          return arrayList.get(x).get(height - 1 - y);
        }
      } else {
        return arrayList.get(y).get(x);
      }
    }

    public void set2DColor(int x, int y, Color color) {
      if (x < 0 || x >= width) {
        x = x % width;
      }
      if (y < 0 || y >= height) {
        y = y % height;
      }
      if (physicalStart != intendedOrigin) {
        if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_LEFT) || (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.BOTTOM_RIGHT)) {
          y = height - 1 - y;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.TOP_RIGHT) || (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT)) {
          x = width - 1 - x;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT) || (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.TOP_RIGHT)) {
          x = width - 1 - x;
          y = height - 1 - y;
        }
      }
      if (serpentine && ((y % 2 == 1 && alignment == Alignment.ROW_MAJOR) || (x % 2 == 1 && alignment == Alignment.COLUMN_MAJOR))) {
        if (alignment == Alignment.ROW_MAJOR) {
          arrayList.get(y).set(width - 1 - x, color);
        } else /*if Alignment.COLUMN_MAJOR*/ {
          arrayList.get(x).set(height - 1 - y, color);
        }
      } else {
        arrayList.get(y).set(x, color);
      }
    }

    public ArrayList<Color> colorList(ArrayList<Color> colorList) {
        return twoDToOneDConverter(this.arrayList);
    }
  }

  // Preserving my nonsense down here for now until I can save it properly
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

    public static class OrbitalNonsense extends TwoDArrayPattern {
        public OrbitalNonsense(int width, int height, StartPosition physicalStart, StartPosition intendedOrigin, Alignment alignment, boolean serpentine) {
            super(width, height, physicalStart, intendedOrigin, alignment, serpentine);
        }

        public ArrayList<Color> colorList(ArrayList<Color> colorList) {
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
                    if (((int) Math.sqrt(thisR)) + (rX*rY*2) > 255) {
                        r = r % 255;
                    } else {
                        r = (int) (Math.sqrt(thisR) + (rX*rY*2));
                    }
                    if (((int) Math.sqrt(thisG)) + (gX*gY*2) > 255) {
                        g = g % 255;
                    } else {
                        g = (int) (Math.sqrt(thisG) + (gX*gY*2));
                    }
                    if (((int) Math.sqrt(thisB)) + (bX*bY*2) > 255) {
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

    public static OrbitalNonsense orbitalNonsensePattern = new OrbitalNonsense(
        10, 10,
        TwoDArrayPattern.StartPosition.TOP_LEFT,
        TwoDArrayPattern.StartPosition.TOP_LEFT,
        TwoDArrayPattern.Alignment.ROW_MAJOR,
        true
    );

    // public interface ColorPattern extends LEDPattern {
  //   public Color color();

  //   public default void applyTo(LEDReader reader, LEDWriter writer) {
  //        int bufLen = reader.getLength();

  //        for(int led = 0; led < bufLen; ++led) {
  //           writer.setLED(led, color());
  //        }
  //   }
  // }

  // public class MyTestPattern implements ColorPattern {
  //   public Color color() {
  //     return new Color(0.5, 0.5, 0.5);
  //   }
  // }

  // public LEDPattern pattern = new MyTestPattern();
  // public LEDPattern lambdaPattern = (ColorPattern) () -> new Color(0.5, 0.5, 0.5);
}
