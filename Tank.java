package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tank extends MilitaryUnit{
    public Tank(){
        img = new ImageView(new Image("file:images/tank.png"));
        attack = 15;
        life = initialLife = 30;
        speed = 10;
        price = 15;
        type = MilitaryUnitType.TANK;
    }
}
