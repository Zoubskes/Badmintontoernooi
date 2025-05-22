module pack {
    requires javafx.controls;
    
    requires mysql.connector.j;
    requires org.apache.commons.dbcp2;
    
    requires java.management;
    requires java.sql;
    
    exports pack;
}
