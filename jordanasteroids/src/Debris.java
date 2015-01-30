/***********************************************************************
*                         PROGRAM HEADER                               * 
************************************************************************
* PROGRAMMER'S NAME:    Jordan Verasamy                                *                                                                    
* DATE:                 Monday, January 21, 2012                       *                                                                   
* PROGRAM NAME:         ASTEROIDS ISP                                  *                                                               
* CLASS:                ICS-4U1                                        *                                                                                                                                                                      
* TEACHER:              Mrs. Barsan                                    *                                                                     
* DUE DATE:             Monday, January 21, 2012                       * 
*                                                                      * 
************************************************************************/

import java.awt.Polygon;
import java.applet.Applet;

public class Debris extends VectorSprite
{
    public Debris(double x, double y) 
    {
        double a;
        
        shape = new Polygon();
        shape.addPoint(1,1);
        shape.addPoint(-1,-1);
        shape.addPoint(-1,1);
        shape.addPoint(1,-1);
        
        drawShape = new Polygon();
        drawShape.addPoint(1,1);
        drawShape.addPoint(-1,-1);
        drawShape.addPoint(-1,1);
        drawShape.addPoint(1,-1);
        
        xposition = x;
        yposition = y;
        
        a = Math.random() * 2*Math.PI;
        angle = a;
        
        THRUST = Math.random() * 5 + 5;
        
        xspeed = Math.cos(angle) * THRUST;
        yspeed = Math.sin(angle) * THRUST;
    }
}
