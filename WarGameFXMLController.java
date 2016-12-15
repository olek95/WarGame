package wargame;

import java.awt.event.MouseAdapter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;

public class WarGameFXMLController implements Initializable {
    private static double BATTLEFIELD_WIDTH, MARGIN;
    private double click;
    private MilitaryUnit unit;
    @FXML
    private Button buySoldierButton;
    @FXML
    private Pane battlefieldPane;
    @FXML
    private void buySoldier(ActionEvent event) throws InterruptedException {
        unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
    }
    
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        BATTLEFIELD_WIDTH = battlefieldPane.getPrefWidth();
        MARGIN = 165;
        battlefieldPane.setOnMouseClicked(event -> {
            click = event.getY();
            battlefieldPane.getChildren().add(unit.getImg());
            new Thread(unit).start();
        });
    }    
    public double getRow(){
        return click;
    }
    public static double getBattlefieldWidth(){
        return BATTLEFIELD_WIDTH;
    }
    public static double getMargin(){
        return MARGIN;
    }
}
