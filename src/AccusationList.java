import java.util.*;

public class AccusationList{

    private ArrayList<Accusation> accusation_list;
    private String last_accuser;
    private String[] last_accused_people;

    public AccusationList(){
        accusation_list = new ArrayList<Accusation>();
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

    public HashMap<String, Integer> get_accusation_map(){
        HashMap<String, Integer> accusation_map = new HashMap<String, Integer>();
        for(Accusation accusation : accusation_list){
            for(String accused : accusation.accused){
                accusation_map.put(accused, accusation_map.getOrDefault(accused, 0) + 1);
            }
        }
        return accusation_map;
    }

    public ArrayList<String> get_non_accused(String[] players){
        ArrayList<String> non_accused = new ArrayList<String>();
        HashMap<String, Integer> accusation_map = get_accusation_map();
        for(String player : players){
            if(!accusation_map.containsKey(player)){
                non_accused.add(player);
            }
        }
        return non_accused;
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
