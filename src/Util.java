import java.util.*;

public class Util {

    /*
     *
     */
    public static String get_lowest_key(HashMap<String, Integer> map){
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

    /*
     *
     */
    public static String get_highest_key(HashMap<String, Integer> map){
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

    /*
     *
     */
    public static boolean spy_in_team(ArrayList<String> team, ArrayList<String> spies){
        for (String player : spies){
            if (team.contains(player)) return true;
        }
        return false;
    }

    /*
     *
     */
    public static boolean is_subset_of_team(ArrayList<String> team, ArrayList<ArrayList<String>> team_list){
        for (ArrayList<String> t : team_list){
            ArrayList<String> match = t;
            match.retainAll(team);
            if (match.size() > 0)
                return true;
        }
        return false;
    }

    /*
     *
     */
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
