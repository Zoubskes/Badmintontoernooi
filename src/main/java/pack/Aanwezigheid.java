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
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Zou
 */
public class Aanwezigheid {
        private GridPane grid;
        private Connection con;
        private App app;
        private Label straatgenootLabel, badmintondatumLabel, feedbackLabel;
        private ComboBox<String> straatgenoot, badmintondatum;
        private Button backButton, toevoegButton;
    
        public Aanwezigheid(App app) {
        this.app = app;
        badmintondatumLabel = new Label("badmintondatum");
        straatgenootLabel = new Label("straatgenoot");
        straatgenoot = new ComboBox<>();
        vulStraatgenootBox();
        badmintondatum = new ComboBox<>();
        vulBadmintondatumBox();
        toevoegButton = new Button("Toevoegen");
        feedbackLabel = new Label();
        
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
        
        toevoegButton.setOnAction(e -> voegOpeningstijden());
        
        grid.add(backButton, 0, 0);
        grid.add(straatgenootLabel, 0,2);
        grid.add(straatgenoot, 1,2);
        grid.add(badmintondatumLabel, 0,3);
        grid.add(badmintondatum, 1,3);
        grid.add(toevoegButton, 1,4);
        grid.add(feedbackLabel, 1,5);

    }
        private void voegOpeningstijden(){
            String straatgenootInvoer = straatgenoot.getValue();
            String badmintondatumInvoer = badmintondatum.getValue();
            
            if(badmintondatumInvoer == null){
            feedbackLabel.setText("badmintondatum is leeg.");
            feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
            return; // Controleer of de velden zijn ingevuld
            }
            if(straatgenootInvoer == null){
                feedbackLabel.setText("straatgenoot is leeg.");
                feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
                return; // Controleer of alle velden zijn ingevuld
            }

            
            try{
                
                con = DataSource.getConnection();
                String sql = "INSERT INTO aanwezigheid (strtgnt, bdmintndat) VALUES (?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, straatgenootInvoer);
                pstmt.setDate(2, java.sql.Date.valueOf(badmintondatumInvoer));
                pstmt.executeUpdate();
                
                straatgenoot.setValue(null);
                badmintondatum.setValue(null);
                
                feedbackLabel.setText("Je aanpassing is doorgevoerd.");
                feedbackLabel.setTextFill(Color.GREEN);
                
            } catch (SQLException se){
                feedbackLabel.setText("Je aanpassing is onjuist doorgevoerd.");
                feedbackLabel.setTextFill(Color.RED);
                System.out.println(se.getMessage());
            }finally{
                try{
                    if(con != null) con.close();
                }catch (SQLException se){
                    System.out.println(se.getMessage());
                }
            }
        }
        private void vulStraatgenootBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select strtgnt FROM persoonsgegevens";
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
        private void vulBadmintondatumBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select bdmintndat FROM openingstijden";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String sluittijdTijd = result.getString("bdmintndat");
                    badmintondatum.getItems().add(sluittijdTijd);
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
        
        public GridPane getGrid() {
        return grid;
    }
}
