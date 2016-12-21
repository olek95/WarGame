package wargame;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;

/**
 * Klasa <code>WarGameFXMLController</code> reprezentuje działanie gry bitewnej.
 * Posiada przyciski uruchamiające grę, wyświetlające statystyki zapisane w 
 * bazie danych, tworzące użytkownika, wyświetlające pomoc i wychodzące z gry. 
 * Jednostki należy najpierw kupić, następnie umieścić za pomocą myszy na planszy. 
 * Dane jednostek są w lewym górnym rogu, a aktualny stan bitwy w prawym górnym rogu. 
 * Gra kończy się w momencie, gdy ktoś zniszczy bazę przeciwnika. 
 * @author AleksanderSklorz
 */
public class WarGameFXMLController implements Initializable {
    private static double BATTLEFIELD_WIDTH, MARGIN;
    private static WarGameFXMLController controller;
    private MilitaryUnit unit;
    private Player p;
    private Enemy e;
    private GameManager manager;
    private DatabaseManager dbManager;
    private Dialog<String> textAreaDialog;
    Thread t;
    @FXML
    private Button buySoldierButton, buyTankButton, buyHelicopterButton, accountButton, statisticsButton, helpButton, exitButton;
    @FXML
    private Pane battlefieldPane;
    @FXML
    private TextArea battleTextArea;
    @FXML
    private TextField loginTextField, cashTextField, pointsTextField;
    @FXML
    private void play(ActionEvent event){
        if(!manager.getGameover()){
            Alert gameIsRunningAlert = new Alert(AlertType.WARNING, "Aktualnie grasz w grę. Czy chcesz rozpocząć nową grę?", ButtonType.OK, ButtonType.CANCEL);
            gameIsRunningAlert.setTitle("Gra działa");
            gameIsRunningAlert.showAndWait().ifPresent(type -> {
                if(type.equals(ButtonType.OK)){
                    manager.setGameover(true);
                    manager.setInterruptedGame(true);
                    setGameInterfaceState(false);
                    prepareNewGame();
                }
            });
        }else prepareNewGame();
    }
    @FXML
    private void signIn(ActionEvent event){
        TextInputDialog accountDialog = new TextInputDialog();
        accountDialog.setTitle("Konto");
        accountDialog.setContentText("Podaj login: ");
        boolean ok;
        do{
            Optional<String> result = accountDialog.showAndWait();
            ok = result.isPresent();
            if(ok){
                String login = result.get().trim();
                if(!login.equals("")){
                    if(login.length() <= 30){
                        if(!dbManager.existsPlayer(login)){
                            dbManager.addPlayer(login);
                        }
                        loginTextField.setText(login);
                        Player.setLogin(login);
                    }else{
                        showWarningAlert("Przekroczona długość", "Login nie może przekraczać 30 znaków. Podaj krótszy login.");
                    }
                }else{
                    showWarningAlert("Zły login", "Podałeś zły login. Login nie może być pusty.");
                }
            }
        }while(Player.getLogin() == null && ok);
    }
    @FXML
    private void help(ActionEvent event){
        ((TextArea)textAreaDialog.getDialogPane().getContent()).setText("ZASADY GRY:\n"
                + "Jeśli chcesz aby Twój wynik został zapisany, wybierz przycisk Konto i utwórz\n"
                + "użytkownika. Jeśli już posiadasz konto, wybierz ten sam przycisk i podaj swój\n"
                + "login, ale pamiętaj że dane zostaną nadpisane! Jeśli nie chcesz zapisać wyników,\n"
                + "wybierz od razu Graj i graj jako Anonim.\n"
                + "Gra polega na wysyłaniu jednostek w kierunku wrogiej bazy. Każda jednostka ma\n"
                + "swoje życie, koszt, prędkość i siłę ataku (podane w lewym górnym rogu).\n"
                + "Jedyny wyjątek to baza, która posiada tylko życie. Aby umieścić\n"
                + "jednostkę, kup dany typ jednostki, a następnie umieść kliknięciem myszy\n"
                + "na dowolnym miejscu planszy - jednostka automatycznie dołaczy do\n"
                + "odpowiedniego wiersza. Po każdym kupnie odejmowane są pieniądze, które\n"
                + "wzrastaja co 20 sekund po 5.\n"
                + "Podczas bitwy losowane jest czy jednostka trafiła w przeciwnika - jeśli tak,\n"
                + "odejmowana jest ilość życia równa wartości ataku. Aktualny stan bitwy\n"
                + "podany jest w prawym górnym rogu, podzielony na wiersze.\n"
                + "Wygrywa ten kto pierwszy zniszczy bazę wroga.");
        textAreaDialog.showAndWait();
    }
    @Override
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        controller = this;
        setGameInterfaceState(false);
        manager = GameManager.createGameManager();
        createTextAreaDialog();
        try{
            dbManager = DatabaseManager.createDatabaseManager("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/wargame?dontTrackOpenResources=true");
        }catch (ClassNotFoundException | SQLException e){
            Logger.getLogger(WarGameFXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
        manager.setGameover(true);
        BATTLEFIELD_WIDTH = battlefieldPane.getPrefWidth();
        MARGIN = 165;
        buySoldierButton.setOnAction(value -> {
            unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.SOLDIER);
            if(!manager.changeCash(unit.price)) unit = null;
            else blockPurchase(buySoldierButton);
        });
        buyTankButton.setOnAction(value -> {
            unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.TANK);
            if(!manager.changeCash(unit.price)) unit = null;
            else blockPurchase(buyTankButton);
        });
        buyHelicopterButton.setOnAction(value -> {
            unit = MilitaryUnitFactory.createMilitaryUnit(MilitaryUnitType.HELICOPTER);
            if(!manager.changeCash(unit.price)) unit = null;
            else blockPurchase(buyHelicopterButton);
        });
        statisticsButton.setOnAction(value -> {
            dbManager.showStatistics(textAreaDialog);
            textAreaDialog.showAndWait();
        });
        battlefieldPane.setOnMouseClicked(click -> {
            if(unit != null){
                battlefieldPane.getChildren().add(unit.getImg());
                p.addUnit(unit);
                unit.getImg().setLayoutY(getLocationY(click.getY()));
                new Thread(unit).start();
                unblockPurchase();
                unit = null;
            }
        });
        exitButton.setOnAction(value -> {
            System.exit(0);
        });
    }    
    /**
     * Pobiera szerokość pola bitwy.
     * @return szerokość pola bitwy. 
     */
    public static double getBattlefieldWidth(){
        return BATTLEFIELD_WIDTH;
    }
    /**
     * Zwraca odległość zatrzymania jednostki od bazy, aby jednostki wysłane 
     * przez bazę mogły się zmieścić. 
     * @return odległość zatrzymania jednostki od bazy
     */
    public static double getMargin(){
        return MARGIN;
    }
    /**
     * Zwraca obiekt kontrolera, aby umożliwić dostęp do jego pól w innych klasach.
     * @return obiekt kontrolera.
     */
    public static WarGameFXMLController getController(){
        return controller;
    }
    /**
     * Zwraca planszę z obrazkami jednostek na niej umieszczonymi. 
     * @return plansza z jednostkami. 
     */
    public Pane getBattlefieldPane(){
        return battlefieldPane;
    }
    /**
     * Zwraca obszar tekstowy służący do przedstawiania aktualnych wyników bitwy. 
     * @return obszar tekstowy z wynikami bitwy.
     */
    public TextArea getBattleTextArea(){
        return battleTextArea;
    }
    /**
     * Zwraca pole tekstowe z aktualnym stanem konta gracza. 
     * @return pole tekstowe ze stanem konta gracza. 
     */
    public TextField getCashTextField(){
        return cashTextField;
    }
    /**
     * Zwraca pole tekstowe z aktualnym stanem liczby punktów uzyskanych przez gracza. 
     * @return pole tekstowe z liczbami punktów uzyskanych przez gracza. 
     */
    public TextField getPointsTextField(){
        return pointsTextField;
    }
    /**
     * Ustawia stan przycisków interfejsu gry. Jeżeli true, to włącza interfejs 
     * w tryb gry, czyli blokuje przyciski służące do przygotowania gry, takie jak:
     * konto, statystyka, pomoc. Natomiast jeśli false, to włącza interfejs w
     * tryb przygotowania do gry, więc udostępnia przyciski konto, statystyka, 
     * pomoc, a blokuje np. możliwość kupienia jednostek. Jedynie przyciski
     * Graj oraz Wyjście są dostępne przez cały czas, aby gracz mógł w dowolnym 
     * momencie rozpocząć na nowo grę lub wyjść z niej. 
     * @param state tryb, jeśli true to włącz przyciski kupna jednostek, jeśli false włącz przyciski Konto, Statystyka, Pomoc
     */
    public static void setGameInterfaceState(boolean state){
        controller.accountButton.setDisable(state);
        controller.statisticsButton.setDisable(state);
        controller.helpButton.setDisable(state);
        controller.buyHelicopterButton.setDisable(!state);
        controller.buySoldierButton.setDisable(!state);
        controller.buyTankButton.setDisable(!state);
    }
    /**
     * Odblokowuje możliwość kupowania w momencie gdy umieszczono już jednostkę 
     * wcześniej zakupioną. Dodatkowo sprawdza, który przycisk jest oznaczony 
     * plusem, czyli znakiem sygnalizującym kupioną poprzednio jednostkę, i usuwa
     * ten plus z etykiety przycisku.
     */
    public static void unblockPurchase(){
        controller.buyHelicopterButton.setDisable(false);
        controller.buySoldierButton.setDisable(false);
        controller.buyTankButton.setDisable(false);
        if(controller.buySoldierButton.getText().endsWith("+")) controller.buySoldierButton.setText("Kup");
        else if(controller.buyTankButton.getText().endsWith("+")) controller.buyTankButton.setText("Kup");
        else controller.buyHelicopterButton.setText("Kup");
    }
    private double getLocationY(double click){
        if(click < 97) return 0; // 97 to wysokość najwyższego obrazka 
        else if(click > 97 && click < 194) return 97;
        else if(click > 194 && click < 291) return 194;
        return 291;
    }
    private void showWarningAlert(String title, String text){
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(text);
        a.showAndWait();
    }
    private void createTextAreaDialog(){
        textAreaDialog = new Dialog(); 
        textAreaDialog.setTitle("Statystyka");
        textAreaDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        textAreaDialog.setOnCloseRequest(value -> ((TextArea)textAreaDialog.getDialogPane().getContent()).setText(""));
        textAreaDialog.getDialogPane().setContent(new TextArea());
    }
    private void blockPurchase(Button b){
        buyHelicopterButton.setDisable(true);
        buySoldierButton.setDisable(true);
        buyTankButton.setDisable(true);
        b.setText("Kup+");
    }
    private void prepareNewGame(){
        boolean yes = false;
        if(p != null) battlefieldPane.getChildren().clear(); // jeśli gracz nie jest nullem to gra się 2 raz, więc czyszczę planszę
        p = new Player();
        if(Player.getLogin().equals("Anonim")){
            Alert anonymAlert = new Alert(AlertType.WARNING, "Czy chcesz grać jako anonim?", ButtonType.YES, ButtonType.NO);
            anonymAlert.setTitle("Anonim");
            Optional<ButtonType> result = anonymAlert.showAndWait();
            yes = result.get().equals(ButtonType.YES);
            if(yes){
                loginTextField.setText("Anonim");
            }
        }else yes = true;
        if(yes){
            e = new Enemy();
            setGameInterfaceState(true);
            manager.setInitialValues();
            manager.setGameover(false);
            manager.setInterruptedGame(false);
            manager.increaseCash();
            t = new Thread(e);
            t.start();
        }
    }
}

