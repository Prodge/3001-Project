import java.util.*;

public class Accusation{

    private ArrayList<SimpleAccusation> accusation_list;
    private String last_accuser;
    private String[] last_accused_people;

    public Accusation(){
        ArrayList accusation_list = new ArrayList<SimpleAccusation>();
    }

    public String get_recent_accuser(){
        return last_accuser;
    }

    public String[] get_recent_accused(){
        return last_accused_people;
    }

    public int get_total_accusations(){
        return accusation_list.size();
    }

    public void add_accusation(String accuser, String accused, String string_delimenator){
        accusation_list.add(new SimpleAccusation(accuser, accused.split(string_delimenator)));
    }

}

class SimpleAccusation{

    public final String x;
    public final String[] y;

    public SimpleAccusation(String x, String[] y) {
        this.x = x;
        this.y = y;
    }

}
