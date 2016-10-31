import java.util.*;

/**
 * This class contains helper methods which are used in other classes
 *
 * @author Tim Metcalf (21515553) and Don Wimodya Randula Athukorala (21440859)
 */
public class Util {

    /**
     * Get the key with the loweset value in a hashmap
     * @return key of the hashmap with the lowest value
     **/
    public static String getLowestKey(HashMap<String, Integer> map){
        String key = "";
        int lowest_value = -1;
        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = it.next();
            if(lowest_value == -1 || pair.getValue() < lowest_value){
                key = pair.getKey();
                lowest_value = pair.getValue();
            }
        }
        return key;
    }

    /**
     * Get the key with the highest value in a hashmap
     * @return key of the hashmap with the highest value
     **/
    public static String getHighestKey(HashMap<String, Integer> map){
        String key = "";
        int highest_value = -1;
        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = it.next();
            if(highest_value == -1 || pair.getValue() > highest_value){
                key = pair.getKey();
                highest_value = pair.getValue();
            }
        }
        return key;
    }

    /**
     * Given a team of players and team of spies, checks whether team containes spy
     * @return true if there is a spy otherwise returns false
     **/
    public static boolean spyInTeam(ArrayList<String> team, ArrayList<String> spies){
        for (String player : spies){
            if (team.contains(player)) return true;
        }
        return false;
    }

    /**
     * Given a team checks whether the team is part of a list of teams
     * @return true if the team is a part of the list of teams otherwise returns false
     **/
    public static boolean isSubsetOfTeam(ArrayList<String> team, ArrayList<ArrayList<String>> team_list){
        for (ArrayList<String> t : team_list){
            ArrayList<String> match = t;
            match.retainAll(team);
            if (match.size() > 0)
                return true;
        }
        return false;
    }

    /**
     * Given a hashmap and a value returns a list of matching keys
     * @return list of matching keys
     **/
    public static ArrayList<String> getKeyFromValue(HashMap<String, Float> hmsf, float value){
        ArrayList<String> matches = new ArrayList<String>();
        for (String key : hmsf.keySet()){
            if (hmsf.get(key).equals(value)){
                matches.add(key);
            }
        }
        return matches;
    }

}
