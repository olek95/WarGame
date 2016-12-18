package wargame;

import java.util.ArrayList;
import javafx.scene.image.ImageView;

public class Player {
    private static ArrayList<MilitaryUnit> units = new ArrayList();
    public Player(){
        units = new ArrayList();
    }
    public void addUnit(MilitaryUnit unit){
        units.add(unit);
    }
    public static ArrayList<MilitaryUnit> getUnits(){
        return units;
    }
    public static boolean hasImageView(ImageView img){
        if(img == null) return false;
        for(MilitaryUnit unit : units)
            if(unit.getImg().equals(img)) return true;
        return false;
    }
}