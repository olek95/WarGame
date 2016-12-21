package wargame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Obiekt klasy <code>Helicopter</code> reprezentuje jednostkę typu helikopter.
 * @author AleksanderSklorz
 */
public class Helicopter extends MilitaryUnit{
    public Helicopter(){
        img = new ImageView(new Image("file:images/helicopter.png"));
        attack = 10;
        life = initialLife = 20;
        speed = 15;
        price = 10;
        type = MilitaryUnitType.HELICOPTER;
    }
}
