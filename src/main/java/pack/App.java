package pack;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private VBox rootLayout;  // Hoofd-layout voor de scene

    @Override
    public void start(Stage primaryStage) {
        rootLayout = new VBox();  // Gebruik een VBox voor de layout

        // Toon de HomePage bij het opstarten
        showHomePage();

        Scene scene = new Scene(rootLayout, 600, 400);
        primaryStage.setTitle("Deelproduct1 Zoubair Khan"); // Titel van het project
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Methode om de HomePage te tonen
    public void showHomePage() {
        HomePage homePage = new HomePage(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(homePage.getGrid());
    }
        public void showGeboortedatum() {
        Geboortedatum geboortedatum = new Geboortedatum(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(geboortedatum.getGrid());
    }
        public void showHuisnummer() {
        Huisnummer huisnummer = new Huisnummer(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(huisnummer.getGrid());
    }
        public void showPostcode() {
        Postcode postcode = new Postcode(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(postcode.getGrid());
    }
        public void showBadmintonklasse() {
        Badmintonklasse badmintonklasse = new Badmintonklasse(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(badmintonklasse.getGrid());
    }
        public void showOpentijd() {
        Opentijd opentijd  = new Opentijd(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(opentijd.getGrid());
    }
        public void showSluittijd() {
        Sluittijd sluittijd  = new Sluittijd(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(sluittijd.getGrid());
    }
        public void showPersoonsgegevens() {
        Persoonsgegevens persoonsgegevens  = new Persoonsgegevens(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(persoonsgegevens.getGrid());
    }
        public void showOpeningstijden() {
        Openingstijden openingstijden  = new Openingstijden(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(openingstijden.getGrid());
    }
        public void showAanwezigheid() {
        Aanwezigheid aanwezigheid  = new Aanwezigheid(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(aanwezigheid.getGrid());
    }
        public void showOverzicht() {
        Overzicht overzicht  = new Overzicht(this);
        rootLayout.getChildren().clear(); // Leeg de VBox voor nieuwe content
        rootLayout.getChildren().add(overzicht.getGrid());
    }
    public static void main(String[] args) {
        launch(args);
    }
}