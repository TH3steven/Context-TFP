package main.java.nl.tudelft.contextproject.model;

import javafx.scene.image.Image;

public class Shot {
    private int shotNum;
    private Image img;
    
    public Shot(int shotNum, Image image) {
        this.shotNum = shotNum;
        img = image;
    }
    
    public int getNum() {
        return shotNum;
    }
    
    public void setNum(int number) {
        shotNum = number;
    }
    
    public Image getImage() {
        return img;
    }
    
    public void setImage(Image image) {
        img = image;
    }
    
    public String toString() {
        return "Shot " + shotNum;
    }
}
