import java.awt.Polygon;

public class Bullet extends VectorSprite
{
    
    public Bullet(double x, double y, double a, int bulletType)
    {
        
        // Draws different types of bullets based on which weapon is being used by the ship
        
        if (bulletType == 1)
        {
            shape = new Polygon();
            shape.addPoint(1,0);
            shape.addPoint(0,-1);
            shape.addPoint(-1,0);
            shape.addPoint(0,1);
        
            drawShape = new Polygon();
            drawShape.addPoint(1,0);
            drawShape.addPoint(0,-1);
            drawShape.addPoint(-1,0);
            drawShape.addPoint(0,1);
        }
        
        if (bulletType == 2)
        {
            shape = new Polygon();
            shape.addPoint(0,0);
            shape.addPoint(0,0);
            shape.addPoint(0,0);
            shape.addPoint(0,0);
        
            drawShape = new Polygon();
            drawShape.addPoint(0,0);
            drawShape.addPoint(0,0);
            drawShape.addPoint(0,0);
            drawShape.addPoint(0,0);
        }
        
        if (bulletType == 3)
        {
            shape = new Polygon();
            shape.addPoint(4,0);
            shape.addPoint(0,-4);
            shape.addPoint(-4,0);
            shape.addPoint(0,4);
        
            drawShape = new Polygon();
            drawShape.addPoint(4,0);
            drawShape.addPoint(0,-4);
            drawShape.addPoint(-4,0);
            drawShape.addPoint(0,4);
        }
        
        xposition = x;
        yposition = y;
        angle = a;
        
        THRUST = 15;
        
        xspeed = Math.cos(angle) * THRUST;
        yspeed = Math.sin(angle) * THRUST;
       
    }
   
}
