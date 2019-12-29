/*
 * File: ShootingGame.java
 * Description: the control for this game
 * Author: Alice Yu 
 * Date-written:  3/6/2019
 */

import java.awt.*;  
import java.util.*;  
import javax.imageio.*;  
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Timer;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;


public class ShootingGame extends JPanel
    implements JavaArcade{
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    
    private static final int START = 0; 
    private static final int RUNNING = 1;
    private static final int PAUSE = 2;
    private static final int OUTOFBOUND = 3;
    private static final int GAME_OVER = 4;

    private static final String INSTRUCITON = "This is an one player shooting game. \n" +
            "Your goal is to survive as long as possible without being hit. \n" +
             "The shooter will follow the movement of your mouse. \n" +
            "You will loose a life if you are hit by an enemy plane or bee. \n" +
            "You will get 5 points for every enemy plane you shoot down. \n" +
            "For every bee you shoot, you might get an extra life or double bullets.";
    private static final String AUTHOR = "This game is made by: \n" +
            "Alice Yu, D Block AP Java Class";

    private static final String AUDIOFILE = "Off Limits.wav";
    
    private int state; //START, RUNNING, PAUSE, GAME_OVER
    
    private String name = "Guest";
    private int score = 0; 
    private Timer timer; 
    private int interval = 20;
    private int flyEnteredIndex = 0;
    private int shootIndex = 0; 
    private int nameIndex = 0;
    
    private FlyingObject[] flyings = {}; // enemies and bees
    private Bullet[] bullets = {};  
    private USAirforce usAirforce = new USAirforce(); // player 
    private Top10Score topScore = new Top10Score();
    
    private static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage USAirforce0;
    public static BufferedImage USAirforce1;
    public static BufferedImage pause;
    public static BufferedImage gameover;

    private Clip clip;

    private static boolean isRunning = false;

    static { // images
        try {
            background = ImageIO.read(ShootingGame.class.getResource("background.png"));
            start = ImageIO.read(ShootingGame.class.getResource("start.png"));
            airplane = ImageIO.read(ShootingGame.class.getResource("airplane.png"));
            bee = ImageIO.read(ShootingGame.class.getResource("bee.png"));
            bullet = ImageIO.read(ShootingGame.class.getResource("bullet.png"));
            USAirforce0 = ImageIO.read(ShootingGame.class.getResource("myplane0.png"));
            USAirforce1 = ImageIO.read(ShootingGame.class.getResource("myplane1.png"));
            pause = ImageIO.read(ShootingGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootingGame.class.getResource("gameover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ShootingGame(int width, int lenght){
        super();
        setSize(WIDTH, HEIGHT);

        try{
            loadBackgroundMusic();
            init();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    

    private void loadBackgroundMusic(){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getClassLoader().getResource(AUDIOFILE);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, AUDIOFILE+ " is missing", "Music File",
                    JOptionPane.INFORMATION_MESSAGE, null);
        }
    }


    private void startBackgroundMusic(){
        try {
            if(clip != null){
                // start audio clip
                clip.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic(){
        try {
           if(clip != null){
               // close audio clip
               clip.stop();
           }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loopBackgroundMusic(){
        try {
            if(clip != null){
                if(!clip.isRunning()){
                   clip.loop(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        try {
            topScore.loadScore();//load recorded 10 scores.

            //algorithm 1 - mouse events
            MouseAdapter mouseAdp = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) { // mouse move
                    if (state == RUNNING) {
                        int x = e.getX();
                        int y = e.getY();
                        usAirforce.moveTo(x, y);
                    }
                }


                @Override
                public void mouseEntered(MouseEvent e) { //mouse moves in
                    if (state == OUTOFBOUND && isRunning ) {
                        state = RUNNING; //resume
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) { //mouse moves out
                    if (state == RUNNING) {
                        state = OUTOFBOUND;
                    }
                }
            }; // mouseAdp end

            this.addMouseListener(mouseAdp);       //click listener
            this.addMouseMotionListener(mouseAdp); //movement listener

            //algorithm 2 - timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (state == RUNNING) {

                        try {
                            enterAction(); // generate flyingobjects
                            stepAction(); // flyingobjects move
                            shootAction(); // USAirforce shoots
                            hitAction(); // hit
                            outOfBoundsAction(); // flyingobjects move out of range
                            checkCollisionGameOverAction(); // USAirforce and flyingobjects collide
                            if(!isGameOver())
                                loopBackgroundMusic();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    repaint(); //refresh
                }

            }, interval, interval);
        } catch (Exception e)   {
            e.printStackTrace();
        }

        score = 0;
        state = START;
    }
    

// draw everything
    @Override
    public void paint(Graphics g) { 
        g.drawImage(background, 0, 0, null); //draw background
        paintUSAirforce(g);                  //draw my plane
        paintBullets(g);                     //draw bullets
        paintFlyingObjects(g);               //draw enemy aircraft and bees 
        paintScore(g);                       //draw name,score and life
        paintState(g);                       //show state
    }
    
//draw my plane
    private void paintUSAirforce(Graphics g) {
        g.drawImage(usAirforce.getImage(), usAirforce.getX(), usAirforce.getY(), null); 
    } 
    
//draw bullets
    private void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) { 
            Bullet b = bullets[i]; 
            g.drawImage(b.getImage(), b.getX() - b.getWidth() / 2, b.getY(), 
                        null); 
        } 
    } 
    
//draw enemy aircrafts and bees
    private void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject f = flyings[i]; 
            g.drawImage(f.getImage(), f.getX(), f.getY(), null); 
        } 
    } 
    
//draw name, score and life
    private void paintScore(Graphics g) {
        int x = 10;  
        int y = 25; 
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 22); 
        g.setColor(new Color(0xFF0000)); 
        g.setFont(font); 
        
        //name
        g.drawString(name, x, y);
        
        //score
        y=y+20; 
        g.drawString("SCORE:" + score, x, y); 
        
        //life
        y=y+20; 
        g.drawString("LIFE:" + usAirforce.getLife(), x, y); 
    } 
    
    //update state
    private void paintState(Graphics g) {
        switch (state) { 
            case START:  
                g.drawImage(start, 0, 0, null); 
                break; 
            case PAUSE:  
                g.drawImage(pause, 0, 0, null); 
                break;
            case GAME_OVER:
                g.drawImage(gameover, 0, 0, null); 
                break; 
        } 
    } 

//Set player's username

    public void setName(String name){
        this.name = name;
    }
    
    public void setUsername(String name) {
        nameIndex++; 
        if (nameIndex % 40 == 0) {        
            setName(name);
        }
    }    
    
    
//generate fly objects  
    public void enterAction() { 
        flyEnteredIndex++; 
        if (flyEnteredIndex % 40 == 0) { 
            FlyingObject obj = generateNewObj(); //create a fly object 
            flyings = Arrays.copyOf(flyings, flyings.length + 1); //enlarge
            flyings[flyings.length - 1] = obj; //add
        } 
    } 
    
// every object step forward. 
    public void stepAction() { 
        //enemy aircrafts and bees step forward
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject f = flyings[i]; 
            f.step(); 
        } 
        
        // bullets move forward
        for (int i = 0; i < bullets.length; i++) { 
            Bullet b = bullets[i]; 
            b.step(); 
        } 
        usAirforce.step(); //my plane - animation 
    } 
    
    //shoot USAirforce generate bullets
    public void shootAction() { 
        shootIndex++; 
        if (shootIndex % 30 == 0) {  
            Bullet[] bs = usAirforce.shoot(); //bullets shot by my plane 
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length); //enlarge 
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, 
                             bs.length); //add 
        } 
    } 
    
//if bullets hit objects
    public void hitAction() { 
        for (int i = 0; i < bullets.length; i++) { 
            Bullet b = bullets[i]; 
            bang(b); //hit?
        } 
    } 
    
    //clean up all objects that out of range
    public void outOfBoundsAction() { 
        //clean up enemy aircrafts and bees
        int index = 0; 
        FlyingObject[] flyingLives = new FlyingObject[flyings.length]; //temporary storage for living enemies and bees
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject f = flyings[i]; 
            if (!f.outOfBounds()) { 
                flyingLives[index++] = f; //save alive objects 
            } 
        } 
        flyings = Arrays.copyOf(flyingLives, index); //copy all living objects back 
        
        //clean up bullets
        index = 0;
        Bullet[] bulletLives = new Bullet[bullets.length]; 
        for (int i = 0; i < bullets.length; i++) { 
            Bullet b = bullets[i]; 
            if (!b.outOfBounds()) { 
                bulletLives[index++] = b; 
            } 
        } 
        bullets = Arrays.copyOf(bulletLives, index); //copy bullets in the range back
    } 
    
// check collision
    public void checkCollisionGameOverAction() { 
        for (int i = 0; i < flyings.length; i++) { 
            int index = -1; 
            FlyingObject obj = flyings[i]; 
            if (usAirforce.hit(obj)) { //collide ? 
                usAirforce.subtractLife(); //reduce lives 
                usAirforce.setDoubleFire(false); //lower fire 
                index = i; //the index of the object collide with my plane 
            } 
            if (index != -1) { 
                FlyingObject t = flyings[index]; 
                flyings[index] = flyings[flyings.length - 1]; // replace the collidate object with the last one
                flyings[flyings.length - 1] = t; 
                
                flyings = Arrays.copyOf(flyings, flyings.length - 1); //remove the last one
            } 
        } 
     } 
    
// game over?
    public boolean isGameOver() throws Exception { 
        if( usAirforce.getLife() <= 0 )
        {
            state = GAME_OVER;
            isRunning = false;

            topScore.saveScore(score, name);//save sorted scores. limit to 10
            flyings = new FlyingObject[0];
            bullets = new Bullet[0];
            usAirforce = new USAirforce();

            timer.cancel();
            timer.purge();

            score = 0;
            init();
            return true;
        }
        else
            return false;
    } 
    
    // bullets hit objects
    public void bang(Bullet bullet) { 
        int index = -1;  
        for (int i = 0; i < flyings.length; i++) { 
            FlyingObject obj = flyings[i]; 
            if (obj.shootBy(bullet)) {  
                index = i;  
                break; 
            } 
        } 
        if (index != -1) {  
            FlyingObject one = flyings[index]; 
            
            FlyingObject temp = flyings[index]; //replace the object with the last one 
            flyings[index] = flyings[flyings.length - 1]; 
            flyings[flyings.length - 1] = temp; 
            
            flyings = Arrays.copyOf(flyings, flyings.length - 1); //remove the last one 
            
            // award/score
            if (one instanceof Airplane) { //if the object is an enemy aircraft 
                Airplane e = (Airplane) one; // cast 
                score += e.getScore(); //add score 
            } else { //it is a bee 
                Bee a = (Bee) one; //cast
                int type = a.getAwardType(); //award type
                switch (type) { 
                    case Bee.DOUBLE_FIRE: 
                        usAirforce.setDoubleFire(true); //set double fire 
                        break; 
                    case Bee.LIFE: 
                        usAirforce.addLife(); //increase life 
                        break; 
                } 
            } 
        } 
    } 
    
//generate a FlyingOjbect randomly
    public static FlyingObject generateNewObj() { 
        Random random = new Random(); 
        int type = random.nextInt(20); // [0,20) 
        if (type < 4) { 
            return new Bee(); 
        } else { 
            return new Airplane(); 
        } 
    }

    /* This method should return true if your game is in a "start" state, it should return false if
     * your game is in a "paused" state or "stopped" or "unstarted" */

    public boolean running()
    {
        return  isRunning;
    };

    /* This method should start your game, it should also set a global boolean value so that your running method
     * can return the appropriate value */

    public void startGame() {
        startBackgroundMusic();
        state = RUNNING;
        isRunning = true;

    }// end of start

    /*This method should return the name of your game */
    public String getGameName(){
        return "Arcade CS AP";
    };

    /* This method should stop your timers but save your score, it should set a boolean value to indicate
     *the game is not running, this value will be returned by running method */

    public void pauseGame(){
        if(isRunning){
            state = PAUSE;
            isRunning = false;
        }
    };

    /* This method should return your instructions */
    public String getInstructions(){
        return INSTRUCITON;
    }

    /* This method should return the author(s) of the game */
    public String getCredits(){
        return AUTHOR;
    };

    /* This method should return the highest score played for this game */
    public String getHighScore(){
        if(topScore != null)
            return topScore.getHighestScore();
        return "0";
    };

    /* This method should stop the timers, reset the score, and set a running boolean value to false */
    public void stopGame(){
        usAirforce.setLife(0);
        stopBackgroundMusic();
    };

    /* This method should return the current players number of points */
    public int getPoints() //The player will get 5 points for every enemy plane he shot.
    {
        return score;
    }
}
    

