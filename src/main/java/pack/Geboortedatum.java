package pack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Geboortedatum {
    private GridPane grid;
    private Connection con;
    private App app;
    private Label geboortelabel, feedbackLabel;
    private DatePicker geboortedatum;
    private Button backButton, toevoegButton, updateButton, verwijderButton;
    private ListView<String> geboortedatumListView;
    
    public Geboortedatum(App app) {
        this.app = app;
        geboortelabel = new Label("Geboortedatum");
        geboortedatum = new DatePicker();
        toevoegButton = new Button("Toevoegen");
        updateButton = new Button("Update");
        verwijderButton = new Button("Verwijder");
        feedbackLabel = new Label();
        geboortedatumListView = new ListView<>();
        
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10,10,10,10));
        grid.setMinSize(100, 50);
        createLayout();
        vulGeboortedatumList();
    }
    
    private void createLayout() {
        backButton = new Button("Terug naar Home");
        backButton.setOnAction(e -> app.showHomePage());
        
        toevoegButton.setOnAction(e -> voegGeboortedatum());
        updateButton.setOnAction(e -> updateGeboortedatum());
        verwijderButton.setOnAction(e -> verwijderGeboortedatum());
        
        geboortedatumListView.setOnMouseClicked(e -> vulDatePickerMetSelectie());
        
        grid.add(backButton, 0, 0);
        grid.add(geboortelabel, 1, 1);
        grid.add(geboortedatum, 1, 2);
        grid.add(toevoegButton, 1, 3);
        grid.add(updateButton, 2, 3);
        grid.add(verwijderButton, 3, 3);
        grid.add(feedbackLabel, 1, 4);
        grid.add(geboortedatumListView, 1, 5, 3, 1); // ListView over drie kolommen
    }

    private void voegGeboortedatum(){
        LocalDate geboortedatumInvoer = geboortedatum.getValue();
        
        if(geboortedatumInvoer == null){
            feedbackLabel.setText("Geboortedatum is leeg.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }
        
        try {
            con = DataSource.getConnection();
            String sql = "INSERT INTO geboortedatum (gbrtdtm) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(geboortedatumInvoer));
            pstmt.executeUpdate();
            
            geboortedatum.setValue(null);
            feedbackLabel.setText("Geboortedatum toegevoegd.");
            feedbackLabel.setTextFill(Color.GREEN);
            vulGeboortedatumList();
            
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
    
    private void vulGeboortedatumList() {
        geboortedatumListView.getItems().clear();
        
        try {
            con = DataSource.getConnection();
            String sql = "SELECT gbrtdtm FROM geboortedatum";
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                geboortedatumListView.getItems().add(rs.getDate("gbrtdtm").toString());
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

    private void vulDatePickerMetSelectie() {
        String geselecteerdeGeboortedatum = geboortedatumListView.getSelectionModel().getSelectedItem();
        if (geselecteerdeGeboortedatum != null) {
            geboortedatum.setValue(LocalDate.parse(geselecteerdeGeboortedatum));
        }
    }

    private void updateGeboortedatum() {
        LocalDate nieuweGeboortedatum = geboortedatum.getValue();
        String geselecteerdeGeboortedatum = geboortedatumListView.getSelectionModel().getSelectedItem();

        if (geselecteerdeGeboortedatum == null || nieuweGeboortedatum == null) {
            feedbackLabel.setText("Selecteer een geboortedatum om te updaten.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "UPDATE geboortedatum SET gbrtdtm = ? WHERE gbrtdtm = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(nieuweGeboortedatum));
            pstmt.setDate(2, java.sql.Date.valueOf(LocalDate.parse(geselecteerdeGeboortedatum)));
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Geboortedatum bijgewerkt.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulGeboortedatumList();
                geboortedatum.setValue(null);
            } else {
                feedbackLabel.setText("Geboortedatum niet gevonden.");
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

    private void verwijderGeboortedatum() {
        String geselecteerdeGeboortedatum = geboortedatumListView.getSelectionModel().getSelectedItem();

        if (geselecteerdeGeboortedatum == null) {
            feedbackLabel.setText("Selecteer een geboortedatum om te verwijderen.");
            feedbackLabel.setTextFill(Color.RED);
            return;
        }

        try {
            con = DataSource.getConnection();
            String sql = "DELETE FROM geboortedatum WHERE gbrtdtm = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.parse(geselecteerdeGeboortedatum)));
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                feedbackLabel.setText("Geboortedatum verwijderd.");
                feedbackLabel.setTextFill(Color.GREEN);
                vulGeboortedatumList();
                geboortedatum.setValue(null);
            } else {
                feedbackLabel.setText("Geboortedatum niet gevonden.");
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
