import java.util.*;

/**
 * This class holds a list of accusatations made against player for a game
 *
 * @author Tim Metcalf (21515553) and Don Wimodya Randula Athukorala (21440859)
 */
public class AccusationList{

    private ArrayList<Accusation> accusation_list;

    public AccusationList(){
        accusation_list = new ArrayList<Accusation>();
    }

    /**
     * Get the player who accussed last
     * @return name of player who accused last
     */
    public String get_recent_accuser(){
        return accusation_list.get(accusation_list.size() - 1).accuser;
    }

    /**
     * Get the list of people who were recently accussed
     * @return list of people who were accussed last
     */
    public ArrayList<String> get_recent_accused(){
        return accusation_list.get(accusation_list.size() - 1).accused;
    }

    /**
     * Get total number of accusations made
     * @return total number of accusations made
     */
    public int get_total_accusations(){
        return accusation_list.size();
    }

    /**
     * Get the number of people accussed by a player
     * @return hash map of people accused
     */
    public HashMap<String, Integer> get_accusation_map(){
        HashMap<String, Integer> accusation_map = new HashMap<String, Integer>();
        for(Accusation accusation : accusation_list){
            for(String accused : accusation.accused){
                accusation_map.put(accused, accusation_map.getOrDefault(accused, 0) + 1);
            }
        }
        return accusation_map;
    }

    /**
     * Get the list of players who were never accused
     * @return list of people who were never accussed
     */
    public ArrayList<String> get_non_accused(ArrayList<String> players){
        ArrayList<String> non_accused = new ArrayList<String>();
        HashMap<String, Integer> accusation_map = get_accusation_map();
        for(String player : players){
            if(!accusation_map.containsKey(player)){
                non_accused.add(player);
            }
        }
        return non_accused;
    }

    /**
     * Add an accusation
     */
    public void add_accusation(String accuser, String accused, String string_delimenator){
        accusation_list.add(new Accusation(accuser, new ArrayList<String>(Arrays.asList(accused.split(string_delimenator)))));
    }

}

/**
 * This is a class that holds an accusation
 */
class Accusation{

    public final String accuser;
    public final ArrayList<String> accused;

    public Accusation(String accuser, ArrayList<String> accused) {
        this.accuser = accuser;
        this.accused = accused;
    }

}
