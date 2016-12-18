package wargame;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class MilitaryUnit extends Task{
    protected ImageView img;
    protected int attack, life, initialLife, speed, price;
    private boolean isPlayerUnit, arrived;
    private GameManager manager = new GameManager();
    protected MilitaryUnitType type;
    public Void call(){
        if(Player.getUnits().contains(this)) isPlayerUnit = true;
        do{
            try {
                arrived = !((isPlayerUnit && img.getLayoutX() + img.getImage().getWidth() < WarGameFXMLController.getBattlefieldWidth() - WarGameFXMLController.getMargin())
                || (!isPlayerUnit && img.getLayoutX() > WarGameFXMLController.getMargin()));
                if(!arrived){
                    if(isPlayerUnit) img.setLayoutX(speed + img.getLayoutX());
                    else img.setLayoutX(img.getLayoutX() - speed);
                }
                ImageView neighbour;
                synchronized(this){
                    do{
                        wait(100);
                        neighbour = stop();
                        for(Node n : WarGameFXMLController.getController().getBattlefieldPane().getChildren()) System.out.print(((ImageView)n).getImage() + " ");
                        System.out.println(isPlayerUnit + " " + img.getImage() + " " + neighbour + " " + life);
                        if(isPlayerUnit && neighbour != null && !Player.hasImageView(neighbour)){
                            wait(200);
                            attack(neighbour);
                        }else{
                            if(arrived && isPlayerUnit && neighbour == null){
                                wait(200);
                                attack(null);
                            }
                        }
                    }while(neighbour != null);
                }
            }catch(InterruptedException e){
                Logger.getLogger(MilitaryUnit.class.getName()).log(Level.SEVERE, null, e);
            }
        }while(life > 0);
        return null;
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
        MilitaryUnit enemy;
        if(neighbour != null) enemy = Enemy.getUnit(neighbour);
        else enemy = Enemy.getBase();
        battleTextArea.textProperty().set(t1 + type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life + t2);
        Random rand = new Random(); 
        if(rand.nextInt(2) == 0){
            life -= enemy.attack; 
            battleTextArea.textProperty().set(t1 + type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life + t2);
            if(life <= 0){
                destroy(this);
                Player.remove(this);
                System.out.println("ZNISZCZONY");
            }
        }
        if(life > 0 && rand.nextInt(2) == 0){
            enemy.life -= attack;
            battleTextArea.textProperty().set(t1 + type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life + t2);
            if(enemy.life <= 0){
                manager.changePoints(enemy.initialLife);
                destroy(enemy);
                Enemy.remove(enemy);
                enemy = null;
                neighbour = null;
                System.out.println("ZNISZCZONY");
            }
        }
    }
    public void destroy(MilitaryUnit unit){
        unit.img.setImage(null);
        unit.img = null;
        unit = null;
    }
}
