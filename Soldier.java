package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Soldier extends MilitaryUnit{
    public Soldier(){
        this.img = new ImageView(new Image("file:images/soldier.png"));
        this.attack = 5;
        this.life = 10;
        this.speed = 5;
    }
}
