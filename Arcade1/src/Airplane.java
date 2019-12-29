 /*
* File: Airplane.java
* Description: Enemy airplane
* Author: Alice Yu 
* Date-written: 
*/
import java.util.Random;
 
public class Airplane extends FlyingObject {

private static final int AIRPLANE_SCORE = 5;
private static final int SPEED = 3;

 
public Airplane(){
  super();
  
  image = ShootingGame.airplane;
  
  width = image.getWidth();
  height = image.getHeight();
  
  Random rand = new Random();
  x = rand.nextInt(ShootingGame.WIDTH - width);
  y = -height;   
 }
 
 public int getScore() { 
  return AIRPLANE_SCORE;
 }

 //over ride outOfBounds in parent class FlyingObject
 @Override
 public  boolean outOfBounds() { 
  return y>ShootingGame.HEIGHT;
 }

 //implementation of an abstract method
 public void step() { 
  y += SPEED;
 }
 
}
