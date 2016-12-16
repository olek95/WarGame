package wargame;

import java.awt.event.MouseAdapter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;

public class WarGameFXMLController implements Initializable {
    private static double BATTLEFIELD_WIDTH, MARGIN;
    private static WarGameFXMLController controller;
    private MilitaryUnit unit;
    private Player p;
    @FXML
    private Button buySoldierButton, exitButton;
    @FXML
    private Pane battlefieldPane;
    @FXML
    private TextArea battleTextArea;
    @FXML
    private void buySoldier(ActionEvent event) throws InterruptedException {
        unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
    }
    
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        controller = this;
        p = new Player();
        BATTLEFIELD_WIDTH = battlefieldPane.getPrefWidth();
        MARGIN = 165;
       MilitaryUnit u = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
        u.getImg().setLayoutX(BATTLEFIELD_WIDTH - u.getImg().getImage().getWidth());
        u.getImg().setLayoutY(0);
        battlefieldPane.getChildren().add(u.getImg());
        new Thread(u).start();
        //try {
        //    Thread.sleep(1000);
        //} catch (InterruptedException ex) {
        //    Logger.getLogger(WarGameFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        //}
          MilitaryUnit u2 = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
        u2.getImg().setLayoutX(BATTLEFIELD_WIDTH - u2.getImg().getImage().getWidth());
        u2.getImg().setLayoutY(97);
         battlefieldPane.getChildren().add(u2.getImg());
         new Thread(u2).start();
        battlefieldPane.setOnMouseClicked(event -> {
            battlefieldPane.getChildren().add(unit.getImg());
            p.addUnit(unit);
            unit.getImg().setLayoutY(getLocationY(event.getY()));
            new Thread(unit).start();
        });
        exitButton.setOnAction(value -> {
            System.exit(0);
        });
    }    
    public double getLocationY(double click){
        if(click < 97) return 0; // 97 to wysokość najwyższego obrazka 
        else if(click > 97 && click < 194) return 97;
        else if(click > 194 && click < 291) return 194;
        return 291;
    }
    public static double getBattlefieldWidth(){
        return BATTLEFIELD_WIDTH;
    }
    public static double getMargin(){
        return MARGIN;
    }
    public static WarGameFXMLController getController(){
        return controller;
    }
    public Pane getBattlefieldPane(){
        return battlefieldPane;
    }
    public TextArea getBattleTextArea(){
        return battleTextArea;
    }
}
