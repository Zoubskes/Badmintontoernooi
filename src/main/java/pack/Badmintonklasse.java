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

public class Badmintonklasse {
    private GridPane grid;
    private Connection con;
    private App app;
    private Label klasseLabel, feedbackLabel;
    private TextField klasse;
    private Button backButton, toevoegButton, updateButton, verwijderButton;
    private ListView<String> klasseListView;
    
    public Badmintonklasse(App app) {
        this.app = app;
        klasseLabel = new Label("Badmintonklasse A, B, C, D, E of F");
        klasse = new TextField();
        toevoegButton = new Button("Toevoegen");
        updateButton = new Button("Update");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        klasseListView = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setMinSize(100, 50);
        createLayout();
        vulKlasseList();
    }
    
    private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        toevoegButton.setOnAction(e -> voegKlasse());
        updateButton.setOnAction(e -> updateKlasse());
        verwijderButton.setOnAction(e -> verwijderKlasse());
        
        grid.add(backButton, 0, 0);
        grid.add(klasseLabel, 1, 1);
        grid.add(klasse, 1, 2);
        grid.add(toevoegButton, 1, 3);
        grid.add(updateButton, 2, 3);
        grid.add(verwijderButton, 3, 3);
        grid.add(feedbackLabel, 1, 4, 3, 1);
        grid.add(klasseListView, 1, 5, 3, 1); // ListView over drie kolommen
    }

    private void voegKlasse() {
        String klasseInvoer = klasse.getText();
        
        if (klasseInvoer.isEmpty()) {
            feedbackLabel.setText("Badmintonklasse is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }
        
        if (!klasseInvoer.matches("[A-Fa-f]")) {
            feedbackLabel.setText("Alleen de letters A, B, C, D, E of F zijn toegestaan.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "INSERT INTO badmintonklasse (bdmintnkls) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, klasseInvoer.toUpperCase());
            pstmt.executeUpdate();
            
            klasse.clear();
            feedbackLabel.setText("Klasse toegevoegd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulKlasseList();
            
        } catch (SQLException se) {
            feedbackLabel.setText("Fout bij toevoegen.");
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

    private void vulKlasseList() {
        klasseListView.getItems().clear();
        
        try {
            con = DataSource.getConnection();
            String sql = "SELECT bdmintnkls FROM badmintonklasse";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                klasseListView.getItems().add(rs.getString("bdmintnkls"));
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

    private void updateKlasse() {
        String geselecteerdKlasse = klasseListView.getSelectionModel().getSelectedItem();
        String nieuweKlasse = klasse.getText();
        
        if (geselecteerdKlasse == null) {
            feedbackLabel.setText("Selecteer een klasse om te updaten.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        if (nieuweKlasse.isEmpty()) {
            feedbackLabel.setText("Klasse veld is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        if (!nieuweKlasse.matches("[A-Fa-f]")) {
            feedbackLabel.setText("Alleen de letters A, B, C, D, E of F zijn toegestaan.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "UPDATE badmintonklasse SET bdmintnkls = ? WHERE bdmintnkls = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nieuweKlasse.toUpperCase());
            pstmt.setString(2, geselecteerdKlasse);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                feedbackLabel.setText("Klasse bijgewerkt.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulKlasseList();
            } else {
                feedbackLabel.setText("Klasse niet gevonden.");
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

    private void verwijderKlasse() {
        String geselecteerdKlasse = klasseListView.getSelectionModel().getSelectedItem();
        
        if (geselecteerdKlasse == null) {
            feedbackLabel.setText("Selecteer een klasse om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "DELETE FROM badmintonklasse WHERE bdmintnkls = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, geselecteerdKlasse);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Klasse verwijderd.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulKlasseList();
            } else {
                feedbackLabel.setText("Klasse niet gevonden.");
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
