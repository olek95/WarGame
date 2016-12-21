package wargame;

/**
 * Klasa <code>MilitaryUnitFactory</code> reprezentuje fabrykę jednostek, która
 * tworzy jednostki danego typu. 
 * @author AleksanderSklorz
 */
public class MilitaryUnitFactory {
    /**
     * Tworzy jednostkę podanego typu. 
     * @param type typ jednostki
     * @return jednostka podanego typu
     */
    public static MilitaryUnit createMilitaryUnit(MilitaryUnitType type){
        if(MilitaryUnitType.SOLDIER.equals(type)) return new Soldier(); 
        if(MilitaryUnitType.TANK.equals(type)) return new Tank();
        if(MilitaryUnitType.HELICOPTER.equals(type)) return new Helicopter();
        if(MilitaryUnitType.BASE.equals(type)) return new Base();
        return null;
    }
}
