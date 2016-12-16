package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Helicopter extends MilitaryUnit{
    public Helicopter(){
        this.img = new ImageView(new Image("file:images/helicopter.png"));
        this.attack = 10;
        this.life = 20;
        this.speed = 15;
        this.type = MilitaryUnitType.HELICOPTER;
    }
}
