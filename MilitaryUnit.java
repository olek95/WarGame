package wargame;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Obiekt klasy <code>MilitaryUnit</code> reprezentuje jednostkę wojskową. 
 * Każda jednostka składa się z obrazka ją reprezentującego oraz z wartości 
 * siły ataku, życia, prędkości oraz kosztu jednostki.
 * @author AleksanderSklorz
 */
public abstract class MilitaryUnit extends Task{
    protected ImageView img;
    protected int attack, life, initialLife, speed, price;
    private boolean isPlayerUnit, arrived;
    private GameManager manager = GameManager.createGameManager();
    protected MilitaryUnitType type;
    /**
     * Wywołuje zadanie obsługujące poruszanie się danej jednostki na polu bitwy. 
     * Porusza jednostkę od jednej strony na stronę przeciwną, aż do końca przeciwnej 
     * strony, jednakże bez marginesu (czyli miejsca przy bazie gdzie rodzą się
     * jednostki przeciwnika). Podejmuje też decyzję o zaatakowaniu przeciwnika. 
     * @return 
     */
    protected Void call(){
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
                //for(Node n : WarGameFXMLController.getController().getBattlefieldPane().getChildren())
                  //  System.out.print(((ImageView)n).getImage() + " ");
                //System.out.println();
                synchronized(this){
                    do{
                        wait(100);
                        neighbour = stop();
                        //for(Node n : WarGameFXMLController.getController().getBattlefieldPane().getChildren()) System.out.print(((ImageView)n).getImage() + " ");
                        //System.out.println(isPlayerUnit + " " + img.getImage() + " " + neighbour + " " + life);
                        if(isPlayerUnit && neighbour != null && !Player.hasImageView(neighbour)){
                            wait(200);
                            attack(neighbour);
                        }else{
                            if(arrived && neighbour == null){
                                wait(200);
                                attack(null);
                            }
                        }
                    }while(neighbour != null && !manager.getGameover());
                }
            }catch(InterruptedException e){
                Logger.getLogger(MilitaryUnit.class.getName()).log(Level.SEVERE, null, e);
            }
        }while(life > 0 && !manager.getGameover());
        return null;
    }
    /**
     * Zwraca komponent przechowujący obraz jednostki. 
     * @return komponent z obrazem jednostki
     */
    public ImageView getImg(){
        return img;
    }
    private ImageView stop(){
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
    private void attack(ImageView neighbour){
        int row = findRow();
        TextArea battleTextArea = WarGameFXMLController.getController().getBattleTextArea();
        MilitaryUnit enemy;
        if(neighbour != null) enemy = Enemy.getUnit(neighbour);
        else{
            if(isPlayerUnit) enemy = Enemy.getBase();
            else enemy = Player.getBase();
        }
        updateBattleResult(battleTextArea, enemy, row);
        Random rand = new Random(); 
        if(isPlayerUnit)
            if(rand.nextInt(2) == 0){
                life -= enemy.attack; 
                replace(battleTextArea, row, type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life);
                if(life <= 0){
                    destroy(this);
                    Player.remove(this);
                }
            }
        if(life > 0 && rand.nextInt(2) == 0){
            enemy.life -= attack;
            updateBattleResult(battleTextArea, enemy, row);
            if(enemy.life <= 0){
                if(isPlayerUnit) manager.changePoints(enemy.initialLife);
                destroy(enemy);
                if(isPlayerUnit) Enemy.remove(enemy);
                else Player.remove(enemy);
                if(neighbour == null) manager.setGameover(true);
                enemy = null;
                neighbour = null;
            }
        }
    }
    private static void destroy(MilitaryUnit unit){
        unit.img.setImage(null);
        unit.img = null;
        unit = null;
    }
    private void updateBattleResult(TextArea battleTextArea, MilitaryUnit enemy, int row){
        if(isPlayerUnit) replace(battleTextArea, row, type.name() + "-" + enemy.type.name() + " " + life + "-" + enemy.life);
        else replace(battleTextArea, row, enemy.type.name() + "-" + type.name() + " " + enemy.life + "-" + life);
    }
    private void replace(TextArea battleTextArea, int row, String result){
        battleTextArea.setText(battleTextArea.getText().replaceAll(row + "\\..*\\n", row + "." + result + "\n"));
        /* zamienia stringa zawierającego wyrażenie regularne składające się z: 
        nr wiersza, następnie kropki, potem dowolnego ciągu znaku aż do skoku do nowego wiersza*/
    }
    private int findRow(){
        double y = img.getLayoutY();
        if(y == 0) return 1;
        if(y == 97) return 2;
        if(y == 194) return 3;
        return 4;
    }
}

