package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tank extends MilitaryUnit{
    public Tank(){
        this.img = new ImageView(new Image("file:images/tank.png"));
        this.attack = 15;
        this.life = 30;
        this.speed = 10;
        this.type = MilitaryUnitType.TANK;
    }
}
