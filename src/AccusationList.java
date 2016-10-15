import java.util.*;

public class AccusationList{

    private ArrayList<Accusation> accusation_list;
    private String last_accuser;
    private String[] last_accused_people;

    public AccusationList(){
        ArrayList accusation_list = new ArrayList<Accusation>();
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
        accusation_list.add(new Accusation(accuser, accused.split(string_delimenator)));
    }

}

class Accusation{

    public final String accuser;
    public final String[] accused;

    public Accusation(String accuser, String[] accused) {
        this.accuser = accuser;
        this.accused = accused;
    }

}
