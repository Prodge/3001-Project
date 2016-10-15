import java.util.*;

public class AccusationList{

    private ArrayList<Accusation> accusation_list;

    public AccusationList(){
        accusation_list = new ArrayList<Accusation>();
    }

    public String get_recent_accuser(){
        return accusation_list.get(accusation_list.size() - 1).accuser;
    }

    public ArrayList<String> get_recent_accused(){
        return accusation_list.get(accusation_list.size() - 1).accused;
    }

    public int get_total_accusations(){
        return accusation_list.size();
    }

    public void add_accusation(String accuser, String accused, String string_delimenator){
        accusation_list.add(new Accusation(accuser, new ArrayList<String>(Arrays.asList(accused.split(string_delimenator)))));
    }

}

class Accusation{

    public final String accuser;
    public final ArrayList<String> accused;

    public Accusation(String accuser, ArrayList<String> accused) {
        this.accuser = accuser;
        this.accused = accused;
    }

}
