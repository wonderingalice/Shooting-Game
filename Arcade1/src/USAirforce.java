/*
* File: USAirforce.java
* Description: player's airplane
* Author: Alice Yu
* Date-written:  3/6/2019
*/

import java.awt.image.BufferedImage;  
   
// player
public class USAirforce extends FlyingObject{  
  
    private static final int INIT_LIFE = 30;
      
    private BufferedImage[] images = {};  // USAirforce image 
    private int index = 0;                // index of USAirforce image  
      
    private boolean doubleFire = false;   
    private int life;     
      
    public USAirforce(){
        super();
      
        image = ShootingGame.USAirforce0;   
        
        width = image.getWidth();  
        height = image.getHeight();    
        
        x = ShootingGame.WIDTH/2 - width/2;  
        y = ShootingGame.HEIGHT - height;  
         
        life = INIT_LIFE;  
        doubleFire = false;     
        images = new BufferedImage[]{ShootingGame.USAirforce0, ShootingGame.USAirforce1}; //two USAirforce images, used for animation 
        
        index = 0;
    }  
 
    public void setDoubleFire(boolean doubleFire) {  
        this.doubleFire = doubleFire;  
    }  
     
    public void addLife(){  
        life++;  
    }  
      
    public void subtractLife(){   
        life--;  
    }  
      
    public int getLife(){  
        return life;  
    }

    public void setLife(int lf){
        life = lf;
    };

    // USAirforce move with the mouse.
    // just do animation
    //implementation of an abstract method
    public void step() {  
         if(images.length>0){  
            image = images[index++/10%images.length];  //switch USAirforce0USAirforce1
        }  
    }  
           
   public void moveTo(int x,int y){     
        this.x = x - width/2;  
        this.y = y - height/2;  
    }  
  
   public Bullet[] shoot(){     
        int xStep = width/4;       
        int yStep = Bullet.Y_STEP;   
        if(doubleFire){  // double fire
            Bullet[] bullets = new Bullet[2];  
            bullets[0] = new Bullet(x+xStep,y-yStep);  // caculate the bullet's postion based on USAirforce 
            bullets[1] = new Bullet(x+3*xStep,y-yStep); 
            return bullets;  
        }else{  //single fire
            Bullet[] bullets = new Bullet[1];  
            bullets[0] = new Bullet(x+2*xStep,y-yStep);    
            return bullets;  
        }  
    }  
       
    // collide with enemy airplane?
    public boolean hit(FlyingObject other){  
        // two objects collide when they are too close  
        int x1 = other.x - this.width/2 - other.width/2;   
        int x2 = other.x + this.width/2 + other.width/2;   
        int y1 = other.y - this.height/2 - other.height/2;   
        int y2 = other.y + this.height/2 + other.height/2;
          
        return this.x>x1 && this.x<x2 && this.y>y1 && this.y<y2;     
    }
    public boolean outOfBounds(){return false;};
}
