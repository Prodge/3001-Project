import java.util.*;
import java.sql.*;

public class Database{
    boolean is_empty;

    public Database(){
        is_empty = initialise_database();
    }

    /**
     * Initialises empty tables in the database
     * @return True if a database was initialised, false if it already existed
     */
    private static boolean initialise_database(){

        // Create 4 tables - one for each do_ method
        // Each table is prefilled with 100 rows from 0 to 1 of 0.01 step size
        // Columns are | value | success | fail |
        // Prefill the success and fail numbers with 0


        return true;
    }
    /**
     * Searches the database for the optimal value for the given variable
     * @param variable maps to the table name in the database
     * @return the optimal value
     */
    public static double get_new_value(String variable){

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
    public static void update_database(boolean last_mission_success){
        // For each of the variables (tables)
        //  Update the row for the current value of the variable
        //      Increment either the success or fail column

    }
}
