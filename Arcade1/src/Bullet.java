/*
* File: Bullet.java
* Description: Player's bullets
* Author: Alice Yu
* Date-written:  3/6/2019
*/ 

public class Bullet extends FlyingObject {  
    private static final int SPEED = 3;  
    public  static final int Y_STEP = 20;
      
    public Bullet(int x,int y){  
        super();
        this.x = x;  
        this.y = y;  
        this.image = ShootingGame.bullet;  
    }

    //over ride outOfBounds in parent class FlyingObject
    @Override  
    public boolean outOfBounds() {  
        return y<-height;  
    }

    //implementation of an abstract method
    public void step(){     
        y-=SPEED;  
    } 

    public void setBullet(int x,int y){  
        this.x = x;  
        this.y = y;  
     }  
}
