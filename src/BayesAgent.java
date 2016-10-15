import java.util.*;

public class BayesAgent implements Agent{
    private static final String string_delimenator = ";";
    private String name;
    private String[] players;
    private boolean spy;
    private int next_mission;
    private int total_failures;
    private String current_leader;
    private String[] all_mission_players;
    private String[] current_mission_players;
    private String[] previous_mission_votes;
    private int mission_traitors;
    private AccusationList accusations;

    public BayesAgent(){
        AccusationList accusations = new AccusationList();
    }

    /**
     * Reports the current status, inlcuding players name, the name of all players, the names of the spies (if known), the mission number and the number of failed missions
     * @param name a string consisting of a single letter, the agent's names.
     * @param players a string consisting of one letter for everyone in the game.
     * @param spies a String consisting of the latter name of each spy, if the agent is a spy, or n questions marks where n is the number of spies allocated; this should be sufficient for the agent to determine if they are a spy or not.
     * @param mission the next mission to be launched
     * @param failures the number of failed missions
     * */
    public void get_status(String name, String players, String spies, int mission, int failures){
        this.name = name;
        this.players = players.split(string_delimenator);
        spy = spies.indexOf(name) != -1; // Checking if we are a spy
        next_mission = mission;
        total_failures = failures;
    }

    private String get_lowest_key(HashMap<String, Integer> map){
        String key = "";
        int lowest_value = -1;
        Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> pair = it.next();
            if(lowest_value == -1 || pair.getValue() < lowest_value){
                key = pair.getKey();
            }
        }
        return key;
    }

    /**
     * Nominates a group of agents to go on a mission.
     * If the String does not correspond to a legitimate mission (<i>number</i> of distinct agents, in a String),
     * a default nomination of the first <i>number</i> agents (in alphabetical order) will be used, as if this was what the agent nominated.
     * @param number the number of agents to be sent on the mission
     * @return a String containing the names of all the agents in a mission
     * */
    public String do_Nominate(int number){
        ArrayList<String> nominations = new ArrayList<String>();
        HashMap<String, Integer> accusation_map = accusations.get_accusation_map();
        ArrayList<String> non_accused = accusations.get_non_accused(players);

        // If we are a spy, we want to go on the mission.
        // We don't want to give away our position so we only nominate ourselves 50% of the time
        if(spy && Math.random() > 0.5){
            nominations.add(name);
        }

        while(nominations.size() != number){
            // If we have non accused players add them first
            if(non_accused.size() != 0){
                nominations.add(non_accused.get(0));
                non_accused.remove(0);

            // Otherwise add the players with the lowest number of accusations
            }else{
                String least_accused = get_lowest_key(accusation_map);
                nominations.add(least_accused);
                accusation_map.remove(least_accused);
            }
        }
        return nominations.toString();
    }

    /**
     * Provides information of a given mission.
     * @param leader the leader who proposed the mission
     * @param mission a String containing the names of all the agents in the mission
     **/
    public void get_ProposedMission(String leader, String mission){
        current_leader = leader;
        all_mission_players = mission.split(string_delimenator);
    }

    /**
     * Gets an agents vote on the last reported mission
     * @return true, if the agent votes for the mission, false, if they vote against it.
     * */
    public boolean do_Vote(){
        //TODO
        return true;
    }

    /**
     * Reports the votes for the previous mission
     * @param yays the names of the agents who voted for the mission
     **/
    public void get_Votes(String yays){
        previous_mission_votes = yays.split(string_delimenator);
    }

    /**
     * Reports the agents being sent on a mission.
     * Should be able to be infered from tell_ProposedMission and tell_Votes, but incldued for completeness.
     * @param mission the Agents being sent on a mission
     **/
    public void get_Mission(String mission){
        current_mission_players = mission.split(string_delimenator);
    }

    /**
     * Agent chooses to betray or not.
     * @return true if agent betrays, false otherwise
     **/
    public boolean do_Betray(){
        //TODO
        return true;
    }

    /**
     * Reports the number of people who betrayed the mission
     * @param traitors the number of people on the mission who chose to betray (0 for success, greater than 0 for failure)
     **/
    public void get_Traitors(int traitors){
        mission_traitors = traitors;
    }


    /**
     * Optional method to accuse other Agents of being spies.
     * Default action should return the empty String.
     * Convention suggests that this method only return a non-empty string when the accuser is sure that the accused is a spy.
     * Of course convention can be ignored.
     * @return a string containing the name of each accused agent.
     * */
    public String do_Accuse(){
        //TODO
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

}
