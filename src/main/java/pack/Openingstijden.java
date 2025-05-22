/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Zou
 */
public class Openingstijden {
        private GridPane grid;
        private Connection con;
        private App app;
        private Label badmintondatumLabel, opentijdLabel, sluittijdLabel, feedbackLabel;
        private DatePicker badmintondatum;
        private ComboBox<String> opentijd, sluittijd;
        private Button backButton, toevoegButton;
    
        public Openingstijden(App app) {
        this.app = app;
        badmintondatumLabel = new Label("badmintondatum");
        opentijdLabel = new Label("opentijd");
        sluittijdLabel = new Label("sluittijd");
        badmintondatum = new DatePicker();
        opentijd = new ComboBox<>();
        vulOpentijdBox();
        sluittijd = new ComboBox<>();
        vulSluittijdBox();
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
        grid.add(badmintondatumLabel, 0,2);
        grid.add(badmintondatum, 1,2);
        grid.add(opentijdLabel, 0,3);
        grid.add(opentijd, 1,3);
        grid.add(sluittijdLabel, 0,4);
        grid.add(sluittijd, 1,4);
        grid.add(toevoegButton, 1,7);
        grid.add(feedbackLabel, 1,8);

    }

        private void voegOpeningstijden(){
            LocalDate badmintondatumInvoer = badmintondatum.getValue();
            String opentijdInvoer = opentijd.getValue();
            String sluittijdInvoer = sluittijd.getValue();
            
            if(badmintondatumInvoer == null){
            feedbackLabel.setText("badmintondatum is leeg.");
            feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
            return; // Controleer of de velden zijn ingevuld
            }
            if(opentijdInvoer == null || sluittijdInvoer == null){
                feedbackLabel.setText("opentijd of sluittijd is niet geselecteerd.");
                feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
                return; // Controleer of alle velden zijn ingevuld
            }

            
            try{
                // Parse de tijdinvoer naar een LocalTime-object
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime localTime1 = LocalTime.parse(opentijdInvoer, formatter1);
                LocalTime localTime2 = LocalTime.parse(sluittijdInvoer, formatter2);
            
                // Converteer LocalTime naar java.sql.Time voor de database
                Time sqlTime1 = Time.valueOf(localTime1);
                Time sqlTime2 = Time.valueOf(localTime2);
                
                con = DataSource.getConnection();
                String sql = "INSERT INTO openingstijden (bdmintndat, pntd, sltd) VALUES (?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setDate(1, java.sql.Date.valueOf(badmintondatumInvoer));
                pstmt.setTime(2, sqlTime1);
                pstmt.setTime(3, sqlTime2);
                pstmt.executeUpdate();
                
                badmintondatum.setValue(null);
                opentijd.setValue(null);
                sluittijd.setValue(null);
                
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
        private void vulOpentijdBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select pntd FROM opentijd";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String opentijdTijd = result.getString("pntd");
                    opentijd.getItems().add(opentijdTijd);
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
        private void vulSluittijdBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select slttd FROM sluittijd";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String sluittijdTijd = result.getString("slttd");
                    sluittijd.getItems().add(sluittijdTijd);
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
