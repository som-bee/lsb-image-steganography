import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.awt.Color;
import javax.imageio.ImageIO;


public class steg {
    public static void main(String[] args) {

        // cover image file
        File coverImg = new File("cover-images/cover-enl.png");

        // fingerprint file
        File fPrint = new File("thumb-images/thumb-shorten.png");

        try {
            hideFingerprint(fPrint, coverImg);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void addSeparator() {
        System.out.println("#################################################################################");
    }

    private static void hideFingerprint(File fPrint, File coverImage) throws IOException {

        addSeparator();
        System.out.println("FIGERPRINT IMAGE");
        ArrayList<Boolean[][]> fPrintPixelArray = getPixelArray(fPrint);
        System.out.println("fingerprint image pixel array size: " + fPrintPixelArray.size());
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
            for (int seg = 1; seg <= 3; seg+=2) {
                int xs = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + seg).get("start").get("x");
                int ys = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + seg).get("start").get("y");
                int xe = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + (seg+1)).get("end").get("x");
                int ye = segmentedImgCoordinateMap.get("region-" + region).get("segment-" + (seg+1)).get("end").get("y");

                addSeparator();
                System.out.println("Region: " + region + ", Segment: " + seg);
                System.out.println("xs: " + xs + ", ys: " + ys + ", xe: " + xe + ", ye: " + ye);

                FPHidingVars.hidden = false;

                FPHidingVars.init();


                for (int y = ys; y <= ye && !FPHidingVars.hidden; y+=1) {
                    for (int x = xs; x <= xe && !FPHidingVars.hidden; x+=1) {
                        // Retrieving contents of a pixel
                        int pixel = bufferedImage.getRGB(x, y);
                        // Creating a Color object from pixel value
                        Color color = new Color(pixel, true);

                        

                        // Retrieving the R G B values
                        int redCover = color.getRed();
                        int greenCover = color.getGreen();
                        int blueCover = color.getBlue();
                        // Modifying the RGB values by inserting the fprintBit

                        int newRedCover = hideBitInColor(redCover, fPrintPixelArray);
                        if (FPHidingVars.hidden) {
                            System.out.println("End : x:" + x + " ,y: " + y);
                            break;
                        }
                        int newGreenCover = hideBitInColor(greenCover, fPrintPixelArray);
                        if (FPHidingVars.hidden) {
                            System.out.println("End : x:" + x + " ,y: " + y);
                            break;
                        }
                        int newBlueCover = hideBitInColor(blueCover, fPrintPixelArray);
                        if (FPHidingVars.hidden) {
                            System.out.println("End : x:" + x + " ,y: " + y);
                            break;
                        }

                        // // Creating new Color object
                        color = new Color(newRedCover, newGreenCover, newBlueCover);
                        // // Setting new Color object to the image
                        bufferedImage.setRGB(x, y, color.getRGB());

                       
                    }
                   
                }
                
            }
        }

        addSeparator();
        // Saving the modified image
        File file = new File("stego-cover/steg-cover.png");
        ImageIO.write(bufferedImage, "png", file);
        System.out.println("Image Steganography Done...");

    }
    //getting 
    private static int hideBitInColor(int coverColor, ArrayList<Boolean[][]> fPrintPixelArray){
        // finger print image current color of current pixel
        Boolean curColorBit = fPrintPixelArray.get(FPHidingVars.curfPrintPixel)[FPHidingVars.curfPrintColor][FPHidingVars.curfPrintBit];

        int newColor=coverColor;     
        // incrementing the color counter

        FPHidingVars.curfPrintBit++;

        FPHidingVars.curfPrintColor += FPHidingVars.curfPrintBit/8;


        FPHidingVars.curfPrintPixel += FPHidingVars.curfPrintColor / 3;

        FPHidingVars.curfPrintColor = FPHidingVars.curfPrintColor % 3;
        FPHidingVars.curfPrintBit %= 8;

         // checking if the fingerprint hiding completed or not
         if (FPHidingVars.curfPrintPixel >= fPrintPixelArray.size()) {
            FPHidingVars.hidden = true;
            System.out.println("hidden successfully:^)");
            //break;
        }


        if(curColorBit){
            newColor = coverColor | 1;
        }
        else{
            newColor = coverColor & 254;
        }

        return  newColor;
    }

    // getting fingerprint image pixel array from the fingerprint image file
    public static ArrayList<Boolean[][]> getPixelArray(File image) {
        ArrayList<Boolean[][]> pixelArray = new ArrayList<Boolean[][]>();

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        System.out.println("width: " + width + ", height: " + height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Retrieving contents of a pixel
                int pixel = bufferedImage.getRGB(x, y);

                // Creating a Color object from pixel value
                Color color = new Color(pixel, true);
                // Retrieving the R G B values
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                Boolean[][] colorsArray = new Boolean[3][8];
                for(int i=0; i<colorsArray.length; i++){
                    Arrays.fill(colorsArray[i], false);
                    //System.out.println(Arrays.toString(colorsArray[i]));
                }
                fillColor(colorsArray[0], red);
                fillColor(colorsArray[1], green);
                fillColor(colorsArray[2], blue);

                
                pixelArray.add(colorsArray);
                // System.out.println(Arrays.toString(colorArray));
            }
        }

        return pixelArray;
    }

    private static void fillColor(Boolean[] colorArr, int color) {
        String colorString =Integer.toBinaryString(color);
        int index = 7;
        for (int i=colorString.length()-1; i>=0; i--) {
            colorArr[index]=('1'==colorString.charAt(i));
            //System.out.println(colorArr[index]+" "+colorString.charAt(i));
            index--;
        }

    }

}

class FPHidingVars{
    public static int curfPrintPixel = 0;
    public static int curfPrintColor = 0;
    public static int curfPrintBit = 0;
    public static Boolean hidden=false;
    public static void init() {
        curfPrintPixel = 0;
   curfPrintColor = 0;
curfPrintBit = 0;
hidden=false;
    }
}