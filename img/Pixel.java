
/**
 * 
 * 
 * @author Daniel Taylor (DTIII)
 * @version .._-
 */
public class Pixel
{
    int red, green, blue;
    public Pixel()
    {
        red = 0;
        green = 0;
        blue = 0;
    }
    
    public Pixel(int r, int g, int b)
    {
        red = r;
        green = g;
        blue = b;
    }
    
    void setColors(int r, int g, int b)
    {
        red = r;
        green = g;
        blue = b;
    }
    
    void incrementColors(int amt)
    {
        red += amt;
        green += amt;
        blue += amt;
        
        red &= 0xFF;
        green &= 0xFF;
        blue &= 0xFF;
    }
    
    int getRed(){return red;}
    int getGreen(){return green;}
    int getBlue(){return blue;}
}