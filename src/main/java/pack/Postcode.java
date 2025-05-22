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

public class Postcode {
    private GridPane grid;
    private Connection con;
    private App app;
    private Label postcodeLabel, feedbackLabel;
    private TextField postcode;
    private Button backButton, toevoegButton, updateButton, verwijderButton;
    private ListView<String> postcodeListView;
    
    public Postcode(App app) {
        this.app = app;
        postcodeLabel = new Label("Postcode");
        postcode = new TextField();
        toevoegButton = new Button("Toevoegen");
        updateButton = new Button("Update");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        postcodeListView = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setMinSize(100, 50);
        createLayout();
        vulPostcodeList();
    }
    
    private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        toevoegButton.setOnAction(e -> voegPostcode());
        updateButton.setOnAction(e -> updatePostcode());
        verwijderButton.setOnAction(e -> verwijderPostcode());
        
        grid.add(backButton, 0, 0);
        grid.add(postcodeLabel, 1, 1);
        grid.add(postcode, 1, 2);
        grid.add(toevoegButton, 1, 3);
        grid.add(updateButton, 2, 3);
        grid.add(verwijderButton, 3, 3);
        grid.add(feedbackLabel, 1, 4, 3, 1);
        grid.add(postcodeListView, 1, 5, 3, 1); // ListView over drie kolommen
    }

    private void voegPostcode() {
        String postcodeInvoer = postcode.getText();
        
        if (postcodeInvoer.isEmpty()) {
            feedbackLabel.setText("Postcode is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }
        
        if (!postcodeInvoer.matches("\\d{4}\\s?[A-Za-z]{2}")) {
            feedbackLabel.setText("Ongeldig postcode formaat. Gebruik '1234AB'.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "INSERT INTO postcode (pstcd) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, postcodeInvoer);
            pstmt.executeUpdate();
            
            postcode.clear();
            feedbackLabel.setText("Postcode toegevoegd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulPostcodeList();
            
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

    private void vulPostcodeList() {
        postcodeListView.getItems().clear();
        
        try {
            con = DataSource.getConnection();
            String sql = "SELECT pstcd FROM postcode";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                postcodeListView.getItems().add(rs.getString("pstcd"));
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

    private void updatePostcode() {
        String geselecteerdPostcode = postcodeListView.getSelectionModel().getSelectedItem();
        String nieuwePostcode = postcode.getText();
        
        if (geselecteerdPostcode == null) {
            feedbackLabel.setText("Selecteer een postcode om te updaten.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        if (nieuwePostcode.isEmpty()) {
            feedbackLabel.setText("Postcode veld is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        if (!nieuwePostcode.matches("\\d{4}\\s?[A-Za-z]{2}")) {
            feedbackLabel.setText("Ongeldig postcode formaat. Gebruik '1234AB'.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "UPDATE postcode SET pstcd = ? WHERE pstcd = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, nieuwePostcode);
            pstmt.setString(2, geselecteerdPostcode);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                feedbackLabel.setText("Postcode bijgewerkt.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulPostcodeList();
            } else {
                feedbackLabel.setText("Postcode niet gevonden.");
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

    private void verwijderPostcode() {
        String geselecteerdPostcode = postcodeListView.getSelectionModel().getSelectedItem();
        
        if (geselecteerdPostcode == null) {
            feedbackLabel.setText("Selecteer een postcode om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "DELETE FROM postcode WHERE pstcd = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, geselecteerdPostcode);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Postcode verwijderd.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulPostcodeList();
            } else {
                feedbackLabel.setText("Postcode niet gevonden.");
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
