package pack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Zou
 */
public class Huisnummer {
    private GridPane grid;
    private Connection con;
    private App app;
    private Label huisLabel, feedbackLabel;
    private TextField huisnummer;
    private Button backButton, toevoegButton, updateButton, verwijderButton;
    private ListView<String> huisnummerListView;
    
    public Huisnummer(App app) {
        this.app = app;
        huisLabel = new Label("Huisnummer");
        huisnummer = new TextField();
        toevoegButton = new Button("Toevoegen");
        updateButton = new Button("Update");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        huisnummerListView = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setMinSize(100, 50);
        createLayout();
        vulHuisnummerList();
    }
    
    private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        toevoegButton.setOnAction(e -> voegHuisnummer());
        updateButton.setOnAction(e -> updateHuisnummer());
        verwijderButton.setOnAction(e -> verwijderHuisnummer());
        
        huisnummerListView.setOnMouseClicked(e -> vulTextFieldMetSelectie());
        
        grid.add(backButton, 0, 0);
        grid.add(huisLabel, 1, 1);
        grid.add(huisnummer, 1, 2);
        grid.add(toevoegButton, 1, 3);
        grid.add(updateButton, 2, 3);
        grid.add(verwijderButton, 3, 3);
        grid.add(feedbackLabel, 1, 4);
        grid.add(huisnummerListView, 1, 5, 3, 1); // Voegt de ListView toe over drie kolommen
    }

    private void voegHuisnummer(){
        String huisInvoer = huisnummer.getText();
        
        if(huisInvoer.isEmpty()){
            feedbackLabel.setText("Huisnummer is leeg.");
            feedbackLabel.setTextFill(Color.RED); // Rood voor foutmeldingen
            return; // Controleer of de velden zijn ingevuld
        }

        try{
            con = DataSource.getConnection();
            String sql = "INSERT INTO huisnummer (hsnmmr) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, huisInvoer);
            pstmt.executeUpdate();
            
            huisnummer.clear();
            feedbackLabel.setText("Huisnummer toegevoegd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulHuisnummerList();
            
        } catch (SQLException se){
            feedbackLabel.setText("Fout bij toevoegen.");
            feedbackLabel.setTextFill(Color.RED);
            System.out.println(se.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException se){
                System.out.println(se.getMessage());
            }
        }
    }
    
    private void vulHuisnummerList() {
        huisnummerListView.getItems().clear();
        
        try {
            con = DataSource.getConnection();
            String sql = "SELECT hsnmmr FROM huisnummer";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                huisnummerListView.getItems().add(rs.getString("hsnmmr"));
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

    private void vulTextFieldMetSelectie() {
        String geselecteerdHuisnummer = huisnummerListView.getSelectionModel().getSelectedItem();
        if (geselecteerdHuisnummer != null) {
            huisnummer.setText(geselecteerdHuisnummer);
        }
    }

    private void updateHuisnummer() {
        String nieuwHuisnummer = huisnummer.getText();
        String geselecteerdHuisnummer = huisnummerListView.getSelectionModel().getSelectedItem();

        if (geselecteerdHuisnummer == null) {
            feedbackLabel.setText("Selecteer een huisnummer om te updaten.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "UPDATE huisnummer SET hsnmmr = ? WHERE hsnmmr = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nieuwHuisnummer);
            pstmt.setString(2, geselecteerdHuisnummer);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Huisnummer bijgewerkt.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulHuisnummerList();
                huisnummer.clear();
            } else {
                feedbackLabel.setText("Huisnummer niet gevonden.");
                feedbackLabel.setTextFill(Color.RED);
            }
        } catch (SQLException se) {
            feedbackLabel.setText("Fout bij updaten.");
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

    private void verwijderHuisnummer() {
        String geselecteerdHuisnummer = huisnummerListView.getSelectionModel().getSelectedItem();

        if (geselecteerdHuisnummer == null) {
            feedbackLabel.setText("Selecteer een huisnummer om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "DELETE FROM huisnummer WHERE hsnmmr = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, geselecteerdHuisnummer);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Huisnummer verwijderd.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulHuisnummerList();
                huisnummer.clear();
            } else {
                feedbackLabel.setText("Huisnummer niet gevonden.");
                feedbackLabel.setTextFill(Color.RED);
            }
        } catch (SQLException se) {
            feedbackLabel.setText("Fout bij verwijderen.");
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
