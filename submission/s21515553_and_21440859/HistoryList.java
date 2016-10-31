package s21515553_and_21440859;

import java.util.*;

/**
 * This data struture holds the history of a given type
 *
 * @author Tim Metcalf (21515553) and Don Wimodya Randula Athukorala (21440859)
 */
public class HistoryList<T> {

    private HashMap<Integer, T> main;
    private int last_key;

    public HistoryList(){
        main = new HashMap<Integer, T>();
        last_key = -1;
    }

    /**
     * Adds a pair to the map
     */
    public void add(int k, T s){
        main.put(k, s);
        last_key = k;
    }

    /**
     * Gets a value for a given keu
     * @return value for a given key
     */
    public T get_value_for_key(int k){
        return main.get(k);
    }

    /**
     * Gets the latest value in the map
     * @return the latest value in the map
     */
    public T get_latest_value(){
        return main.get(last_key);
    }

    /**
     * Get the next index in the map
     * @return the next index in the map
     */
    public int get_next_key(){
        return last_key + 1;
    }

    /**
     * Get whether the map is empty
     * @return true if the map is emtpy
     */
    public boolean is_empty(){
        return main.isEmpty();
    }

}
