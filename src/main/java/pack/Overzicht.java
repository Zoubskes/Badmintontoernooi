/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Zou
 */
public class Overzicht {
        private GridPane grid;
        private Connection con;
        private App app;
        private Label straatgenootLabel, badmintondatumLabel, feedbackLabel;
        private ComboBox<String> straatgenoot;
        private ComboBox<LocalDate> badmintondatum;
        private Button backButton, bekijkButton, verwijderButton;
        private ListView<String> lvOverzicht;
    
        public Overzicht(App app) {
        this.app = app;
        straatgenootLabel = new Label("straatgenoot");
        straatgenoot = new ComboBox<>();
        vulStraatgenootBox();
        badmintondatumLabel = new Label("badmintondatum");
        badmintondatum = new ComboBox<>();
        bekijkButton = new Button("Bekijk");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        lvOverzicht = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setMinSize(100, 50);
        createLayout();
    }
        private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        straatgenoot.setOnAction(e -> vulBadmintondatumBox());
        bekijkButton.setOnAction(e -> vulListView());
        verwijderButton.setOnAction(e -> verwijderAanwezigheid());
        
        grid.add(backButton, 0, 0);
        grid.add(straatgenootLabel, 0,2);
        grid.add(straatgenoot, 1,2);
        grid.add(badmintondatumLabel, 0,3);
        grid.add(badmintondatum, 1,3);
        grid.add(bekijkButton, 1,4);
        grid.add(feedbackLabel, 1,5);
        grid.add(lvOverzicht, 1,6);
        grid.add(verwijderButton, 1,7);

    }

        private void vulStraatgenootBox(){
            try{
                con = DataSource.getConnection();
                String sql = "SELECT DISTINCT strtgnt FROM aanwezigheid";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String opentijdTijd = result.getString("strtgnt");
                    straatgenoot.getItems().add(opentijdTijd);
                }
            }catch (SQLException se){
                System.out.println(se.getMessage());
            }finally{
                try{
                    if(con != null) con.close();
                } catch (SQLException se){
                    System.out.println(se.getMessage());
                }
            }
        }
        private void vulBadmintondatumBox() {
    badmintondatum.getItems().clear(); // Leeg de ComboBox

    String straatgenootInvoer = straatgenoot.getValue();
    if (straatgenootInvoer == null) return; // Controleer of een straatgenoot is geselecteerd

    try {
        con = DataSource.getConnection();
        String sql = "SELECT bdmintndat FROM aanwezigheid WHERE strtgnt = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, straatgenootInvoer);
        ResultSet result = pstmt.executeQuery();

        while (result.next()) {
            LocalDate badmintondatumDatum = result.getDate("bdmintndat").toLocalDate();
            badmintondatum.getItems().add(badmintondatumDatum); // Voeg LocalDate toe aan ComboBox
        }
    } catch (SQLException se) {
        System.out.println(se.getMessage());
    } finally {
        try {
            if (con != null) con.close();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
}

private void vulListView() {
    // Leeg de ListView voordat nieuwe gegevens worden toegevoegd
    lvOverzicht.getItems().clear();
    
    // Verkrijg de geselecteerde straatgenoot uit de ComboBox
    String geselecteerdeStraatgenoot = straatgenoot.getValue();
    LocalDate geselecteerdeBadmintondatum = badmintondatum.getValue();
    
    // Controleer of er een selectie is gemaakt
    if (geselecteerdeStraatgenoot == null) {
        feedbackLabel.setText("Selecteer een straatgenoot.");
        feedbackLabel.setTextFill(Color.RED);
        return;
    }
    if (geselecteerdeBadmintondatum == null){
        feedbackLabel.setText("Selecteer een badmintondatum");
        feedbackLabel.setTextFill(Color.RED);
        return;
    }
    
    try {
        con = DataSource.getConnection();
        
        // Gebruik een PreparedStatement met de filterwaarde
        String sql = "SELECT p.gbrtdtm, p.pstcd, p.bdmintnkls, o.pntd, o.sltd " +
                     "FROM aanwezigheid a, persoonsgegevens p, openingstijden o " +
                     "WHERE a.strtgnt = p.strtgnt AND a.bdmintndat = o.bdmintndat " +
                     "AND a.strtgnt = ? AND a.bdmintndat = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, geselecteerdeStraatgenoot); // Filter op geselecteerde straatgenoot
        pstmt.setDate(2, java.sql.Date.valueOf(geselecteerdeBadmintondatum));
        
        ResultSet result = pstmt.executeQuery();
        
        // Voeg elke gevonden rij toe aan de ListView
        while (result.next()) {
            String geboortedatumStr = result.getString("gbrtdtm");
            String postcodeStr = result.getString("pstcd");
            String badmintonklasseStr = result.getString("bdmintnkls");
            String opentijdStr = result.getString("pntd");
            String sluittijdStr = result.getString("sltd");
            
            
            String strCombi = geboortedatumStr + " - " + postcodeStr + " - " + badmintonklasseStr + " - " + opentijdStr + " - " + sluittijdStr;

            lvOverzicht.getItems().add(strCombi);
        }
        
        // Controleer of er resultaten zijn gevonden
        if (lvOverzicht.getItems().isEmpty()) {
            feedbackLabel.setText("Geen gegevens gevonden voor de geselecteerde straatgenoot.");
            feedbackLabel.setTextFill(Color.RED);
        } else {
            feedbackLabel.setText("Gegevens succesvol geladen.");
            feedbackLabel.setTextFill(Color.GREEN);
        }
        
    } catch (SQLException se) {
        feedbackLabel.setText("Er is een fout opgetreden bij het laden van de gegevens.");
        feedbackLabel.setTextFill(Color.RED);
        System.out.println(se.getMessage());
    } finally {
        try {
            if (con != null) con.close();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
}    
private void verwijderAanwezigheid() {
    String geselecteerdeStraatgenoot = straatgenoot.getValue();
    LocalDate geselecteerdeBadmintondatum = badmintondatum.getValue();

    if (geselecteerdeStraatgenoot == null || geselecteerdeBadmintondatum == null) {
        feedbackLabel.setText("Selecteer zowel een straatgenoot als een badmintondatum.");
        feedbackLabel.setTextFill(Color.RED);
        return;
    }

    try {
        con = DataSource.getConnection();
        
        String sql = "DELETE FROM aanwezigheid WHERE strtgnt = ? AND bdmintndat = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        
        pstmt.setString(1, geselecteerdeStraatgenoot);
        pstmt.setDate(2, java.sql.Date.valueOf(geselecteerdeBadmintondatum));
        
        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected > 0) {
            feedbackLabel.setText("Record succesvol verwijderd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulBadmintondatumBox(); // Optioneel: vernieuw de badmintondata na het verwijderen
            straatgenoot.setValue(null);
            vulListView(); // Ververs de ListView
        } else {
            feedbackLabel.setText("Geen overeenkomend record gevonden om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
        }
        
    } catch (SQLException se) {
        feedbackLabel.setText("Er is een fout opgetreden bij het verwijderen van het record.");
        feedbackLabel.setTextFill(Color.RED);
        System.out.println(se.getMessage());
    } finally {
        try {
            if (con != null) con.close();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
}
        
        public GridPane getGrid() {
        return grid;
    }
}
