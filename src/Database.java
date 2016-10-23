import java.util.*;
import java.sql.*;

public class Database{
    private String database_name = "history.db";
    private List<String> tables = Arrays.asList("accuse_as_spy_chance", "betray_base_factor", "nominate_spy_when_spy_chance");
    private HashMap<String, Double> last_values = new HashMap<String, Double>();

    public Database(){
        for(String variable : tables){
            last_values.put(variable, -1.0);
        }

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
                row_set.add(new DatabaseRecord(rs.getInt(1), ((double) rs.getInt(2) / 100), rs.getInt(3), rs.getInt(4)));
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

    /**
     * @return true if the database is empty
     * The database is considered empty when all values are 0 zeros
     */
    public boolean empty_database(){
        boolean is_empty = false;
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            Statement stmt = null;
            stmt = con.createStatement();
            // Just check the first table as they are all updated at once
            ResultSet rs = stmt.executeQuery( "SELECT count(*) as count FROM " + tables.get(0) + " WHERE success == 0 AND fail == 0;");
            while(rs.next()) {
                if(rs.getInt("count") == 101){
                    is_empty = true;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return is_empty;
    }

    private void create_database_tables(Connection con) throws SQLException {
        try{
            String query = "";
            for(String table_name : tables){
                // Create table
                query +=
                    "create table " + table_name +
                    "(ID        INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "value      INT NOT NULL," +
                    "success    INT NOT NULL," +
                    "fail       INT NOT NULL);\n"
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
                for(double i=0; i<=100; i+=1){
                    query += "insert into " + table_name + " (value, success, fail) VALUES (" + i + ", 0, 0);\n";
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

    private void set_default_last_value(String variable){
        switch (variable){
            // Default values when we have an empty database
            case "accuse_as_spy_chance": last_values.put(variable, 0.5);
                                         break;
            case "betray_base_factor": last_values.put(variable, 0.35);
                                         break;
            case "nominate_spy_when_spy_chance": last_values.put(variable, 0.8);
                                         break;
        }
    }

    /**
     * Searches the database for the optimal value for the given variable
     * @param variable maps to the table name in the database
     * @return the optimal value
     */
    public double get_new_value(String variable){
        if(empty_database()){
            set_default_last_value(variable);
        }else{
            ArrayList<DatabaseRecord> rows = get_table(variable);
            DatabaseRecord best_row = get_highest_success_ratio(rows);

            if(get_success_ratio(best_row) == 0.0){
                set_default_last_value(variable);
            }else{

                // 30% of the time, jump randomly left or right up to 5 indexes from the currently considered 'best' index
                if(Math.random() < 0.3){
                    //GUARD AGAINST INVALID INDEXES
                    if(best_row.id < 5 || best_row.id > 95){
                        if(best_row.id < 5){
                            last_values.put(variable, rows.get((int) Math.round(best_row.value*100) + 5).value);
                        }
                        if(best_row.id > 95){
                            last_values.put(variable, rows.get((int) Math.round(best_row.value*100) - 5).value);
                        }
                    }else{
                        if(Math.random() < 0.5){
                            last_values.put(variable, rows.get((int) Math.round(best_row.value*100) + ((int) (Math.random() * 5))).value);
                        }else{
                            last_values.put(variable, rows.get((int) Math.round(best_row.value*100) - ((int) (Math.random() * 5))).value);
                        }
                    }
                }else{

                    //GUARD AGAINST INVALID INDEXES
                    if(best_row.id == 1 || best_row.id == 101){
                        if(best_row.id==1){
                            last_values.put(variable, rows.get((int) Math.round(best_row.value*100) + 1).value);
                        }
                        if(best_row.id==101){
                            last_values.put(variable, rows.get((int) Math.round(best_row.value*100) - 1).value);
                        }
                    }else{

                        // Look at the rows on either side
                        DatabaseRecord left = rows.get((int) Math.round(best_row.value*100) - 1);
                        DatabaseRecord right = rows.get((int) Math.round(best_row.value*100) + 1);

                        double highest_value;
                        double lowest_value;
                        if(get_success_ratio(left) > get_success_ratio(right)){
                            highest_value = left.value;
                            lowest_value = right.value;
                        }else{
                            highest_value = right.value;
                            lowest_value = left.value;
                        }

                        //If one of the rows success value was 0, set this to the highest value as we havent explored yet
                        if(get_success_ratio(left) == 0.0){
                            highest_value = left.value;
                            lowest_value = right.value;
                        }

                        if(get_success_ratio(right) == 0.0){
                            highest_value = right.value;
                            lowest_value = left.value;
                        }

                        // Take the side with the higher success ratio 80% of the time
                        if(Math.random() < 0.8){
                            last_values.put(variable, highest_value);
                        }else{
                            last_values.put(variable, lowest_value);
                        }
                    }
                }
            }
        }
        System.out.println("returning value: " + last_values.get(variable));
        return last_values.get(variable);

        // This slight bit of randomness prevents the agent from getting stuck
        // anywhere in the search space and should give it a chance to traverse outwards
        // while also making sure it spends the majority of its time in a favorable position
    }

    private double get_success_ratio(DatabaseRecord row){
        return ((double) row.success / (row.fail +1));
    }

    /**
     *Returns the DatabaseRecord of the highest success ratio in the given table
     */
    private DatabaseRecord get_highest_success_ratio(ArrayList<DatabaseRecord> rows){
        double current_max = 0;
        DatabaseRecord db_rec = rows.get(0);
        for(DatabaseRecord row: rows){
            double success_ratio = get_success_ratio(row);
            if(success_ratio > current_max){
                current_max = success_ratio;
                db_rec = row;
            }
        }
        return db_rec;
    }

    private ArrayList<DatabaseRecord> get_table(String table){
        ArrayList<DatabaseRecord> row_set = new ArrayList<DatabaseRecord>();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            Statement stmt = null;
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM " + table + " ;" );
            while ( rs.next() ) {
                row_set.add(new DatabaseRecord(rs.getInt(1), ((double) rs.getInt(2) / 100), rs.getInt(3), rs.getInt(4)));
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return row_set;
    }

    /**
     * Looks up the given column of the given table and returns an INT column only that is specified when called
     */
    private int get_column_value_for_table_at_row(String table, double value, String column){
        int old_count = 0;
        try{
            int int_value = (int) Math.round(value*100);
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            Statement stmt = null;
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM " + table + " WHERE value == " + int_value + " ;" );
            while ( rs.next() ) {
                old_count = rs.getInt(column);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return old_count;
    }

    /**
     * Sets the given set_value for the given column at the given value in the given table
     * Used for updating the number of success / fails an agent has had
     */
    private void set_new_success_or_fail_value(String table, String column, double value, int set_value){
        try{
            int int_value = (int) Math.round(value*100);
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:" + database_name);
            Statement s = null;
            s = c.createStatement();
            s.executeUpdate("UPDATE " + table + " SET " + column + " = " + set_value + " WHERE value == " + int_value + ";");
            s.close();
            c.close();
        } catch (SQLException e) {
            printSQLException(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    /**
     * Adds the results from the last mission into the database
     * @param last_mission_success Whether or not the last mission was a success for the team this agent is on
     */
    public void update_database(boolean last_mission_success){
        // For each of the variables (tables)
        //   Update the row for the current value of the variable
        //     Increment either the success or fail column
        String column_to_update = last_mission_success ? "success" : "fail";
        for(String variable : tables){
            double value = last_values.get(variable);
            int old_count = get_column_value_for_table_at_row(variable, value, column_to_update);
            set_new_success_or_fail_value(variable, column_to_update, value, old_count + 1);
        }
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
