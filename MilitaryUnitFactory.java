package wargame;

public class MilitaryUnitFactory {
    public static MilitaryUnit createMilitaryUnit(MilitaryUnitType type){
        if(MilitaryUnitType.SOLDIER.equals(type)) return new Soldier(); 
        if(MilitaryUnitType.TANK.equals(type)) return new Tank();
        if(MilitaryUnitType.HELICOPTER.equals(type)) return new Helicopter();
        if(MilitaryUnitType.BASE.equals(type)) return new Base();
        return null;
    }
}
