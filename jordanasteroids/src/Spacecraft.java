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

public class Spacecraft extends VectorSprite
{
        public Spacecraft()
    {
        shape = new Polygon();
        shape.addPoint(0,-25);
        shape.addPoint(15,10);
        shape.addPoint(-15,10);
        
        drawShape = new Polygon();
        drawShape.addPoint(0,-25);
        drawShape.addPoint(15,10);
        drawShape.addPoint(-15,10);
        
        xposition = 450;
        yposition = 300;
        
        ROTATION = 0.15;
        THRUST = 0.25;
        
        weaponType = 1;
        spreadModifier = 0.1;
        
        invincible = true;
        
        burstCounter = 0;
        bursting = false;
        
        for (int i = 0; i < 3; i++)
        {
            for (int n = 0; n < 4; n++)
            {
                upgrades[i][n] = 0;
            }
        }
        
        upgrades[0][1] = 0;
        upgrades[0][2] = 0;
        upgrades[0][0] = 0;
        
        upgrades[1][1] = 0;
        upgrades[1][2] = 0;
        upgrades[1][0] = 0;
        
        upgrades[2][1] = 0;
        upgrades[2][2] = 0;
        upgrades[2][0] = 0;
        
        active = true;
    }
        
        public void accelerate()
    {
        xspeed += Math.cos(angle - Math.PI/2) * THRUST;
        yspeed += Math.sin(angle - Math.PI/2) * THRUST;
    }

    public void rotateLeft()
    {
        angle -= ROTATION;
    }
    
    public void rotateRight()
    {
        angle += ROTATION;
    }
    
    public void hit()
    {
        active = false;
        counter = 0;
    }
    
    public void reset()
    {
        xposition = 450;
        yposition = 300;
        xspeed = 0;
        yspeed = 0;
        angle = 0;
        active = true;
        invincCounter = 0;
        
    }
    
    public void changeWeapon()
    {
        if (weaponSwitched == false) // The weaponSwitched boolean allows me to switch weapons only once per Shift key press
        {                            // instead of switching continuously whenever the Shift key is held down
            weaponType++;
            weaponSwitched = true;
        }
        
        if (weaponType > 3)
        {
            weaponType = 1;
        }
  
    }
    
    public void checkWeapon()
    {
        // Changes the attributes of the ship's gun based on which weapon is selected
        
        if (weaponType == 1) //Spread-shot gun
        {
            fireDelay = 42 - 4*upgrades[0][1];
            damage = 3 + 3*upgrades[0][2];
        }
        
        if (weaponType == 2) //Gatling gun
        {
            fireDelay = 6 - upgrades[1][1];
            damage = 2 + 2*upgrades[1][2];
        }
        
        if (weaponType == 3) //Phase cannon
        {
            fireDelay = 31 - 5*upgrades[2][1];
            damage = 10 + 8*upgrades[2][2];
        }
    }
    
    public void checkInvinc()
    {
        if (invincCounter > 200 && invincible == true)
        {
            invincible = false;
        }
    }
    
}


