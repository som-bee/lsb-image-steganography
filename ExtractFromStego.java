import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.Color;
import javax.imageio.ImageIO;

public class ExtractFromStego {
    public static void main(String[] args) {

        // cover image file
        File coverImg = new File("cover-images/steg-cover.png");

        // fingerprint file
        // File fPrint = new File("thumb-extracted.png");

        try {
            extractFingerprint(coverImg);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void addSeparator() {
        System.out.println("#################################################################################");
    }

    private static void extractFingerprint(File coverImage) throws IOException {

        addSeparator();

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(coverImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        System.out.println("COVER IMAGE");
        System.out.println("width: " + width + ", height: " + height);

        // storing the image coordinates for getting the segmented coordinates
        int[] imgCoordinates = { 0, 0, width - 1, height - 1 };

        // storing segmented coordinates in segmentedImgCoordinateMap : using methods
        // from GenerateCoordinates class
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> segmentedImgCoordinateMap = GenerateCoordinates
                .getCoordinatesFromCoverImg(imgCoordinates);

        // print segmentedImgCoordinateMap
        // System.out.println("SEGMENT COORDINATES MAP : " +
        // Arrays.asList(segmentedImgCoordinateMap));

        // hiding fingerprint in every segment
        for (int region = 1; region <= 3; region++) {
            for (int seg = 1; seg <= 3; seg += 2) {
                int xs = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + seg).get("start").get("x");
                int ys = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + seg).get("start").get("y");
                int xe = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + (seg + 1)).get("end")
                        .get("x");
                int ye = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + (seg + 1)).get("end")
                        .get("y");

                addSeparator();
                System.out.println("Region: " + region + ", Segment: " + seg);
                System.out.println("xs: " + xs + ", ys: " + ys + ", xe: " + xe + ", ye: " + ye);

                // FPExtractingVars.hidden = false;

                FPExtractingVars.init();
                // pixel array of fingerprint
                ArrayList<Boolean[][]> fPrintPixelArray = new ArrayList<Boolean[][]>();
                // adding initial pixel
                fPrintPixelArray.add(new Boolean[3][8]);

                for (int y = ys; y <= ye; y += 1) {
                    for (int x = xs; x <= xe; x += 1) {
                        // Retrieving contents of a pixel
                        int pixel = bufferedImage.getRGB(x, y);
                        // Creating a Color object from pixel value
                        Color color = new Color(pixel, true);

                        // Retrieving the R G B values
                        int redCover = color.getRed();
                        int greenCover = color.getGreen();
                        int blueCover = color.getBlue();
                        // Modifying the RGB values by inserting the fprintBit

                        extractBitFromColor(redCover, fPrintPixelArray);
                        extractBitFromColor(greenCover, fPrintPixelArray);
                        extractBitFromColor(blueCover, fPrintPixelArray);

                    }

                }
                System.out.println("Extracted.....");
                System.out.println("Pixel array length: " + fPrintPixelArray.size());

                // fingerprint file
                File fPrint = new File("extracted-finger-prints/extracted-thumb-reg-" + region + "-seg-" + seg + ".png");

                getImageFromPixelArray(fPrint, fPrintPixelArray);
            }
        }

        addSeparator();
        // Saving the modified image

        System.out.println("Image Steganography Extraction Done...");

    }

    // getting
    private static void extractBitFromColor(int coverColor, ArrayList<Boolean[][]> fPrintPixelArray) {

        Boolean curColorBit = coverColor % 2 == 1;

        // finger print image current color of current pixel
        fPrintPixelArray.get(FPExtractingVars.curfPrintPixel)[FPExtractingVars.curfPrintColor][FPExtractingVars.curfPrintBit] = curColorBit;

        // incrementing the color counter

        FPExtractingVars.curfPrintBit++;

        if (FPExtractingVars.curfPrintBit / 8 >= 1) {
            FPExtractingVars.curfPrintColor += 1;
        }

        if (FPExtractingVars.curfPrintColor / 3 >= 1) {
            fPrintPixelArray.add(new Boolean[3][8]);
            FPExtractingVars.curfPrintPixel += 1;
        }

        FPExtractingVars.curfPrintColor %= 3;
        FPExtractingVars.curfPrintBit %= 8;

        // checking if the fingerprint hiding completed or not
        // if (FPExtractingVars.curfPrintPixel >= fPrintPixelArray.size()) {
        // FPExtractingVars.hidden = true;
        // System.out.println("hidden successfully:^)");
        // //break;
        // }

    }

    // getting fingerprint image pixel array from the fingerprint image file
    public static void getImageFromPixelArray(File image, ArrayList<Boolean[][]> pixelArray) {

        int width = 100;
        int height = 125;
        System.out.println("width: " + width + ", height: " + height);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int pixelCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Boolean[][] pixel = pixelArray.get(pixelCount++);

                // Setting the R G B values
                int red = getColorValue(pixel[0]);
                int green = getColorValue(pixel[1]);
                int blue = getColorValue(pixel[2]);

                // // Creating new Color object
                Color color = new Color(red, green, blue);
                // // Setting new Color object to the image
                bufferedImage.setRGB(x, y, color.getRGB());

            }
        }
        addSeparator();
        // Saving the modified image
       
        try {
            ImageIO.write(bufferedImage, "png", image);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Extracted Successfully...");

    }

    private static int getColorValue(Boolean[] colorArr) {
        String colorString = "";
        for (Boolean bit : colorArr) {
            if (bit) {
                colorString = colorString + "1";
            } else {
                colorString = colorString + "0";
            }
        }

        int color = Integer.parseInt(colorString, 2);
        return color;

    }

}

class FPExtractingVars {
    public static int curfPrintPixel = 0;
    public static int curfPrintColor = 0;
    public static int curfPrintBit = 0;
    public static Boolean extracted = false;

    public static void init() {
        curfPrintPixel = 0;
        curfPrintColor = 0;
        curfPrintBit = 0;
        extracted = false;
    }
}