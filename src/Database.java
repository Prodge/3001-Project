import java.util.*;
import java.sql.*;

public class Database{
    private String database_name = "history.db";
    private List<String> tables = Arrays.asList("accuse_as_spy_chance", "betray_base_factor");

    public Database(){
        initialise_database();
    }

    /**
     * @return true if the database is empty
     */
    public boolean is_empty(){

        // Check a table in the database. if it contains all 0's for every row, the database is empty
        // This is ok because we always write to all tables at once
        return true;
    }

    /**
     * Initialises empty tables in the database
     * @return True if a database was initialised, false if it already existed
     */
    private void initialise_database(){

        // Create 4 tables - one for each do_ method
        // Each table is prefilled with 100 rows from 0 to 1 of 0.01 step size
        // Columns are | value | success | fail |
        // Prefill the success and fail numbers with 0

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            stmt = c.createStatement();

            for(String table_name : tables){
                // Create table
                stmt.executeUpdate("CREATE TABLE " + table_name +
                           "(id         INTEGER     PRIMARY KEY  AUTOINCREMENT," +
                           " value      REAL        NOT NULL, " +
                           " success    INTEGER     NOT NULL, " +
                           " fail       INTEGER     NOT NULL);");

                // Insert a row for every 0.01 step value from 0 to 1 inclusive
                for(double i = 0.0; i <= 1; i += 0.01){
                    stmt.executeUpdate(
                        "INSERT INTO " + table_name + "(value, success, fail)"+
                        "VALUES (" + i + ", 0, 0);");
                }
            }

            c.close();
            System.out.println("CREATED DATABASE");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println("FAILED TO CREATE DB");
        }
    }
    /**
     * Searches the database for the optimal value for the given variable
     * @param variable maps to the table name in the database
     * @return the optimal value
     */
    public double get_new_value(String variable){

        // Open the given database table
        //
        // Find the row with the highest success ratio (success / fail)
        //
        // Look at the rows on either side
        // Take the side with the higher success ratio 90% of the time
        //
        // This slight bit of randomness prevents the agent from getting stuck
        // anywhere in the search space and should give it a chance to traverse outwards
        // while also making sure it spends the majority of its time in a favorable position

        return 0;
    }

    /**
     * Adds the results from the last mission into the database
     * @param last_mission_success Whether or not the last mission was a success for the team this agent is on
     */
    public void update_database(boolean last_mission_success){
        // For each of the variables (tables)
        //  Update the row for the current value of the variable
        //      Increment either the success or fail column

    }
}
