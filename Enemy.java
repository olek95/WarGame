package wargame;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;

/**
 * Obiekt klasy <code>Enemy</code> reprezentuje przeciwnika. Dodatkowo 
 * przyjąłem że w momencie gry kończy się jego wątek - wyświetlane zostają
 * informacje np. o koncu gry, ponieważ obojętnie czy wygra czy przegra, 
 * jego wątek będzie zakończony. 
 * @author AleksanderSklorz
 */
public class Enemy extends Task{
    private static ArrayList<MilitaryUnit> units = new ArrayList();
    private static GameManager manager = GameManager.createGameManager();
    public Enemy(){
        units = new ArrayList();
        units.add(MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.BASE));
        setOnSucceeded(value -> {
            if(!manager.getInterruptedGame()) manager.showEndGameAlerts();
            WarGameFXMLController.unblockPurchase();
            WarGameFXMLController.setGameInterfaceState(false);
        });
    }
    /**
     * Wywołuje zadanie obsługujące decyzje podjęte przez przeciwnika. W zadaniu tym
     * przeciwnik losuje którą jednostkę chce umieścić i na jakiej pozycji. 
     * Decyzje przeciwnika są podejmowanie w losowym czasie z przedziału od 5 do
     * 20 sekund. Zadanie się kończy w momencie końca gry. 
     * @return null
     */
    protected Void call(){
        Random rand = new Random();
        MilitaryUnitType[] types = MilitaryUnitType.values();
        do{
            if(!manager.getGameover()){
                Platform.runLater(() -> {
                    MilitaryUnit u = MilitaryUnitFactory.createMilitaryUnit(types[rand.nextInt(3)]); // losuje rodzaj jednostki, bez możliwości wylosowania bazy
                    WarGameFXMLController.getController().getBattlefieldPane().getChildren().add(u.getImg());
                    units.add(u);
                    u.getImg().setLayoutX(WarGameFXMLController.getBattlefieldWidth()  - u.getImg().getImage().getWidth());
                    u.getImg().setLayoutY(randPosition());
                    new Thread(u).start();
                });
            }
            try{
                if(!manager.getGameover())
                    Thread.sleep(rand.nextInt(15001) + 5000); // zakładam że komputer będzie wysyłał jednostki z odstępami czasowymi od 5000 do 20000 milisekund
            }catch(InterruptedException e){
                    Logger.getLogger(WarGameFXMLController.class.getName()).log(Level.SEVERE, null, e);
            }
        }while(!manager.getGameover());
        return null;
    }
    /**
     * Zwraca wrogą jednostkę na podstawie komponentu przechowującego obraz. 
     * @param img komponent zawierający obraz przedstawiajacy jednostkę
     * @return wrogą jednostkę 
     */
    public static MilitaryUnit getUnit(ImageView img){
        for(MilitaryUnit unit : units)
            if(unit.getImg().equals(img)) return unit; 
        return null;
    }
    /**
     * Usuwa wrogą jednostkę z listy jednostek. 
     * @param unit jednostka do usunięcia
     */
    public static void remove(MilitaryUnit unit){
        units.remove(unit);
    }
    /**
     * Zwraca wrogą bazę. Jeśli baza została zniszczona, zwraca null. 
     * @return wrogą bazę lub jeśli zniszczona - null
     */
    public static MilitaryUnit getBase(){
        if(units.size() > 0 && units.get(0) instanceof Base) return units.get(0);
        return null;
    }
    private static int randPosition(){
        Random rand = new Random();
        int[] positions = {0, 97, 194, 291};
        return positions[rand.nextInt(4)];
    }
}
