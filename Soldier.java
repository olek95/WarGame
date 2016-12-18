package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Soldier extends MilitaryUnit{
    public Soldier(){
        img = new ImageView(new Image("file:images/soldier.png"));
        attack = 5;
        life = initialLife = 10;
        speed = 5;
        price = 5;
        type = MilitaryUnitType.SOLDIER;
    }
}
