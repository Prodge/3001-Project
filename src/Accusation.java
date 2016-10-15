import java.util.*;

public class Accusation{

    private HashMap<String, new > accusation_map;
    private String last_accuser;
    private String[] last_accused_people;

    public Accusation(){
        HashMap accusation_map = new HashMap<String, String[]>();
    }

    public get_recent_accusation(){

    }

    public void add_accusation(String accuser, String accused){
        accusation_map.put(accuser, accused.split(","));
    }

}

class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
