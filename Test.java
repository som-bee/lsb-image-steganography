import java.util.Arrays;

public  class Test {
  
// function have implementation of another 
    // function inside local class
    static void Foo()
    {
        int x=20;
        // local class
        class Local {
            void fun()
            {
               // x++;
                System.out.println("geeksforgeeks");
            }
        }
        new Local().fun();
            System.out.println(x);
    }
    public static void main(String[] args)
    {
        Boolean[][] colorsArray = new Boolean[3][8];
        
        Boolean[] colorArr =new Boolean[]{true,false,true,false};
       
    }
} 
    

