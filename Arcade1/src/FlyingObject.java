/*
* File: FlyingObject.java
* Description: Basic class for all moving objects
* Author: Alice Yu
* Date-written:  3/6/2019
*/

import java.awt.image.BufferedImage;  
  
public abstract class FlyingObject {
    protected int x;      
    protected int y;      
    protected int width;      
    protected int height;   
    protected BufferedImage image;     
  
    public int getX() {  
        return x;  
    }  
  
    public void setX(int x) {  
        this.x = x;  
    }  
  
    public int getY() {  
        return y;  
    }  
  
    public void setY(int y) {  
        this.y = y;  
    }  
  
    public int getWidth() {  
        return width;  
    }  
  
    public void setWidth(int width) {  
        this.width = width;  
    }  
  
    public int getHeight() {  
        return height;  
    }  
  
    public void setHeight(int height) {  
        this.height = height;  
    }  
  
    public BufferedImage getImage() {  
        return image;  
    }  
  
    public void setImage(BufferedImage image) {  
        this.image = image;  
    }  
  
    public abstract boolean outOfBounds();
 /*   {
       return false;
    }*/

    //move forward
    public abstract void step();
 
    public boolean shootBy(Bullet bullet){  
        int x = bullet.x;   
        int y = bullet.y;  
        return this.x<x && x<this.x+width && this.y<y && y<this.y+height;  
    }   
}
