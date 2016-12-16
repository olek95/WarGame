package wargame;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class MilitaryUnit implements Runnable{
    protected ImageView img;
    protected int attack, life, speed;
    private boolean isPlayerUnit;
    protected MilitaryUnitType type;
    public void run(){
        if(Player.getUnits().contains(this)) isPlayerUnit = true;
        do{
            try {
                if(isPlayerUnit) img.setLayoutX(speed + img.getLayoutX());
                else img.setLayoutX(img.getLayoutX() - speed);
                ImageView neighbour;
                synchronized(this){
                    do{
                        wait(100);
                        neighbour = stop();
                        if(isPlayerUnit && neighbour != null && !Player.hasImageView(neighbour)){
                            attack(neighbour);
                        }
                    }while(neighbour != null);
                }
            }catch(InterruptedException e){
                Logger.getLogger(MilitaryUnit.class.getName()).log(Level.SEVERE, null, e);
            }
            System.out.println((!isPlayerUnit && img.getLayoutX() > WarGameFXMLController.getMargin()));
        }while((isPlayerUnit && img.getLayoutX() + img.getImage().getWidth() < WarGameFXMLController.getBattlefieldWidth() - WarGameFXMLController.getMargin())
                || (!isPlayerUnit && img.getLayoutX() > WarGameFXMLController.getMargin()));
    }
    public ImageView getImg(){
        return img;
    }
    public ImageView stop(){
        for(Node n : WarGameFXMLController.getController().getBattlefieldPane().getChildren()){
            if(!img.equals((ImageView)n) && img.getLayoutY() == n.getLayoutY()){
                Bounds b = n.getBoundsInParent();
                Image unitImage = img.getImage();
                ArrayList<MilitaryUnit> playerUnits = Player.getUnits();
                if(playerUnits.contains(this) && unitImage.getWidth() + img.getLayoutX() > b.getMinX() && unitImage.getWidth() + img.getLayoutX() < b.getMaxX())
                    return (ImageView)n;
                if(!playerUnits.contains(this) && img.getLayoutX() > b.getMinX() && img.getLayoutX() < b.getMaxX())
                    return (ImageView)n;
            }
        }
        return null;
    }
    public void attack(ImageView neighbour){
        double y = img.getLayoutY();
        int row;
        if(y == 0) row = 1;
        else if(y == 97) row = 2;
        else if(y == 194) row = 3;
        else row = 4;
        TextArea battleTextArea = WarGameFXMLController.getController().getBattleTextArea();
        String text = battleTextArea.getText();
        int i = text.indexOf(row + "");
        String t1 = text.substring(0, i + 2); // +2 bo chcę objąć nr wiersza wraz z kropką za nim
        String t2 = text.substring(i);
        t2 = t2.substring(t2.indexOf("\n"));
        battleTextArea.setText(t1 + type.name() + t2);
    }
}
