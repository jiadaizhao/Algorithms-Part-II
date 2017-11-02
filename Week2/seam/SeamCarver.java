/******************************************************************************
 *  Compilation:  javac SeamCarver.java
 *  Execution:    java SeamCarver
 *  Dependencies:
 *  
 *  SeamCarver module API
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private static final int BORDER_ENERGY = 1000;
    private int width;
    private int height;
    private final Picture pict;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Picture is null.");
        }
        this.width = picture.width();
        this.height = picture.height();
        this.pict = new Picture(picture);
    }
    
    private int gradientSquare(int x1, int y1, int x2, int y2) {
        Color color1 = pict.get(x1, y1);
        Color color2 = pict.get(x2, y2);
        int red = color1.getRed() - color2.getRed();
        int green = color1.getGreen() - color2.getGreen();
        int blue = color1.getBlue() - color2.getBlue();
        return red * red + green * green + blue * blue;
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                picture.set(x, y, pict.get(x, y));
            }
        }
        return picture;
    }
    
    // width of current picture
    public int width() {
        return width;
    }
    
    // height of current picture
    public int height() {
        return height;
    }
    
    // energy of pixel at column x and row y
    public  double energy(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Indices are out of boundary.");
        }
        
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER_ENERGY;
        }
        
        int deltaX = gradientSquare(x + 1, y, x - 1, y);
        int deltaY = gradientSquare(x, y + 1, x, y - 1);
        return Math.sqrt(deltaX + deltaY);
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] sum = new double[2][height];
        int[][] parent = new int[width][height];
        for (int y = 0; y < height; ++y) {
            sum[0][y] = BORDER_ENERGY;
            parent[0][y] = y;
        }
        
        for (int x = 1; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                double temp = sum[(x - 1) % 2][y];
                parent[x][y] = y;
                if (y > 0 && sum[(x - 1) % 2][y - 1] < temp) {
                    temp = sum[(x - 1) % 2][y - 1];
                    parent[x][y] = y - 1;
                }
                
                if (y < height - 1 && sum[(x - 1) % 2][y + 1] < temp) {
                    temp = sum[(x - 1) % 2][y + 1];
                    parent[x][y] = y + 1;
                }
                sum[x % 2][y] = energy(x, y) + temp;                
            }
        }
        
        int index = 0;
        for (int y = 1; y < height; ++y) {
            if (sum[(width - 1) % 2][y] < sum[(width - 1) % 2][index]) {
                index = y;
            }
        }
        int[] seam = new int[width];
        seam[width - 1] = index;
        for (int x = width - 2; x >= 0; --x) {
            seam[x] = parent[x + 1][index];
            index = parent[x + 1][index];
        }
        return seam;        
    }
    
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {        
        double[][] sum = new double[width][2];
        int[][] parent = new int[width][height];
        for (int x = 0; x < width; ++x) {
            sum[x][0] = BORDER_ENERGY;
            parent[x][0] = x;
        }
        
        for (int y = 1; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double temp = sum[x][(y - 1) % 2];
                parent[x][y] = x;
                if (x > 0 && sum[x - 1][(y - 1) % 2] < temp) {
                    temp = sum[x - 1][(y - 1) % 2];
                    parent[x][y] = x - 1;
                }
                
                if (x < width - 1 && sum[x + 1][(y - 1) % 2] < temp) {
                    temp = sum[x + 1][(y - 1) % 2];
                    parent[x][y] = x + 1;
                }
                sum[x][y % 2] = energy(x, y) + temp;                
            }
        }
        
        int index = 0;
        for (int x = 1; x < width; ++x) {
            if (sum[x][(height - 1) % 2] < sum[index][(height - 1) % 2]) {
                index = x;
            }
        }
        int[] seam = new int[height];
        seam[height - 1] = index;
        for (int y = height - 2; y >= 0; --y) {
            seam[y] = parent[index][y + 1];
            index = parent[index][y + 1];
        }
        return seam;
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width) {
            throw new IllegalArgumentException("Seam is illegal.");
        }
        
        if (height <= 1) {
            throw new IllegalArgumentException("Height of the picture is less than or equal to 1");
        }
        
        for (int x = 0; x < width; ++x) {
            if (seam[x] < 0 || seam[x] >= height || (x > 0 && Math.abs(seam[x] - seam[x - 1]) > 1)) {
                throw new IllegalArgumentException("Seam is illegal.");
            }
            for (int y = seam[x]; y < height - 1; ++y) {
                pict.set(x, y, pict.get(x, y + 1));
            }
        }
        
        --height;
    }
    
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height) {
            throw new IllegalArgumentException("Seam is illegal.");
        }
        
        if (width <= 1) {
            throw new IllegalArgumentException("Width of the picture is less than or equal to 1");
        }
        
        for (int y = 0; y < height; ++y) {
            if (seam[y] < 0 || seam[y] >= width || (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1)) {
                throw new IllegalArgumentException("Seam is illegal.");
            }
            for (int x = seam[y]; x < width - 1; ++x) {
                pict.set(x, y, pict.get(x + 1, y));
            }
        }
        
        --width;
    }
}
