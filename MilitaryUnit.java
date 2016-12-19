package wargame;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class MilitaryUnit extends Task{
    protected ImageView img;
    protected int attack, life, initialLife, speed, price;
    private boolean isPlayerUnit, arrived;
    private GameManager manager = new GameManager();
    protected MilitaryUnitType type;
    public Void call(){
        System.out.println(Player.getUnits().size());
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
                        //for(Node n : WarGameFXMLController.getController().getBattlefieldPane().getChildren()) System.out.print(((ImageView)n).getImage() + " ");
                        //System.out.println(isPlayerUnit + " " + img.getImage() + " " + neighbour + " " + life);
                        System.out.println(WarGameFXMLController.getController().getBattlefieldPane().getChildren().size());
                        if(isPlayerUnit && neighbour != null && !Player.hasImageView(neighbour)){
                            wait(200);
                            attack(neighbour);
                        }else{
                            if(arrived && neighbour == null){
                                wait(200);
                                attack(null);
                            }
                        }
                        System.out.println("ZAKLINOWALEM SIEEEE");
                    }while(neighbour != null && !manager.getGameover());
                }
            }catch(InterruptedException e){
                Logger.getLogger(MilitaryUnit.class.getName()).log(Level.SEVERE, null, e);
            }
        }while(life > 0 && !manager.getGameover());
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
        int row = findRow();
        TextArea battleTextArea = WarGameFXMLController.getController().getBattleTextArea();
        String[] t = splitBattleTextArea(battleTextArea, row);
        MilitaryUnit enemy;
        if(neighbour != null) enemy = Enemy.getUnit(neighbour);
        else{
            if(isPlayerUnit) enemy = Enemy.getBase();
            else enemy = Player.getBase();
        }
        updateBattleResult(battleTextArea, t[0], t[1], enemy);
        Random rand = new Random(); 
        if(isPlayerUnit)
            if(rand.nextInt(2) == 0){
                life -= enemy.attack; 
                battleTextArea.textProperty().set(t[0] + type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life + t[1]);
                if(life <= 0){
                    destroy(this);
                    Player.remove(this);
                    System.out.println("ZNISZCZONY");
                }
            }
        if(life > 0 && rand.nextInt(2) == 0){
            enemy.life -= attack;
            updateBattleResult(battleTextArea, t[0], t[1], enemy);
            if(enemy.life <= 0){
                if(!isPlayerUnit) manager.changePoints(enemy.initialLife);
                destroy(enemy);
                if(isPlayerUnit) Enemy.remove(enemy);
                else Player.remove(enemy);
                if(neighbour == null){
                    manager.setGameover(true);
                   // Enemy.destroyAll();
                    //Player.destroyAll();
                    //System.out.println("WSZEDLEM :)");
                    //WarGameFXMLController.getController().getMiddlePane().getChildren().remove(WarGameFXMLController.getController().getBattlefieldPane());
                   //WarGameFXMLController.getController().set();
                   //System.out.println(WarGameFXMLController.getController().getBattlefieldPane());
                }
                enemy = null;
                neighbour = null;
            }
        }
    }
    public static void destroy(MilitaryUnit unit){
        unit.img.setImage(null);
        unit.img = null;
        unit = null;
    }
    private void updateBattleResult(TextArea battleTextArea, String t1, String t2, MilitaryUnit enemy){
        if(isPlayerUnit) battleTextArea.textProperty().set(t1 + type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life + t2);
        else battleTextArea.textProperty().set(t1 + enemy.type.name() + "-" + type.name() + " " + enemy.life + "-" + life + t2);
    }
    private int findRow(){
        double y = img.getLayoutY();
        if(y == 0) return 1;
        if(y == 97) return 2;
        if(y == 194) return 3;
        return 4;
    }
    private String[] splitBattleTextArea(TextArea battleTextArea, int row){
        String text = battleTextArea.getText();
        int i = text.indexOf(row + "");
        String t1 = text.substring(0, i + 2); // +2 bo chcę objąć nr wiersza wraz z kropką za nim
        String t2 = text.substring(i);
        t2 = t2.substring(t2.indexOf("\n"));
        String[] splittedTextArea = {t1, t2};
        return splittedTextArea;
    }
}

