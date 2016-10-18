import java.util.*;
import java.sql.*;

public class Database{
    private String database_name = "history.db";
    private List<String> tables = Arrays.asList("accuse_as_spy_chance", "betray_base_factor");

    public Database(){
        initialise_database();
    }

    private void executeCUDQuery(Connection con, String query) throws SQLException {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e){
            printSQLException(e);
        } finally{
            if (stmt != null)
                stmt.close();
        }
    }

    private ArrayList<ArrayList<String>> executeSelectStandardQuery(Connection con, String query, int number_of_columns) throws SQLException {
        ArrayList<ArrayList<String>> row_set = new ArrayList<ArrayList<String>>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                ArrayList<String> row = new ArrayList<String>();
                for (int i=1; i<number_of_columns+1; i++)
                    row.add(rs.getString(i));
                row_set.add(row);
            }
        } catch (SQLException e){
            printSQLException(e);
        } finally{
            if (stmt != null)
                stmt.close();
        }
        return row_set;
    }

    private ArrayList<DatabaseRecord> executeSelectDatabaseRecordQuery(Connection con, String query) throws SQLException {
        ArrayList<DatabaseRecord> row_set = new ArrayList<DatabaseRecord>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next())
                row_set.add(new DatabaseRecord(rs.getInt(1), rs.getDouble(2), rs.getInt(3), rs.getInt(4)));
        } catch (SQLException e){
            printSQLException(e);
        } finally{
            if (stmt != null)
                stmt.close();
        }
        return row_set;
    }

    private boolean database_tables_exists(Connection con) throws SQLException {
        try {
            ResultSet rs = con.getMetaData().getTables(null, null, "%", null);
            int row_count = 0;
            while (rs.next()){
                if (tables.contains(rs.getString(3)))
                    row_count++;
            }
            if (row_count == tables.size())
                return true;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public boolean empty_database(){
        boolean empty_database = true;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            // Checking only one table
            ArrayList<ArrayList<String>> result = executeSelectStandardQuery(c, "select count(*) from " + tables.get(0) + ";", 1);
            if (Integer.valueOf(result.get(0).get(0)) != 0)
                empty_database = false;
            c.close();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return empty_database;
    }

    private void create_database_tables(Connection con) throws SQLException {
        try{
            String query = "";
            for(String table_name : tables){
                // Create table
                query +=
                    "create table " + table_name +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "value DOUBLE NOT NULL," +
                    "success INT NOT NULL," +
                    "fail INT NOT NULL);\n"
                ;
            }
            executeCUDQuery(con, query);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    private void populate_tables(Connection con) throws SQLException {
        try{
            String query = "";
            for(String table_name : tables){
                for(double i=0.0; i<=1; i+=0.01){
                    query += "insert into " + table_name + " (value, success, fail) VALUES (" + Double.toString(i) + ", 0, 0);\n";
                }
            }
            executeCUDQuery(con, query);
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    /**
     * Initialises empty tables in the database
     */
    private void initialise_database(){
        // Create 4 tables - one for each do_ method
        // Each table is prefilled with 100 rows from 0 to 1 of 0.01 step size
        // Columns are [value, success, fail] Prefill the success and fail numbers with 0
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            if (!database_tables_exists(c)){
                create_database_tables(c);
                populate_tables(c);
            }
            c.close();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
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

    /**
     * Prints helpful SQL error messages
     * Referenced from http://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
     */
    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (ignoreSQLException(((SQLException)e).getSQLState()) == false) {
                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " + ((SQLException)e).getSQLState());
                    System.err.println("Error Code: " + ((SQLException)e).getErrorCode());
                    System.err.println("Message: " + e.getMessage());
                    Throwable t = ex.getCause();
                    while(t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

    /**
     * Prints helpful SQL error messages
     * Referenced from http://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
     */
    public static boolean ignoreSQLException(String sqlState) {
        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }
        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32"))
            return true;
        // 42Y55: Table already exists in schema
        if (sqlState.equalsIgnoreCase("42Y55"))
            return true;
        return false;
    }

}
