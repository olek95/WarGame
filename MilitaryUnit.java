package wargame;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.ImageView;

public abstract class MilitaryUnit implements Runnable{
    protected ImageView img;
    protected int attack, life, speed;
    public void run(){
        do{
            try {
                img.setLayoutX(speed + img.getLayoutX());
                synchronized(this){
                    wait(100);
                }
            }catch(InterruptedException e){
                Logger.getLogger(MilitaryUnit.class.getName()).log(Level.SEVERE, null, e);
            }
        }while(img.getLayoutX() + img.getImage().getWidth()< WarGameFXMLController.getBattlefieldWidth() - WarGameFXMLController.getMargin());
    }
    public ImageView getImg(){
        return img;
    }
}
