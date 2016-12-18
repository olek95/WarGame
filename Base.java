package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Base extends MilitaryUnit{
    private static boolean playerUnit = true;
    public Base(){
        if(playerUnit){
            img = new ImageView(new Image("file:images/base1.png"));
            playerUnit = !playerUnit;
        }else img = new ImageView(new Image("file:images/base2.png"));
        attack = 0;
        life = initialLife = 100;
        speed = 0;
        price = 0;
        type = MilitaryUnitType.BASE;
    }
}
