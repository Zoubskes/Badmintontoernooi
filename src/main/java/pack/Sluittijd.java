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

public class Sluittijd {
    private GridPane grid;
    private Connection con;
    private App app;
    private Label sluittijdLabel, feedbackLabel;
    private TextField sluittijd;
    private Button backButton, toevoegButton, updateButton, verwijderButton;
    private ListView<String> sluittijdListView;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    
    public Sluittijd(App app) {
        this.app = app;
        sluittijdLabel = new Label("Sluittijd (HH:mm)");
        sluittijd = new TextField();
        toevoegButton = new Button("Toevoegen");
        updateButton = new Button("Update");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        sluittijdListView = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setMinSize(100, 50);
        createLayout();
        vulSluittijdList();
    }
    
    private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        toevoegButton.setOnAction(e -> voegSluittijd());
        updateButton.setOnAction(e -> updateSluittijd());
        verwijderButton.setOnAction(e -> verwijderSluittijd());
        
        grid.add(backButton, 0, 0);
        grid.add(sluittijdLabel, 1, 1);
        grid.add(sluittijd, 1, 2);
        grid.add(toevoegButton, 1, 3);
        grid.add(updateButton, 2, 3);
        grid.add(verwijderButton, 3, 3);
        grid.add(feedbackLabel, 1, 4, 3, 1);
        grid.add(sluittijdListView, 1, 5, 3, 1); // ListView over drie kolommen
    }

    private void voegSluittijd() {
        String sluittijdInvoer = sluittijd.getText();
        
        if (sluittijdInvoer.isEmpty()) {
            feedbackLabel.setText("Sluittijd is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            LocalTime localTime = LocalTime.parse(sluittijdInvoer, formatter);
            Time sqlTime = Time.valueOf(localTime);

            con = DataSource.getConnection();
            String sql = "INSERT INTO sluittijd (slttd) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setTime(1, sqlTime);
            pstmt.executeUpdate();
            
            sluittijd.clear();
            feedbackLabel.setText("Sluittijd toegevoegd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulSluittijdList();
            
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

    private void vulSluittijdList() {
        sluittijdListView.getItems().clear();
        
        try {
            con = DataSource.getConnection();
            String sql = "SELECT slttd FROM sluittijd";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                LocalTime localTime = rs.getTime("slttd").toLocalTime();
                sluittijdListView.getItems().add(localTime.format(formatter));
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

    private void updateSluittijd() {
        String geselecteerdeSluittijd = sluittijdListView.getSelectionModel().getSelectedItem();
        String nieuweSluittijd = sluittijd.getText();
        
        if (geselecteerdeSluittijd == null) {
            feedbackLabel.setText("Selecteer een sluittijd om te updaten.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        if (nieuweSluittijd.isEmpty()) {
            feedbackLabel.setText("Sluittijd veld is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            LocalTime nieuweLocalTime = LocalTime.parse(nieuweSluittijd, formatter);
            Time nieuweSqlTime = Time.valueOf(nieuweLocalTime);
            LocalTime geselecteerdeLocalTime = LocalTime.parse(geselecteerdeSluittijd, formatter);
            Time geselecteerdeSqlTime = Time.valueOf(geselecteerdeLocalTime);

            con = DataSource.getConnection();
            String sql = "UPDATE sluittijd SET slttd = ? WHERE slttd = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setTime(1, nieuweSqlTime);
            pstmt.setTime(2, geselecteerdeSqlTime);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                feedbackLabel.setText("Sluittijd bijgewerkt.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulSluittijdList();
            } else {
                feedbackLabel.setText("Sluittijd niet gevonden.");
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

    private void verwijderSluittijd() {
        String geselecteerdeSluittijd = sluittijdListView.getSelectionModel().getSelectedItem();
        
        if (geselecteerdeSluittijd == null) {
            feedbackLabel.setText("Selecteer een sluittijd om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            LocalTime geselecteerdeLocalTime = LocalTime.parse(geselecteerdeSluittijd, formatter);
            Time geselecteerdeSqlTime = Time.valueOf(geselecteerdeLocalTime);

            con = DataSource.getConnection();
            String sql = "DELETE FROM sluittijd WHERE slttd = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setTime(1, geselecteerdeSqlTime);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Sluittijd verwijderd.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulSluittijdList();
            } else {
                feedbackLabel.setText("Sluittijd niet gevonden.");
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
