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
************************************************************************                                                                      
* WHAT THE PROGRAM DOES                                                * 
* The ASTEROIDS program allows the user to play a version of the retro *
* arcade game. Players can fly around and shoot asteroids to progress  *
* through levels. 
*			 					       *
************************************************************************                                                                    
* CLASSES							       *
*								       *		
* MainGame - This class contains all of the main program logic. The    *
* timer ticks causing all button-presses to be detected and all graphic*
* objects to be drawn. Additionally, the program logic runs every time *
* the timer ticks, moving the ship and other objects.                  *
* 
* VectorSprite - The base class for all other object classes in this   *
* game. All graphics are VectorSprites. VectorSprites are basically    *
* just graphics drawn by connecting points.                            *
* 
* Spacecraft, Bullet - These classes allow the game to construct the   *
* ship and its bullets. They are sub-classes of VectorSprite. The      *
* Spacecraft object allows the ship to have parameters such as         *
* ship.weaponType and tracks the ship's coordinates. Bullet is an      *
* object that is instantiated every time a bullet is fired, to create a*
* bullet object that then fires forward a bit and peters out.          *
* 
* Asteroid, Debris - These classes represent the asteroids and the     *
* debris that the game draws. Debris is created every time an asteroid *
* or ship is destroyed, going off from the center of impact in random  *
* directions at a random speed. The Asteroid class is essential to     *
* the game, to draw asteroids procedurally but randomly. Both are sub- *
* classes of VectorSprite.                                             *
*								       *
************************************************************************                                                                  
* OTHER INFORMATION                                                    *
* The game uses a lot of math to work. Trigonometry and algebra are    *
* extremely important in getting the objects to move and rotate in a   *
* physically accurate way. In particular, the math used to randomly    *
* generate asteroids in pseudo-3D is fairly complicated. First, the    *
* asteroid is generated by finding 4 random points, one in each quad-  *
* rant, and taking the fifth point to be the origin (0,0). Then, to    *
* generate the pseudo-3D, the game runs through a triple-nested for    *
* loop. For all connections between all points on all asteroids, a     *
* white line is drawn - so all points for any given asteroid are       *
* connected by white lines. Next, to get these inner lines to rotate as*
* the asteroid rotates, the x/y coordinates of the lines are modified  *
* as a function of the angle variable relative to the center of the    *
* asteroid, so that the lines rotate about the center at the same rate *
* that the vertices do.
* 
************************************************************************                                                               
* EXTENSIONS AND IMPROVEMENTS                                          * 
* The program could be able to include 2 or more players, by making    *
* more instantiations of the Spacecraft object. The game could also    *
* allow the player to upgrade their weapons using points - perhaps to  *
* increase fire rate, damage, or range.                                *
*								       *
***********************************************************************/



import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.ArrayList;
import java.text.*;
import java.awt.Graphics.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainGame extends Applet implements KeyListener, ActionListener {
    
    Image offscreen;
    Graphics offg;
    Spacecraft ship; // Defines "ship" as a Spacecraft, giving it
    Asteroid rock;   // parameters such as ship.angle or ship.yspeed
    ArrayList<Asteroid> asteroidList;
    ArrayList<Bullet> bulletList;
    ArrayList<Debris> explosionList;
    Timer timer;
    int shopSelection;
    int fireDelay;
    int level, credits, lives;
    int numAsteroids;
    int numDebris;
    int bulletDeathCounter;
    Boolean[][] starPositions = new Boolean[900][600];
    int starPositionSeed;
    boolean upKey, leftKey, rightKey, spaceKey, shiftKey, SKey, DKey, PKey, FKey, escKey;
    boolean isExplosionShip;
    boolean isMainInstr = false, instrSwitched = false;
    boolean pauseKeyActivated = false;
    int gameState;
    DecimalFormat df = new DecimalFormat("#.##");
    
    public void init() {
        initStarPositions();
        
        timer = new Timer(20, this);
        
        this.setSize(900, 600);
        this.addKeyListener(this);
        
        ship = new Spacecraft();
        
        offscreen = createImage(this.getWidth(), this.getHeight());
        offg = offscreen.getGraphics();
        
        asteroidList = new ArrayList();
        bulletList = new ArrayList();
        explosionList = new ArrayList();
        
        numAsteroids = 4;
        numDebris = 20;
        
        shopSelection = 5;
        
        gameState = 0;
        // 0 = game main menu, 1 = game running, 2 = level complete, 3 = game over, 4 = paused
        
        bulletDeathCounter = 30;
        
        level = 1;
        credits = 0;
        lives = 3;
        
        for (int i = 0; i < numAsteroids; i++) {
            asteroidList.add(new Asteroid());
        }
    }
    
    public void paint(Graphics g) //Method that draws everything
    {
        if (gameState == 0) {
            try {
                drawMainMenu();
            } catch (Exception e) {
            }
        } else if (gameState == 1) {
            //Draw background black with stars in the background, randomly generated according to starPositions[][]
            offg.setColor(Color.BLACK);
            offg.fillRect(0, 0, 900, 600);
            
            offg.setColor(Color.GRAY);
            
            for (int i = 0; i < 900; i++) {
                for (int n = 0; n < 600; n++) {
                    if (starPositions[i][n] == true) {
                        offg.drawLine(i, n, i, n); // Creates a star at the point in the bakground with a 1/1000 probability. (See init())
                    }
                }
            }
            
            //Draw each asteroid white with white 3D inside lines
            
            offg.setColor(Color.WHITE);
            
            for (int i = 0; i < asteroidList.size(); i++) {
                for (int n = 0; n < 5; n++) {
                    for (int j = 0; j < 5; j++) {
                        offg.drawLine((int) Math.round((asteroidList.get(i).shape.xpoints[n] * Math.cos(asteroidList.get(i).angle) - asteroidList.get(i).shape.ypoints[n] * Math.sin(asteroidList.get(i).angle) + asteroidList.get(i).xposition)),
                                (int) Math.round((asteroidList.get(i).shape.xpoints[n] * Math.sin(asteroidList.get(i).angle) + asteroidList.get(i).shape.ypoints[n] * Math.cos(asteroidList.get(i).angle) + asteroidList.get(i).yposition)),
                                (int) Math.round((asteroidList.get(i).shape.xpoints[j] * Math.cos(asteroidList.get(i).angle) - asteroidList.get(i).shape.ypoints[j] * Math.sin(asteroidList.get(i).angle) + asteroidList.get(i).xposition)),
                                (int) Math.round((asteroidList.get(i).shape.xpoints[j] * Math.sin(asteroidList.get(i).angle) + asteroidList.get(i).shape.ypoints[j] * Math.cos(asteroidList.get(i).angle) + asteroidList.get(i).yposition)));
                    }
                }
                
                if (asteroidList.get(i).active == true) {
                    asteroidList.get(i).paint(offg);
                }
            }
            
            //Draw each bullet yellow
            offg.setColor(Color.YELLOW);
            for (int i = 0; i < bulletList.size(); i++) {
                if (bulletList.get(i).active == true) {
                    bulletList.get(i).paint(offg);
                }
            }
            
            drawExplosions();
            try {
                drawHUD();
            } catch (Exception e) {
            };
            drawShip();
            
        } else if (gameState == 2) {
            drawShop();
        } else if (gameState == 3) {
            drawEndScreen();
        } else if (gameState == 4) {
            offg.setColor(Color.GREEN);
            offg.fillRect(390, 75, 105, 40);
            
            offg.setColor(Color.BLACK);
            offg.fillRect(395, 80, 95, 30);
            
            offg.setColor(Color.GREEN);
            offg.drawString("GAME PAUSED", 400, 100);
        }
        
        g.drawImage(offscreen, 0, 0, this);
    }
    
    public void keyPressed(KeyEvent e) {
        
        // Checks what key is being pressed
        
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_S) {
            SKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_P) {
            PKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            shiftKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_D) {
            DKey = true;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F) {
            FKey = true;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        
        //Checks when keys are released
        
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upKey = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKey = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKey = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceKey = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_S) {
            SKey = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_P) {
            PKey = false;
            pauseKeyActivated = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            shiftKey = false;
            ship.weaponSwitched = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escKey = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_D) {
            DKey = false;
            instrSwitched = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_F) {
            FKey = false;
        }
        
    }
    
    public void keyTyped(KeyEvent e) {
        // Not using this, but Java's key press code doesn't work without alll three related methods.
    }
    
    public void update(Graphics g) {
        paint(g);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (gameState == 0) {
            keyCheck();
        } else if (gameState == 1) {
            
            ship.updatePosition();
            ship.checkWeapon();
            ship.checkInvinc();
            respawnShip();
            keyCheck();
            
            // Updates positions of VectorSprites and deletes them at end of range
            
            for (int i = 0; i < asteroidList.size(); i++) {
                asteroidList.get(i).updatePosition();
            }
            
            for (int i = 0; i < bulletList.size(); i++) {
                bulletList.get(i).updatePosition();
                if (bulletList.get(i).counter == bulletDeathCounter) {
                    bulletList.remove(i);
                }
            }
            
            for (int i = 0; i < explosionList.size(); i++) {
                explosionList.get(i).updatePosition();
                if (explosionList.get(i).counter == 25) {
                    explosionList.remove(i);
                }
            }
            
            checkCollisions();
            checkDestruction();
            
        } else if (gameState == 2) {
            keyCheck();
        } else if (gameState == 3) {
            keyCheck();
        } else if (gameState == 4) {
            keyCheck();
        }
        
        repaint();
        
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }
    
    public void keyCheck() {
        
        if (ship.bursting == true && gameState == 1)
        {
            fireBullet();
        }
        
        if (upKey == true) {
            ship.accelerate();
        }
        
        if (rightKey == true) {
            ship.rotateRight();
        }
        
        if (leftKey == true) {
            ship.rotateLeft();
        }
        
        if (spaceKey == true) {
            if (gameState == 1) {
                fireBullet();
            } else if (gameState == 3) {
                gameState = 0;
                init();
            }
            
        }
        
        if (shiftKey == true) {
            ship.changeWeapon();
        }
        
        if (SKey == true) {
            if (gameState == 2) {
                gameState = 1;
                newLevel();
            } else if (gameState == 0) {
                gameState = 1;
            }
        }
        
        if (escKey == true) {
            System.exit(0);
        }
        
        if (FKey == true) {
            if (gameState == 1)
            {
                for (int i = 0; i < asteroidList.size(); i++)
                {
                    asteroidList.get(i).active = false;
                }
            }
        }
        
        if (PKey == true && pauseKeyActivated == false) {
            if (gameState == 1) {
                gameState = 4;
                pauseKeyActivated = true;
            } else if (gameState == 4) {
                gameState = 1;
                pauseKeyActivated = true;
            }
        }
        
        if (gameState == 0 && DKey == true && instrSwitched == false) {
            isMainInstr = !isMainInstr;
            instrSwitched = true;
        }
    }
    
    public boolean collision(VectorSprite object1, VectorSprite object2) {
        //Check if any of the ship points are inside the rock
        for (int i = 0; i < object1.drawShape.npoints; i++) {
            
            if (object2.drawShape.contains(object1.drawShape.xpoints[i], object1.drawShape.ypoints[i]) && object1.active && object2.active) {
                return true;
            }
            
        }
        
        //Check if any of the rock points are inside the rock
        for (int i = 0; i < object2.drawShape.npoints; i++) {
            
            if (object1.drawShape.contains(object2.drawShape.xpoints[i], object2.drawShape.ypoints[i]) && object1.active && object2.active) {
                return true;
            }
            
        }
        
        return false;
        
    }
    
    public void checkCollisions() {
        for (int i = 0; i < asteroidList.size(); i++) {
            
            if (collision(ship, asteroidList.get(i)) == true && ship.invincible == false) // Checks for ship+asteroid collisions
            {
                ship.hit();
                lives -= 1;
                credits -= 50;
                for (int e = 0; e < 10 * numDebris; e++) {
                    explosionList.add(new Debris(ship.xposition, ship.yposition));
                    isExplosionShip = true; // Tells the explosion graphic that the explosion is a ship
                }                       // blowing up, and not an asteroid being shot
            }
            
            for (int j = 0; j < bulletList.size(); j++) {
                if (collision(bulletList.get(j), asteroidList.get(i)) == true) // Checks for bullet+asteroid collisions
                {
                    bulletList.get(j).active = false;
                    asteroidList.get(i).health -= ship.damage;
                    
                    if (asteroidList.get(i).health <= 0) {
                        
                        asteroidList.get(i).active = false;
                        
                        for (int e = 0; e < numDebris; e++) {
                            explosionList.add(new Debris(asteroidList.get(i).xposition, asteroidList.get(i).yposition));
                            isExplosionShip = false; // Tells the explosion graphic that the explosion is an asteroid
                        }                            // being shot, not the ship
                        
                        credits += 10;
                        
                    }
                }
            }
        }
    }
    
    public void respawnShip() {
        if (ship.active == false && ship.counter >= 50) {
            ship.reset();
            ship.invincible = true;
        }
    }
    
    public void fireBullet() {
        if (ship.counter > ship.fireDelay && ship.active == true) {
            if (ship.weaponType == 1) // Adds multiple bullets, each at slightly different angles, if the multiple-shot gun is selected
            {
                if (ship.upgrades[0][0] == 0)
                {
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2 + ship.spreadModifier, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2 - ship.spreadModifier, ship.weaponType));
                }
                else if (ship.upgrades[0][0] == 1)
                {
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2 + 0.5*ship.spreadModifier, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2 + ship.spreadModifier, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2 - ship.spreadModifier, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2 - 0.5*ship.spreadModifier, ship.weaponType));
                }
                
                ship.counter = 0;
            }
            
            if (ship.weaponType == 2)
            {
                if (ship.upgrades[1][0] == 0)
                {
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2, ship.weaponType));
                }
                else if (ship.upgrades[1][0] == 1)
                {
                    bulletList.add(new Bullet(ship.xposition + 10*Math.cos(ship.angle), ship.yposition + 10*Math.sin(ship.angle), ship.angle - Math.PI / 2, ship.weaponType));
                    bulletList.add(new Bullet(ship.xposition - 10*Math.cos(ship.angle), ship.yposition - 10*Math.sin(ship.angle), ship.angle - Math.PI / 2, ship.weaponType));
                }
                
                ship.counter = 0;
            }
            
            if (ship.weaponType == 3)
            {
                if (ship.upgrades[2][0] == 0)
                {
                    bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2, ship.weaponType));
                    ship.counter = 0;
                }
                else if (ship.upgrades[2][0] == 1)
                {
                    ship.bursting = true;
                    if (ship.burstCounter < 3)
                    {
                        bulletList.add(new Bullet(ship.xposition, ship.yposition, ship.angle - Math.PI / 2, ship.weaponType));
                        ship.counter = ship.fireDelay - 2;
                        ship.burstCounter += 1;
                    }
                    else
                    {
                        ship.bursting = false;
                        ship.counter = 0;
                        ship.burstCounter = 0;
                    }
                }
                
                
            }
        }
    }
    
    public void checkDestruction() {
        for (int i = 0; i < asteroidList.size(); i++) {
            if (asteroidList.get(i).active == false) {
                if (asteroidList.get(i).size > 2) {
                    asteroidList.add(new Asteroid(asteroidList.get(i).xposition, asteroidList.get(i).yposition, asteroidList.get(i).size -= 1, asteroidList.get(i).xspeed, asteroidList.get(i).yspeed));
                    asteroidList.add(new Asteroid(asteroidList.get(i).xposition, asteroidList.get(i).yposition, asteroidList.get(i).size -= 1, asteroidList.get(i).xspeed, asteroidList.get(i).yspeed));
                }
                asteroidList.remove(i);
            }
        }
    }
    
    public void drawHUD() throws Exception {
        offg.setColor(Color.RED);
        
        if (asteroidList.isEmpty() == true) {
            endLevel();
        }
        
        if (lives == 0 && ship.active == true) {
            gameState = 3;
        }
        
        offg.setColor(Color.CYAN);
        
        if (ship.weaponType == 1) {
            offg.drawString("WEAPON: DE-82 Disruptor", 50, 50);
        }
        
        if (ship.weaponType == 2) {
            offg.drawString("WEAPON: Z-850 Vulcan", 50, 50);
        }
        
        if (ship.weaponType == 3) {
            offg.drawString("WEAPON: C-86 Ion Cannon", 50, 50);
        }
        
        offg.drawString("DAMAGE: " + ship.damage, 50, 70);
        offg.drawString("RATE OF FIRE: " + df.format((100 / (double) ship.fireDelay)), 50, 90);
        offg.drawString("DAMAGE / TIME: " + df.format(((double) ship.damage * 20 / (double) ship.fireDelay)), 50, 110);
        
        offg.drawString("LIVES: " + lives, 800, 550);
        offg.drawString("CREDITS: " + credits, 100, 550);
        offg.drawString("LEVEL: " + level, 450, 550);
    }
    
    public void drawExplosions() {
        if (isExplosionShip == true) {
            offg.setColor(Color.GREEN);
        }
        
        if (isExplosionShip == false) {
            offg.setColor(Color.WHITE);
        }
        
        for (int i = 0; i < explosionList.size(); i++) {
            explosionList.get(i).paint(offg);
        }
        
    }
    
    public void drawShip() {
        
        if (ship.invincible == true && (ship.invincCounter % 10) > 4) {
            offg.setColor(Color.GREEN);
        }
        
        if (ship.invincible == true && (ship.invincCounter % 10) <= 4) {
            offg.setColor(Color.gray);
        }
        
        if (ship.invincible == false) {
            offg.setColor(Color.GREEN);
        }
        
        if (lives == 0) {
            offg.setColor(Color.BLACK);
        }
        
        if (ship.active == true) {
            ship.paint(offg);
        }
    }
    
    public void drawMainMenu() throws IOException {
        
        BufferedImage img = null;
        if (isMainInstr == false) {
            try {
                img = ImageIO.read(new File("mainscreen.jpg"));
            } catch (IOException e) {
                offg.drawString("Failed to load image. Please check that you have all necessary files and restart the game.", 10, 10);
            }
        } else {
            try {
                img = ImageIO.read(new File("instructions.jpg"));
            } catch (IOException e) {
                offg.drawString("Failed to load image. Please check that you have all necessary files and restart the game.", 10, 10);
            }
        }
        
        offg.drawImage(img, 0, 0, null);
        
        offg.setColor(Color.WHITE);
        
        offg.drawString("Press S to start the game.", 550, 50);
        offg.drawString("Press ESC to quit.", 550, 70);
        offg.drawString("Press D to view / hide the instructions.", 550, 90);
    }
    
    public void drawEndScreen() {
        offg.setColor(Color.RED);
        offg.drawString("You Lose! You made it to level " + level + ".", 400, 300);
        offg.drawString("Press SPACE to return to the main menu, or ESC to quit.", 300, 320);
    }
    
    public void drawShop() {
        offg.setColor(Color.CYAN);
        offg.drawString("Congrats, you completed level " + level + "! Press S to advance to the next level", 300, 100);
        
        offg.setColor(Color.YELLOW);
        
        offg.drawString("DE-82 Disruptor", 100, 180);
        offg.drawString("DAMAGE: ", 100, 200);
        offg.drawString("RATE OF FIRE: ", 100, 220);
        
        for (int i = 0; i < 5; i++)
        {
            if (i < ship.upgrades[0][1])
            {
                offg.fillOval(10*i + 200, 190, 9, 9);
            }
            else
            {
                offg.drawOval(10*i + 200, 190, 8, 8);
            }
        }
        
        if (shopSelection == 2)
            offg.setColor(Color.WHITE);
        else
            offg.setColor(Color.YELLOW);
        
        offg.fillRect(255, 213, 12, 2);
        offg.fillRect(260, 208, 2, 12);
        offg.drawString("UPGRADE", 275, 220);
        
        if (shopSelection == 5)
            offg.setColor(Color.WHITE);
        else
            offg.setColor(Color.YELLOW);
        
        offg.fillRect(260, 388, 2, 12);
        offg.fillRect(255, 393, 12, 2);
        offg.drawString("UPGRADE", 275, 400);
        
        if (shopSelection == 6)
            offg.setColor(Color.WHITE);
        else
            offg.setColor(Color.YELLOW);
        
        offg.fillRect(255, 413, 12, 2);
        offg.fillRect(260, 408, 2, 12);
        offg.drawString("UPGRADE", 275, 420);
        
        if (shopSelection == 4)
            offg.setColor(Color.WHITE);
        else
            offg.setColor(Color.YELLOW);
        
        offg.fillRect(255, 313, 12, 2);
        offg.fillRect(260, 308, 2, 12);
        offg.drawString("UPGRADE", 275, 320);
        
        if (shopSelection == 3)
            offg.setColor(Color.WHITE);
        else
            offg.setColor(Color.YELLOW);
        
        offg.fillRect(260, 288, 2, 12);
        offg.fillRect(255, 293, 12, 2);
        offg.drawString("UPGRADE", 275, 300);
        
        if (shopSelection == 1)
            offg.setColor(Color.WHITE);
        else
            offg.setColor(Color.YELLOW);
        
        offg.fillRect(260, 188, 2, 12);
        offg.fillRect(255, 193, 12, 2);
        offg.drawString("UPGRADE", 275, 200);
        
        offg.setColor(Color.YELLOW);
        
        
        for (int i = 0; i < 5; i++)
        {
            if (i < ship.upgrades[0][2])
            {
                offg.fillOval(10*i + 200, 210, 9, 9);
            }
            else
            {
                offg.drawOval(10*i + 200, 210, 8, 8);
            }
        }
        
        offg.drawString("Z-850 Vulcan", 100, 280);
        offg.drawString("DAMAGE: ", 100, 300);
        offg.drawString("RATE OF FIRE: ", 100, 320);
        
        for (int i = 0; i < 5; i++)
        {
            if (i < ship.upgrades[1][1])
            {
                offg.fillOval(10*i + 200, 290, 9, 9);
            }
            else
            {
                offg.drawOval(10*i + 200, 290, 8, 8);
            }
        }
        
        if (shopSelection == 3)
        {
            offg.setColor(Color.WHITE);
            
            offg.fillRect(260, 288, 2, 12);
            offg.fillRect(255, 293, 12, 2);
            offg.drawString("UPGRADE", 275, 300);
            
            offg.setColor(Color.YELLOW);
            
            offg.fillRect(255, 313, 12, 2);
            offg.fillRect(260, 308, 2, 12);
            offg.drawString("UPGRADE", 275, 320);
        }
        
        if (shopSelection == 4)
        {
            offg.setColor(Color.YELLOW);
            
            offg.fillRect(260, 288, 2, 12);
            offg.fillRect(255, 293, 12, 2);
            offg.drawString("UPGRADE", 275, 300);
            
            offg.setColor(Color.WHITE);
            
            offg.fillRect(255, 313, 12, 2);
            offg.fillRect(260, 308, 2, 12);
            offg.drawString("UPGRADE", 275, 320);
        }
        
        for (int i = 0; i < 5; i++)
        {
            if (i < ship.upgrades[1][2])
            {
                offg.fillOval(10*i + 200, 310, 9, 9);
            }
            else
            {
                offg.drawOval(10*i + 200, 310, 8, 8);
            }
        }
        
        offg.drawString("C-86 Ion Cannon", 100, 380);
        offg.drawString("DAMAGE: ", 100, 400);
        offg.drawString("RATE OF FIRE: ", 100, 420);
        
        for (int i = 0; i < 5; i++)
        {
            if (i < ship.upgrades[2][1])
            {
                offg.fillOval(10*i + 200, 390, 9, 9);
            }
            else
            {
                offg.drawOval(10*i + 200, 390, 8, 8);
            }
        }
        
        if (shopSelection == 5)
        {
            offg.setColor(Color.WHITE);
            
            offg.fillRect(260, 388, 2, 12);
            offg.fillRect(255, 393, 12, 2);
            offg.drawString("UPGRADE", 275, 400);
            
            offg.setColor(Color.YELLOW);
            
            offg.fillRect(255, 413, 12, 2);
            offg.fillRect(260, 408, 2, 12);
            offg.drawString("UPGRADE", 275, 420);
        }
        
        if (shopSelection == 6)
        {
            offg.setColor(Color.YELLOW);
            
            offg.fillRect(260, 388, 2, 12);
            offg.fillRect(255, 393, 12, 2);
            offg.drawString("UPGRADE", 275, 400);
            
            offg.setColor(Color.WHITE);
            
            offg.fillRect(255, 413, 12, 2);
            offg.fillRect(260, 408, 2, 12);
            offg.drawString("UPGRADE", 275, 420);
        }
        
        for (int i = 0; i < 5; i++)
        {
            if (i < ship.upgrades[2][2])
            {
                offg.fillOval(10*i + 200, 410, 9, 9);
            }
            else
            {
                offg.drawOval(10*i + 200, 410, 8, 8);
            }
        }
    }
    
    public void whichBulletType() {
        // Sets the bullet type to match the ship's weapon type, so the correct bullet is drawn
        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).weaponType = ship.weaponType;
        }
    }
    
    public void endLevel() {
        
        for (int i = 0; i < asteroidList.size(); i++) {
            asteroidList.get(i).active = false;
        }
        
        for (int i = 0; i < asteroidList.size(); i++) {
            asteroidList.remove(asteroidList.get(i));
        }
        
        for (int i = 0; i < explosionList.size(); i++) {
            explosionList.get(i).active = false;
        }
        
        for (int i = 0; i < explosionList.size(); i++) {
            explosionList.remove(explosionList.get(i));
        }
        
        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.get(i).active = false;
        }
        
        for (int i = 0; i < bulletList.size(); i++) {
            bulletList.remove(bulletList.get(i));
        }
        
        gameState = 2;
        
    }
    
    public void newLevel() {
        level++;
        lives += Math.round(level / 10) + 1;
        
        ship.invincible = true;
        
        ship.reset();
        
        numAsteroids = 2 + (level * 2);
        
        for (int i = 0; i < numAsteroids; i++) {
            asteroidList.add(new Asteroid());
        }
    }
    
    public void initStarPositions() {
        for (int i = 0; i < 900; i++) {
            for (int n = 0; n < 600; n++) {
                starPositionSeed = (int) (Math.random() * 2000);
                
                if (starPositionSeed > 1998) {
                    starPositions[i][n] = true;
                } else {
                    starPositions[i][n] = false;
                }
            }
        }
    }
}