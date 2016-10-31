package s21515553_and_21440859;

import java.util.*;

public class HistoryList<T> {

    private HashMap<Integer, T> main;
    private int last_key;

    public HistoryList(){
        main = new HashMap<Integer, T>();
        last_key = -1;
    }

    public void add(int k, T s){
        main.put(k, s);
        last_key = k;
    }

    public T get_value_for_key(int k){
        return main.get(k);
    }

    public T get_latest_value(){
        return main.get(last_key);
    }

    public int get_next_key(){
        return last_key + 1;
    }

    public boolean is_empty(){
        return main.isEmpty();
    }

}
