package wargame;

import java.util.ArrayList;
import javafx.scene.image.ImageView;

public class Player {
    private static ArrayList<MilitaryUnit> units = new ArrayList();
    private static String login;
    public Player(){
        units = new ArrayList();
        units.add(MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.BASE));
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
    public static void remove(MilitaryUnit unit){
        units.remove(unit);
    }
    public static MilitaryUnit getBase(){
        if(units.size() > 0 && units.get(0) instanceof Base)  return units.get(0);
        return null;
    }
    public static void setLogin(String login){
        Player.login = login;
    }
    public static String getLogin(){
        return login;
    }
}
