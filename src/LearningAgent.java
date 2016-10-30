import java.util.*;

public class LearningAgent implements Agent{
    // Constants
    private static final String STRING_DELIMINATOR = "";

    // State variables
    private boolean spy;
    private int current_mission;
    private int total_failures;
    private int total_wins;
    private String name;
    private ArrayList<String> players;
    private ArrayList<String> spy_list;
    private HistoryList<ArrayList<String>> current_mission_propositions;
    private HashMap<String, Float> suspicious_list;

    // History
    private AccusationList accusations;
    private HistoryList<Integer> traitors_list;
    private HistoryList<String> leader_list;
    private HistoryList<ArrayList<String>> mission_vote_list;
    private HistoryList<ArrayList<String>> players_mission_list;
    private HistoryList<HistoryList<ArrayList<String>>> mission_propositions_list;

    private double betray_base_factor;
    private double accuse_as_spy_chance;
    private double nominate_spy_when_spy_chance;
    private Database db;

    public LearningAgent(){
        players = new ArrayList<String>();
        spy_list = new ArrayList<String>();
        suspicious_list = new HashMap<String, Float>();
        accusations = new AccusationList();
        mission_vote_list = new HistoryList<ArrayList<String>>();
        players_mission_list  = new HistoryList<ArrayList<String>>();
        traitors_list = new HistoryList<Integer>();
        leader_list = new HistoryList<String>();
        mission_propositions_list = new HistoryList<HistoryList<ArrayList<String>>>();
        db = new Database();
        updateVariables();
    }


    /**
     * Reports the current status, inlcuding players name, the name of all players,
     * the names of the spies (if known), the mission number and the number of failed missions
     * @param name a string consisting of a single letter, the agent's names.
     * @param players a string consisting of one letter for everyone in the game.
     * @param spies a String consisting of the latter name of each spy,
     * if the agent is a spy, or n questions marks where n is the number of spies allocated;
     * this should be sufficient for the agent to determine if they are a spy or not.
     * @param mission the next mission to be launched
     * @param failures the number of failed missions
     * */
    public void get_status(String name, String players, String spies, int mission, int failures){
        this.name = name;
        this.players = new ArrayList<String>(Arrays.asList(players.split(STRING_DELIMINATOR)));
        current_mission = mission;
        spy = spies.indexOf(name) != -1;
        spies = spy ? spies : "";
        spy_list = new ArrayList<String>(Arrays.asList(spies.split(STRING_DELIMINATOR)));
        total_failures = failures;
        total_wins = current_mission - total_failures - 1;

        if(current_mission == 1){
            suspicious_list = createSuspiciousList();
        }else{
            // If this isn't the start of the game, update the database with the results from the last round
            db.update_database((spy && traitors_list.get_latest_value() > 0) || (!spy && traitors_list.get_latest_value() == 0));
            updateVariables();
            mission_propositions_list.add(current_mission, current_mission_propositions);
        }
        current_mission_propositions = new HistoryList<ArrayList<String>>();
    }

    /**
     * Nominates a group of agents to go on a mission.
     * If the String does not correspond to a legitimate mission (<i>number</i> of distinct agents, in a String),
     * a default nomination of the first <i>number</i> agents (in alphabetical order) will be used,
     * as if this was what the agent nominated.
     * @param number the number of agents to be sent on the mission
     * @return a String containing the names of all the agents in a mission
     * */
    public String do_Nominate(int number){
        if (spy){
            ArrayList<String> nominations = new ArrayList<String>();
            // If we are a spy, nominate a random spy to go on the mission each time - a certain percentage of the time
            if(Math.random() > nominate_spy_when_spy_chance){
                nominations.add(spy_list.get((int) (Math.random() * spy_list.size())));
            }
            for (String player : suspicious_list.keySet()){
                if (suspicious_list.get(player) == 0.0f)
                    nominations.add(player);
                if (nominations.size() == number) break;
            }
            return String.join("", nominations);
        }
        return String.join("", getMostTrustableTeam(number));
    }

    /**
     * Provides information of a given mission.
     * @param leader the leader who proposed the mission
     * @param mission a String containing the names of all the agents in the mission
     **/
    public void get_ProposedMission(String leader, String mission){
        leader_list.add(current_mission, leader);
        current_mission_propositions.add(
            current_mission_propositions.get_next_key(),
            new ArrayList<String>(Arrays.asList(mission.split(STRING_DELIMINATOR)))
        );
    }

    /**
     * Gets an agents vote on the last reported mission
     * @return true, if the agent votes for the mission, false, if they vote against it.
     * */
    public boolean do_Vote(){
        // Always approve our own missions
        if (Objects.equals(leader_list.get_latest_value(), name))
            return true;

        // As a spy only approve if there is a spy in the misssion
        if (spy){
            if (Util.spyInTeam(current_mission_propositions.get_latest_value(), spy_list))
                return true;
            else
                return false;
        }

        // If I'm not on the team and its a team of 3 then it is likely there is a spy in the mission
        if (current_mission_propositions.get_latest_value().size() == 3 && !current_mission_propositions.get_latest_value().contains(name))
            return false;

        // If any players in the team have a suspicon level greater than the threshold then disapprove mission
        for (String player : current_mission_propositions.get_latest_value())
            if (suspicious_list.get(player) > 0.1f)
                return false;

        return true;
    }

    /**
     * Reports the votes for the previous mission
     * @param yays the names of the agents who voted for the mission
     **/
    public void get_Votes(String yays){
        mission_vote_list.add(current_mission, new ArrayList<String>(Arrays.asList(yays.split(STRING_DELIMINATOR))));
    }

    /**
     * Reports the agents being sent on a mission.
     * Should be able to be infered from tell_ProposedMission and tell_Votes, but incldued for completeness.
     * @param mission the Agents being sent on a mission
     **/
    public void get_Mission(String mission){
        players_mission_list.add(current_mission, new ArrayList<String>(Arrays.asList(mission.split(STRING_DELIMINATOR))));
    }

    /**
     * Agent chooses to betray or not.
     * @return true if agent betrays, false otherwise
     **/
    public boolean do_Betray(){
        // If we are not a spy then never betray
        if(!spy)
            return false;

        int mission_size = players_mission_list.get_latest_value().size();

        // Do not betray if we are the only player on the mission
        if(mission_size == 1)
            return false;

        ArrayList<String> spyteam = getSpiesInTeam(players_mission_list.get_latest_value());

        // When spy, if have already failed 2 missions then betray to win the game
        if (total_failures == 2)
            return true;

        // When spy, if have not won anything and there is a resistance memeber in the team then betray without getting caught
        if (total_wins == 2 && spyteam.size() < mission_size)
            return true;

        // Higher odds of betraying when the mission contains a larger number of players
        return ((((double) mission_size / players.size()) * (1 - betray_base_factor)) + betray_base_factor > Math.random());
    }

    /**
     * Reports the number of people who betrayed the mission
     * @param traitors the number of people on the mission who chose to betray (0 for success, greater than 0 for failure)
     **/
    public void get_Traitors(int traitors){
        traitors_list.add(current_mission, traitors);
        adjustSuspiciousList();
    }

    /**
     * Optional method to accuse other Agents of being spies.
     * Default action should return the empty String.
     * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
     * Of course convention can be ignored.
     * @return a string containing the name of each accused agent.
     * */
    public String do_Accuse(){
        // If I am a spy, accuse the most frequently previously accused non spy 50% of the time
        if(spy && Math.random() > accuse_as_spy_chance){
            HashMap<String, Integer> accusation_map = accusations.get_accusation_map();
            String most_accused = Util.getHighestKey(accusation_map);
            while(spy_list.contains(most_accused)){
                accusation_map.remove(most_accused);
                most_accused = Util.getHighestKey(accusation_map);
            }
            return most_accused;
        }

        // If the last mission had n players and n betrayals, accuse all of the players
        if(!spy && players_mission_list.get_latest_value().size() == traitors_list.get_latest_value()){
            return String.join("", players_mission_list.get_latest_value());
        }
        return "";
    }

    /**
     * Optional method to process an accusation.
     * @param accuser the name of the agent making the accusation.
     * @param accused the names of the Agents being Accused, concatenated in a String.
     * */
    public void get_Accusation(String accuser, String accused){
        accusations.add_accusation(accuser, accused, STRING_DELIMINATOR);
    }

    /////////////////////////////////////////
    // P R I V A T E    F U N C T I O N S //
    ////////////////////////////////////////

    /**
     * This method gets the most optimal value from the database for chance variables
     **/
    private void updateVariables(){
        accuse_as_spy_chance = db.get_new_value("accuse_as_spy_chance");
        betray_base_factor = db.get_new_value("betray_base_factor");
        nominate_spy_when_spy_chance = db.get_new_value("nominate_spy_when_spy_chance");
    }

    /**
     * Create and return initial list of suspicious players
     * @return hash map of suspicious players with player to level of suspiciousness
     **/
    private HashMap<String, Float> createSuspiciousList(){
        HashMap<String, Float> slist = new HashMap<String, Float>();
        if (spy) {
            // As a spy i know everything
            for (String player : players){
                if (spy_list.contains(player)){
                    slist.put(player, 1.0f);
                }else{
                    slist.put(player, 0.0f);
                }
            }
        } else {
            // As resistance
            for (String player : players){
                if (Objects.equals(name, player)){
                    slist.put(player, 0.0f);
                }else{
                    slist.put(player, 0.5f); // initial suspicison
                }
            }
        }
        return slist;
    }

    /**
     * Increase suspiciousness of a given player
     **/
    private void increaseSuspicious(String player){
        if(!Objects.equals(player, name)){
            suspicious_list.put(player, suspicious_list.get(player) + 0.35f);
        }
    }

    /**
     * Decrease suspiciousness of a given player
     **/
    private void decreaseSuspicious(String player){
        if(!Objects.equals(player, name)){
            suspicious_list.put(player, suspicious_list.get(player) - 0.25f);
            if (suspicious_list.get(player) < 0.0f){
                suspicious_list.put(player, 0.0f);
            }
        }
    }

    /**
     * This method is to be called at the end of round to adjust the suspicousness levels of each player according to mission results
     **/
    private void adjustSuspiciousList(){
        // If spy don't do anything as everything is known
        if (!spy){
            // If mission was not sabtoaged then increase trust
            if (traitors_list.get_latest_value() == 0){
                for (String player : players_mission_list.get_latest_value()){
                    decreaseSuspicious(player);
                }
            // If everyone in the team sabotaged the mission then everyone is a spy
            }else if (traitors_list.get_latest_value() == players_mission_list.get_latest_value().size()){
                for (String player : players_mission_list.get_latest_value()){
                    suspicious_list.put(player, 10.0f);
                }
            // the mission was sabotaged and there were 2 in the mission and i was one of them
            }else if (players_mission_list.get_latest_value().size() == 2 && players_mission_list.get_latest_value().contains(name)){
                ArrayList<String> other = new ArrayList<String>(players_mission_list.get_latest_value());
                other.remove(other.indexOf(name));
                suspicious_list.put(other.get(0), 10.0f);
            // the mission was sabotaged and we are part of a big team so dont trust the others
            }else if (players_mission_list.get_latest_value().size() > 2 && players_mission_list.get_latest_value().contains(name)){
                ArrayList<String> other = new ArrayList<String>(players_mission_list.get_latest_value());
                other.remove(other.indexOf(name));
                for (String player : other){
                    increaseSuspicious(player);
                }
            }
            // Misssion was sabotaged and we dont know who went so dont trust anyone
            for (String player : players_mission_list.get_latest_value()){
                increaseSuspicious(player);
            }
        }
    }

    /**
     * This method given the number of players wanted returns the players with the least suspicioiusness levels
     * @return list of players who are most trusted
     **/
    private ArrayList<String> getMostTrustableTeam(int count){
        ArrayList<String> trust_team = new ArrayList<String>();

        float[] sorted_suspicion_values = new float[suspicious_list.size()];
        int c = 0;
        for (Map.Entry<String, Float> entry : suspicious_list.entrySet()){
            sorted_suspicion_values[c] = (Float) entry.getValue();
            c++;
        }
        Arrays.sort(sorted_suspicion_values);

        for (int i=0; i<suspicious_list.size(); i++){
            ArrayList<String> matching_players = Util.getKeyFromValue(suspicious_list, sorted_suspicion_values[i]);
            for (String player : matching_players){
                if (!trust_team.contains(player)){
                    trust_team.add(player);
                    if (trust_team.size() == count) break;
                }
            }
            if (trust_team.size() == count) break;
        }
        return trust_team;
    }

    /**
     * This method given a team of players returns the spies in that team
     * @return list of spies in the team
     **/
    private ArrayList<String> getSpiesInTeam(ArrayList<String> team){
        ArrayList<String> spyteam = new ArrayList<String>();
        for (String player : team)
            if (spy_list.contains(player))
                spyteam.add(player);
        return spyteam;
    }

}
