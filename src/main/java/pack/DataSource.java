
package pack;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;


// Database connectie
public class DataSource {
    
        private static BasicDataSource ds = new BasicDataSource();
    
    static {
        ds.setUrl("jdbc:mysql://localhost:3306/deelproduct1");
        ds.setUsername("root");
        ds.setPassword("Poeppoep1!");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);        
    }
    
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
    
    private DataSource(){
        
    }
}
