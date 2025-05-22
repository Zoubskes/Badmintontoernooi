/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Zou
 */
public class Persoonsgegevens {
        private GridPane grid;
        private Connection con;
        private App app;
        private Label straatgenootLabel, geboortedatumLabel, huisnummerLabel, postcodeLabel, badmintonklasseLabel, feedbackLabel;
        private TextField straatgenoot;
        private ComboBox<String> geboortedatum, huisnummer, postcode, badmintonklasse;
        private Button backButton, toevoegButton;
    
        public Persoonsgegevens(App app) {
        this.app = app;
        straatgenootLabel = new Label("straatgenoot");
        geboortedatumLabel = new Label("geboortedatum");
        huisnummerLabel = new Label("huisnummer");
        postcodeLabel = new Label("postcode");
        badmintonklasseLabel = new Label("badmintonklasse");
        straatgenoot = new TextField();
        geboortedatum = new ComboBox<>();
        vulGeboortedatumBox();
        huisnummer = new ComboBox<>();
        vulHuisnummerBox();
        postcode = new ComboBox<>();
        vulPostcodeBox();
        badmintonklasse = new ComboBox<>();
        vulBadmintonklasseBox();
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
        
        toevoegButton.setOnAction(e -> voegPersoonsgegevens());
        
        grid.add(backButton, 0, 0);
        grid.add(straatgenootLabel, 0,2);
        grid.add(straatgenoot, 1,2);
        grid.add(geboortedatumLabel, 0,3);
        grid.add(geboortedatum, 1,3);
        grid.add(huisnummerLabel, 0,4);
        grid.add(huisnummer, 1,4);
        grid.add(postcodeLabel, 0,5);
        grid.add(postcode, 1,5);
        grid.add(badmintonklasseLabel, 0,6);
        grid.add(badmintonklasse, 1,6);
        grid.add(toevoegButton, 1,7);
        grid.add(feedbackLabel, 1,8);

    }

        private void voegPersoonsgegevens(){
            String straatgenootInvoer = straatgenoot.getText();
            String geboortedatumInvoer = geboortedatum.getValue();
            String huisnummerInvoer = huisnummer.getValue();
            String postcodeInvoer = postcode.getValue();
            String badmintonklasseInvoer = badmintonklasse.getValue();
            
            if(straatgenootInvoer.isEmpty()){
            feedbackLabel.setText("straatgenoot is leeg.");
            feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
            return; // Controleer of de velden zijn ingevuld
            }
            if(geboortedatumInvoer == null || huisnummerInvoer == null || postcodeInvoer == null || badmintonklasseInvoer == null){
                feedbackLabel.setText("Geboortedatum, huisnummer, postcode of badmintonklasse is niet geselecteerd.");
                feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
                return; // Controleer of alle velden zijn ingevuld
            }

            
            try{
                con = DataSource.getConnection();
                String sql = "INSERT INTO persoonsgegevens (strtgnt, gbrtdtm, hsnmmr, pstcd, bdmintnkls) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, straatgenootInvoer);
                pstmt.setDate(2, java.sql.Date.valueOf(geboortedatumInvoer));
                pstmt.setString(3, huisnummerInvoer);
                pstmt.setString(4, postcodeInvoer);
                pstmt.setString(5, badmintonklasseInvoer);
                pstmt.executeUpdate();
                
                straatgenoot.clear();
                geboortedatum.setValue(null);
                huisnummer.setValue(null);
                postcode.setValue(null);
                badmintonklasse.setValue(null);
                
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
        private void vulGeboortedatumBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select gbrtdtm FROM geboortedatum";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String geboortedatumDatum = result.getString("gbrtdtm");
                    geboortedatum.getItems().add(geboortedatumDatum);
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
        private void vulHuisnummerBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select hsnmmr FROM huisnummer";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String geboortedatumDatum = result.getString("hsnmmr");
                    huisnummer.getItems().add(geboortedatumDatum);
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
        private void vulPostcodeBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select pstcd FROM postcode";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String geboortedatumDatum = result.getString("pstcd");
                    postcode.getItems().add(geboortedatumDatum);
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
        private void vulBadmintonklasseBox(){
            try{
                con = DataSource.getConnection();
                String sql = "Select bdmintnkls FROM badmintonklasse";
                PreparedStatement pstmt = con.prepareStatement(sql);
                ResultSet result = pstmt.executeQuery();
                while(result.next()){
                    String geboortedatumDatum = result.getString("bdmintnkls");
                    badmintonklasse.getItems().add(geboortedatumDatum);
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
