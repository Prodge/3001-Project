import java.util.*;

public class StatAgent implements Agent{
    // Constants
    private static final String string_delimenator = "";

    // State variables
    private boolean spy;
    private int current_mission;
    private int total_failures;
    private int total_wins;
    private String name;
    private ArrayList<String> players;
    private ArrayList<String> spy_list;
    private HistoryList<ArrayList<String>> current_mission_propositions;

    // History
    private AccusationList accusations;
    private HistoryList<Integer> traitors_list;
    private HistoryList<String> leader_list;
    private HistoryList<ArrayList<String>> mission_vote_list;
    private HistoryList<ArrayList<String>> players_mission_list;
    private HistoryList<HistoryList<ArrayList<String>>> mission_propositions_list;

    public StatAgent(){
        players = new ArrayList<String>();
        spy_list = new ArrayList<String>();
        accusations = new AccusationList();
        mission_vote_list = new HistoryList<ArrayList<String>>();
        players_mission_list  = new HistoryList<ArrayList<String>>();
        traitors_list = new HistoryList<Integer>();
        leader_list = new HistoryList<String>();
        mission_propositions_list = new HistoryList<HistoryList<ArrayList<String>>>();
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
        this.players = new ArrayList<String>(Arrays.asList(players.split(string_delimenator)));
        current_mission = mission - 1;
        spy = spies.indexOf(name) != -1; // Checking if we are a spy
        spies = spy ? spies : "";
        spy_list = new ArrayList<String>(Arrays.asList(spies.split(string_delimenator)));
        total_failures = failures;
        total_wins = current_mission - total_failures;
        mission_propositions_list.add(current_mission-1, current_mission_propositions);
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
        return "";
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
            new ArrayList<String>(Arrays.asList(mission.split(string_delimenator)))
        );
    }

    /**
     * Gets an agents vote on the last reported mission
     * @return true, if the agent votes for the mission, false, if they vote against it.
     * */
    public boolean do_Vote(){
        return true;
    }

    /**
     * Reports the votes for the previous mission
     * @param yays the names of the agents who voted for the mission
     **/
    public void get_Votes(String yays){
        mission_vote_list.add(current_mission, new ArrayList<String>(Arrays.asList(yays.split(string_delimenator))));
    }

    /**
     * Reports the agents being sent on a mission.
     * Should be able to be infered from tell_ProposedMission and tell_Votes, but incldued for completeness.
     * @param mission the Agents being sent on a mission
     **/
    public void get_Mission(String mission){
        players_mission_list.add(current_mission, new ArrayList<String>(Arrays.asList(mission.split(string_delimenator))));
    }

    /**
     * Agent chooses to betray or not.
     * @return true if agent betrays, false otherwise
     **/
    public boolean do_Betray(){
        return spy;
    }

    /**
     * Reports the number of people who betrayed the mission
     * @param traitors the number of people on the mission who chose to betray (0 for success, greater than 0 for failure)
     **/
    public void get_Traitors(int traitors){
        traitors_list.add(current_mission, traitors);
    }

    /**
     * Optional method to accuse other Agents of being spies.
     * Default action should return the empty String.
     * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
     * Of course convention can be ignored.
     * @return a string containing the name of each accused agent.
     * */
    public String do_Accuse(){
        return "";
    }

    /**
     * Optional method to process an accusation.
     * @param accuser the name of the agent making the accusation.
     * @param accused the names of the Agents being Accused, concatenated in a String.
     * */
    public void get_Accusation(String accuser, String accused){
        accusations.add_accusation(accuser, accused, string_delimenator);
    }

    //////////////////////
    // Helper Functions //
    /////////////////////

    private String get_lowest_key(HashMap<String, Integer> map){
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

    private String get_highest_key(HashMap<String, Integer> map){
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

    private boolean spy_in_team(ArrayList<String> team, ArrayList<String> spies){
        for (String player : spies){
            if (team.contains(player)) return true;
        }
        return false;
    }

    private ArrayList<ArrayList<String>> get_failed_teams(){
        ArrayList<ArrayList<String>> failed_teams = new ArrayList<ArrayList<String>>();
        for (int i=1; i<current_mission; i++)
            if (traitors_list.get_value_for_key(i) > 0)
                failed_teams.add(players_mission_list.get_value_for_key(i));
        return failed_teams;
    }

    private boolean is_subset_of_team(ArrayList<String> team, ArrayList<ArrayList<String>> team_list){
        for (ArrayList<String> t : team_list){
            ArrayList<String> match = t;
            match.retainAll(team);
            if (match.size() > 0)
                return true;
        }
        return false;
    }
}
