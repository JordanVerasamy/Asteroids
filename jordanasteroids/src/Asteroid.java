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

import java.applet.Applet;
import java.awt.*;

public class Asteroid extends VectorSprite
{
    int c, d, e, f, g, h, i, j, k, l, m, n;
    double a, b;
    
    public Asteroid()
    {
        size = 5;
        initializeAsteroid();
    }
    
    public Asteroid (double x, double y, double size, double x2, double y2)
    {
        this.size = size;
        initializeAsteroid();
        xposition = x;
        yposition = y;
        xspeed = x2 + ((Math.random() - 0.5)*2);
        yspeed = y2 + ((Math.random() - 0.5)*2);
    }
    
    public void initializeAsteroid()
    {
        c = (int)(Math.random()*12*size);
        d = (int)(Math.random()*12*size);
        e = (int)(Math.random()*12*size);
        f = (int)(Math.random()*12*size);
        g = (int)(Math.random()*12*size);
        h = (int)(Math.random()*12*size);
        i = (int)(Math.random()*12*size);
        j = (int)(Math.random()*12*size);
        k = (int)(Math.random()*12*size);
        l = (int)(Math.random()*12*size);
        
        shape = new Polygon();
        shape.addPoint(c,i);
        shape.addPoint(d,-j);
        shape.addPoint(-e,-k);
        shape.addPoint(-h,n);
        shape.addPoint(0,m);
        
        drawShape = new Polygon();
        drawShape.addPoint(c,i);
        drawShape.addPoint(d,-j);
        drawShape.addPoint(-e,-k);
        drawShape.addPoint(-h,n);
        drawShape.addPoint(0,m);
        
        health = 5 * (size - 2);
        
        xposition = 450;
        yposition = 300;
        
        a = Math.random()*2;
        b = Math.random()*2*Math.PI;
        
        xspeed = Math.cos(b)*a;
        yspeed = Math.sin(b)*a;
        
        a = Math.random()*400+100;
        b = Math.random()*2*Math.PI;
        
        xposition = Math.cos(b)*a + 450;
        yposition = Math.sin(b)*a + 300;
        
        ROTATION = (Math.random()-0.5)/5;
    }
    
    public void updatePosition()
    {
        angle += ROTATION;
        super.updatePosition();
    }
}
