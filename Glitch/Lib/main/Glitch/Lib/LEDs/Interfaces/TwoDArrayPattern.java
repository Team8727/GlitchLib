package Glitch.Lib.LEDs.Interfaces;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.util.Color;

public class TwoDArrayPattern implements LEDArrayPattern {
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
        if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_LEFT) || 
            (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.BOTTOM_RIGHT) ||
            (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.TOP_LEFT) ||
            (intendedOrigin == StartPosition.BOTTOM_RIGHT && physicalStart == StartPosition.TOP_RIGHT)) {
          y = height - 1 - y;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.TOP_RIGHT) || 
                    (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT) ||
                    (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.TOP_LEFT) ||
                    (intendedOrigin == StartPosition.BOTTOM_RIGHT && physicalStart == StartPosition.BOTTOM_LEFT)) {
          x = width - 1 - x;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT) || 
                    (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.TOP_RIGHT) ||
                    (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.BOTTOM_LEFT) ||
                    (intendedOrigin == StartPosition.BOTTOM_RIGHT && physicalStart == StartPosition.TOP_LEFT)) {
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
        if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_LEFT) || 
            (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.BOTTOM_RIGHT) ||
            (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.TOP_LEFT) ||
            (intendedOrigin == StartPosition.BOTTOM_RIGHT && physicalStart == StartPosition.TOP_RIGHT)) {
          y = height - 1 - y;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.TOP_RIGHT) || 
                    (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT) ||
                    (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.TOP_LEFT) ||
                    (intendedOrigin == StartPosition.BOTTOM_RIGHT && physicalStart == StartPosition.BOTTOM_LEFT)) {
          x = width - 1 - x;
        } else if ((intendedOrigin == StartPosition.TOP_LEFT && physicalStart == StartPosition.BOTTOM_RIGHT) || 
                    (intendedOrigin == StartPosition.BOTTOM_LEFT && physicalStart == StartPosition.TOP_RIGHT) ||
                    (intendedOrigin == StartPosition.TOP_RIGHT && physicalStart == StartPosition.BOTTOM_LEFT) ||
                    (intendedOrigin == StartPosition.BOTTOM_RIGHT && physicalStart == StartPosition.TOP_LEFT)) {
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