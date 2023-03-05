import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


// for testing purposes
public  class Test {


    public static void main(String[] args)

    {
        int color=1;
        Boolean colorArr[]=new Boolean[8];

        String colorString =Integer.toBinaryString(color);
        int index = 7;
        for (int i=colorString.length()-1; i>=0; i--) {
            colorArr[index]=('1'==colorString.charAt(i));
            //System.out.println(colorArr[index]+" "+colorString.charAt(i));
            index--;
        }
        System.out.println(Arrays.toString(colorArr));



        Boolean[][] colorsArray = new Boolean[3][8];
        
        

        ArrayList<Boolean[][]> pixArr=  steg.getPixelArray(new File("thumb-images/thumb-shorten.png"));

        ExtractFromStego.getImageFromPixelArray(new File("test-assets/extracted-thumb-test.png"), pixArr);
       
    }
} 
    

