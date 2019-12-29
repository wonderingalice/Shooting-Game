/*
* File: Bee.java
* Description: Bee - have awards
* Author: Alice Yu 
* Date-written: 
*/
import java.util.Random;  
  
public class Bee extends FlyingObject{  
    public static final int DOUBLE_FIRE = 0;    
    public static final int LIFE = 1;     
      
    private int xSpeed = 1;     
    private int ySpeed = 2;     
    private int awardType;      
      
    public Bee(){  
        super();
        
        image = ShootingGame.bee;  
        
        width = image.getWidth();  
        height = image.getHeight();  

        Random rand = new Random();  
        x = rand.nextInt(ShootingGame.WIDTH - width); 
        y = -height; 
   
        awardType = rand.nextInt(LIFE+1);   //initial award type
    }  

    public int getAwardType(){  
        Random rand = new Random();  
        awardType = rand.nextInt(LIFE+1);   // randomly select award type
        return awardType;  
    }

    //over ride outOfBounds in parent class FlyingObject
    @Override  
    public boolean outOfBounds() {  
        return y>ShootingGame.HEIGHT;  
    }  

    //implementation of an abstract method
    public void step() {        
        x += xSpeed;  
        y += ySpeed;  
        if(x > ShootingGame.WIDTH-width){    
            xSpeed = -1;  //move back
        }  
        if(x < 0){  
            xSpeed = 1;  //move forward
        }  
    }  
}
