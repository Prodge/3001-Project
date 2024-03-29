import java.util.*;

/**
 * This is an implementation of an Agent with expert rules
 *
 * @author Tim Metcalf (21515553) and Don Wimodya Randula Athukorala (21440859)
 */
public class ExpertAgent implements Agent{
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

    // History
    private AccusationList accusations;
    private HistoryList<Integer> traitors_list;
    private HistoryList<String> leader_list;
    private HistoryList<ArrayList<String>> mission_vote_list;
    private HistoryList<ArrayList<String>> players_mission_list;
    private HistoryList<HistoryList<ArrayList<String>>> mission_propositions_list;

    public ExpertAgent(){
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
        this.players = new ArrayList<String>(Arrays.asList(players.split(STRING_DELIMINATOR)));
        current_mission = mission;
        spy = spies.indexOf(name) != -1;
        spies = spy ? spies : "";
        spy_list = new ArrayList<String>(Arrays.asList(spies.split(STRING_DELIMINATOR)));
        total_failures = failures;
        total_wins = current_mission - total_failures - 1;
        if (current_mission != 1){
            mission_propositions_list.add(current_mission-1, current_mission_propositions);
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
        ArrayList<String> nominations = new ArrayList<String>();
        ArrayList<String> suspicious_players = get_suspicious_players();

        if(!spy){
            // if you are resistance, always send yourself
            nominations.add(name);

            // Now add any non suspicious players
            ArrayList<String> non_suspicious_players = players;
            non_suspicious_players.removeAll(suspicious_players);
            while(nominations.size() != number && non_suspicious_players.size() !=0){
                nominations.add(non_suspicious_players.get(0));
                non_suspicious_players.remove(0);
            }
        }else{
            // If we are a spy, nominate a random spy to go on the mission each time
            nominations.add(spy_list.get((int) (Math.random() * spy_list.size())));
        }

        // Fill the rest of our nominations with least accused players.
        // If we are a spy we have already nominated a spy to go on the mission,
        // we don't want more than one spy on each mission as it is easy to spot a pattern.
        // If we are not a spy, this is a relativley safe way to order players in suspiciousness.
        HashMap<String, Integer> accusation_map = accusations.get_accusation_map();
        ArrayList<String> non_accused = accusations.get_non_accused(players);
        while(nominations.size() != number){
            // If we have non accused players add them first
            if(non_accused.size() != 0){
                if(!nominations.contains(non_accused.get(0))){
                    nominations.add(non_accused.get(0));
                }
                non_accused.remove(0);

            // Otherwise add the players with the lowest number of accusations
            }else{
                String least_accused = Util.getLowestKey(accusation_map);
                if(!nominations.contains(least_accused)){
                    nominations.add(least_accused);
                }
                accusation_map.remove(least_accused);
            }
        }
        return String.join("", nominations);
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
        // As a spy, vote for all missions that include one spy
        if (spy)
            return Util.spyInTeam(current_mission_propositions.get_latest_value(), spy_list);
        // Always approve our own missions
        if (leader_list.get_latest_value() == name)
            return true;
        // As resistance, always pass the last round
        if (current_mission == 5)
            return true;
        // If there is a known spy on the team
        if (Util.spyInTeam(current_mission_propositions.get_latest_value(), get_suspicious_players()))
            return false;
        // If current team has a subset of past failed teams
        if (Util.isSubsetOfTeam(current_mission_propositions.get_latest_value(), get_failed_teams()))
            return false;
        // If I'm not on the team and its a team of 3
        if (current_mission_propositions.get_latest_value().size() == 3 && !current_mission_propositions.get_latest_value().contains(name))
            return false;
        // Otherwise just approve the team
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
        if(!spy){
            return false;
        }

        int mission_size = players_mission_list.get_latest_value().size();

        // Do not betray if we are the only player on the mission
        if(mission_size == 1){
            return false;
        }

        // Higher odds of betraying when the mission contains a larger number of players
        double base_factor = 0.25; // the minimum chance of betrayal
        return ((((double) mission_size / players.size()) * (1 - base_factor)) + base_factor > Math.random());
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
        // If I am a spy, accuse the most frequently previously accused non spy 50% of the time
        if(spy && Math.random() > 0.5){
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
     * Finds list of players who went on missions and failed
     * @return teams who failed missions
     * */
    private ArrayList<ArrayList<String>> get_failed_teams(){
        ArrayList<ArrayList<String>> failed_teams = new ArrayList<ArrayList<String>>();
        for (int i=1; i<current_mission; i++)
            if (traitors_list.get_value_for_key(i) > 0)
                failed_teams.add(players_mission_list.get_value_for_key(i));
        return failed_teams;
    }

    /**
     * Gets a list of suspicious players using accusations
     * @return list of players who are suspicous in order of suspicousness
     * */
    private ArrayList<String> get_suspicious_players(){
        int min_failed_missions = 2;
        ArrayList<ArrayList<String>> failed_teams = get_failed_teams();
        HashMap<String, Integer> player_fail_map = new HashMap<String, Integer>();

        // Generate a mapping of players to how many times they have been in a failed mission
        for(ArrayList<String> team : failed_teams){
            for(String player : team){
                player_fail_map.put(player, player_fail_map.getOrDefault(player, 0) + 1);
            }
        }

        // Build a list of the players ordered by most failed missions,
        // with at least 'min_failed_missions' failed missions
        ArrayList<String> suspicious_players = new ArrayList<String>();
        String suspicious_player = Util.getHighestKey(player_fail_map);
        while(suspicious_player != "" && player_fail_map.get(suspicious_player) >= min_failed_missions){
            suspicious_players.add(suspicious_player);
            player_fail_map.remove(suspicious_player);
            suspicious_player = Util.getHighestKey(player_fail_map);
        }
        return suspicious_players;
    }
}
