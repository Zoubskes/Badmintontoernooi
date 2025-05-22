/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pack;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 *
 * @author zouba
 */
public class HomePage {
    
        private GridPane grid;
        private App app;
    
        public HomePage(App app) {
        this.app = app;
        grid = new GridPane();
        createLayout();
        
    }
        // Layout van de homepage
    private void createLayout() {
        Label welkomLabel = new Label("Homepagina");
        Label identificatieLabel = new Label("Identificaties");
        Label kwalificatieLabel = new Label("Kwalificaties");
        Label samengesteldegegevensLabel = new Label("Samengestelde gegevens");
        welkomLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");  // Grotere tekst en vetgedrukt

        // Spaties tussen de objecten
        grid.setVgap(5);
        grid.setHgap(40);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setMinSize(100, 50);
        grid.setAlignment(Pos.CENTER);
        
        // Button objecten
        Button geboortedatumknop = new Button("geboortedatum");
        Button huisnummerknop = new Button("huisnummer");
        Button postcodeknop = new Button("postcode");
        Button badmintonklasseknop = new Button("badmintonklasse");
        Button opentijdknop = new Button("opentijd");
        Button sluittijdknop = new Button("sluittijd");
        Button persoonsgegevensknop = new Button("persoonsgegevens");
        Button openingstijdenknop = new Button("openingstijden");
        Button aanwezigheidknop = new Button("aanwezigheid");
        Button overzichtknop = new Button("overzicht aanwezigheid");
        
        // Events van de buttons
        geboortedatumknop.setOnAction(event -> app.showGeboortedatum());
        huisnummerknop.setOnAction(event -> app.showHuisnummer());
        postcodeknop.setOnAction(event -> app.showPostcode());
        badmintonklasseknop.setOnAction(event -> app.showBadmintonklasse());
        opentijdknop.setOnAction(event -> app.showOpentijd());
        sluittijdknop.setOnAction(event -> app.showSluittijd());
        persoonsgegevensknop.setOnAction(event -> app.showPersoonsgegevens());
        openingstijdenknop.setOnAction(event -> app.showOpeningstijden());
        aanwezigheidknop.setOnAction(event -> app.showAanwezigheid());
        overzichtknop.setOnAction(event -> app.showOverzicht());
        
        

        // Voeg het welkomslabel en knoppen toe aan het grid
        grid.add(welkomLabel, 0, 0, 2, 1);
        grid.add(identificatieLabel, 1,1);
        grid.add(geboortedatumknop, 1,2);
        grid.add(huisnummerknop, 1,3);
        grid.add(postcodeknop, 1,4);
        grid.add(badmintonklasseknop, 1,5);
        grid.add(opentijdknop, 1,6);
        grid.add(sluittijdknop, 1,7);
        grid.add(kwalificatieLabel, 2,1);
        grid.add(persoonsgegevensknop, 2,2);
        grid.add(openingstijdenknop, 2,3);
        grid.add(aanwezigheidknop, 2,4);
        grid.add(samengesteldegegevensLabel, 3,1);
        grid.add(overzichtknop, 3, 2);

    }
        
            public GridPane getGrid() {
        return grid;
    }
}
