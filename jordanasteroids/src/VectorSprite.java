import java.applet.Applet;
import java.awt.*;

public class VectorSprite extends Applet 
{
    //VARIABLES//
    double xposition;
    double yposition;
    double xspeed;
    double yspeed;
    double angle;
    double size;
    double health;
    double damage;
    double spreadModifier;
    
    //CONSTANTS//
    double ROTATION;
    double THRUST;
    
    //INTEGERS//
    int counter;
    int burstCounter;
    boolean bursting;
    int invincCounter;
    int weaponType;
    int fireDelay;
    
    boolean weaponSwitched;
    boolean invincible;
    
    Polygon shape, drawShape;
    
    boolean active;
    
    int[][] upgrades = new int [3][4]; // First slot tells which gun is being upgraded, second slot tells what is being upgraded
    int [][] upgradeCost = new int [3][4];
    
    public VectorSprite()
    {
        active = true;
    }
            
    public void paint(Graphics g)
    {
        g.drawPolygon(drawShape);
    }
    
    public void updatePosition()
    {
        counter ++;
        invincCounter ++;
        
        int x, y;
        
        xposition += xspeed;
        yposition += yspeed;
        
        for (int i = 0; i < shape.npoints; i++)
        {
            
            x = (int)Math.round(shape.xpoints[i] * Math.cos(angle) - shape.ypoints[i] * Math.sin(angle));
            y = (int)Math.round(shape.xpoints[i] * Math.sin(angle) + shape.ypoints[i] * Math.cos(angle));
            
            drawShape.xpoints[i] = x;
            drawShape.ypoints[i] = y;
        }
        
        wraparound();
        
        x = (int)Math.round(xposition);
        y = (int)Math.round(yposition);
        
        drawShape.invalidate();
        
        drawShape.translate(x, y);
                        
    }
    
    private void wraparound ()
    {
        if (xposition > 900)
        {
            xposition = 0;
        }
        
        if (xposition < 0)
        {
            xposition = 900;
        }
        
        if (yposition > 600)
        {
            yposition = 0;
        }
        
        if (yposition < 0)
        {
            yposition = 600;
        }
    }
    
}
