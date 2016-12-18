package wargame;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;

public class Enemy extends Task{
    private static ArrayList<MilitaryUnit> units = new ArrayList();
    public Enemy(){
        units = new ArrayList();
        units.add(MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.BASE));
    }
    protected Void call(){
        Random rand = new Random();
        for(int i = 0; i < 10; i++){
            if(WarGameFXMLController.getController().getBattlefieldPane() == null) cancel();
            Platform.runLater(() -> {
                MilitaryUnit u = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
                WarGameFXMLController.getController().getBattlefieldPane().getChildren().add(u.getImg());
                units.add(u);
                u.getImg().setLayoutX(WarGameFXMLController.getBattlefieldWidth()  - u.getImg().getImage().getWidth());
                u.getImg().setLayoutY(randPosition());
                new Thread(u).start();
            });
        try{
            Thread.sleep(rand.nextInt(15001) + 5000); // zakładam że komputer będzie wysyłał jednostki z odstępami czasowymi od 5000 do 20000 milisekund
        }catch(InterruptedException e){
                Logger.getLogger(WarGameFXMLController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return null;
    }
    public static MilitaryUnit getUnit(ImageView img){
        for(MilitaryUnit unit : units)
            if(unit.getImg().equals(img)) return unit; 
        return null;
    }
    public static void remove(MilitaryUnit unit){
        units.remove(unit);
    }
    public static int size(){
        return units.size();
    }
    public static MilitaryUnit getBase(){
        return units.get(0);
    }
    public static ArrayList<MilitaryUnit> getUnits(){
        return units;
    }
    private static int randPosition(){
        Random rand = new Random();
        int[] positions = {0, 97, 194, 291};
        return positions[rand.nextInt(4)];
    }
    public static void destroyAll(){
        int i = 0;
        do{
            MilitaryUnit unit = units.get(i);
            unit.img.setImage(null);
        unit.img = null;
        unit = null;
        i++;
        }while(i < 0);
    }
}
