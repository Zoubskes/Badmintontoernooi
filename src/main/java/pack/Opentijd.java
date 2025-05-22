package pack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Opentijd {
    private GridPane grid;
    private Connection con;
    private App app;
    private Label opentijdLabel, feedbackLabel;
    private TextField opentijd;
    private Button backButton, toevoegButton, updateButton, verwijderButton;
    private ListView<String> opentijdListView;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    
    public Opentijd(App app) {
        this.app = app;
        opentijdLabel = new Label("Opentijd (HH:mm)");
        opentijd = new TextField();
        toevoegButton = new Button("Toevoegen");
        updateButton = new Button("Update");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        opentijdListView = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setMinSize(100, 50);
        createLayout();
        vulOpentijdList();
    }
    
    private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        toevoegButton.setOnAction(e -> voegOpentijd());
        updateButton.setOnAction(e -> updateOpentijd());
        verwijderButton.setOnAction(e -> verwijderOpentijd());
        
        grid.add(backButton, 0, 0);
        grid.add(opentijdLabel, 1, 1);
        grid.add(opentijd, 1, 2);
        grid.add(toevoegButton, 1, 3);
        grid.add(updateButton, 2, 3);
        grid.add(verwijderButton, 3, 3);
        grid.add(feedbackLabel, 1, 4, 3, 1);
        grid.add(opentijdListView, 1, 5, 3, 1); // ListView over drie kolommen
    }

    private void voegOpentijd() {
        String opentijdInvoer = opentijd.getText();
        
        if (opentijdInvoer.isEmpty()) {
            feedbackLabel.setText("Opentijd is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            // Parse de tijdinvoer naar een LocalTime-object
            LocalTime localTime = LocalTime.parse(opentijdInvoer, formatter);
            Time sqlTime = Time.valueOf(localTime);

            con = DataSource.getConnection();
            String sql = "INSERT INTO opentijd (pntd) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setTime(1, sqlTime);
            pstmt.executeUpdate();
            
            opentijd.clear();
            feedbackLabel.setText("Opentijd toegevoegd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulOpentijdList();
            
        } catch (SQLException se) {
            feedbackLabel.setText("Fout bij toevoegen.");
            feedbackLabel.setTextFill(Color.RED);
            System.out.println(se.getMessage());
        } catch (Exception e) {
            feedbackLabel.setText("Ongeldige tijd. Gebruik formaat HH:mm.");
            feedbackLabel.setTextFill(Color.RED);
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException se) {
                System.out.println(se.getMessage());
            }
        }
    }

    private void vulOpentijdList() {
        opentijdListView.getItems().clear();
        
        try {
            con = DataSource.getConnection();
            String sql = "SELECT pntd FROM opentijd";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LocalTime localTime = rs.getTime("pntd").toLocalTime();
                opentijdListView.getItems().add(localTime.format(formatter));
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

    private void updateOpentijd() {
        String geselecteerdeOpentijd = opentijdListView.getSelectionModel().getSelectedItem();
        String nieuweOpentijd = opentijd.getText();
        
        if (geselecteerdeOpentijd == null) {
            feedbackLabel.setText("Selecteer een opentijd om te updaten.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        if (nieuweOpentijd.isEmpty()) {
            feedbackLabel.setText("Opentijd veld is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            LocalTime nieuweLocalTime = LocalTime.parse(nieuweOpentijd, formatter);
            Time nieuweSqlTime = Time.valueOf(nieuweLocalTime);
            LocalTime geselecteerdeLocalTime = LocalTime.parse(geselecteerdeOpentijd, formatter);
            Time geselecteerdeSqlTime = Time.valueOf(geselecteerdeLocalTime);

            con = DataSource.getConnection();
            String sql = "UPDATE opentijd SET pntd = ? WHERE pntd = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setTime(1, nieuweSqlTime);
            pstmt.setTime(2, geselecteerdeSqlTime);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                feedbackLabel.setText("Opentijd bijgewerkt.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulOpentijdList();
            } else {
                feedbackLabel.setText("Opentijd niet gevonden.");
                feedbackLabel.setTextFill(Color.RED);
            }
        } catch (SQLException se) {
            feedbackLabel.setText("Fout bij updaten.");
            feedbackLabel.setTextFill(Color.RED);
            System.out.println(se.getMessage());
        } catch (Exception e) {
            feedbackLabel.setText("Ongeldige tijd. Gebruik formaat HH:mm.");
            feedbackLabel.setTextFill(Color.RED);
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException se) {
                System.out.println(se.getMessage());
            }
        }
    }

    private void verwijderOpentijd() {
        String geselecteerdeOpentijd = opentijdListView.getSelectionModel().getSelectedItem();
        
        if (geselecteerdeOpentijd == null) {
            feedbackLabel.setText("Selecteer een opentijd om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            LocalTime geselecteerdeLocalTime = LocalTime.parse(geselecteerdeOpentijd, formatter);
            Time geselecteerdeSqlTime = Time.valueOf(geselecteerdeLocalTime);

            con = DataSource.getConnection();
            String sql = "DELETE FROM opentijd WHERE pntd = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setTime(1, geselecteerdeSqlTime);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Opentijd verwijderd.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulOpentijdList();
            } else {
                feedbackLabel.setText("Opentijd niet gevonden.");
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
