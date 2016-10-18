import java.util.*;

public class Runner {

    private static final int[] spyNum = {2,2,3,3,3,4}; //spyNum[n-5] is the number of spies in an n player game

    private static int run_game_config(int expert_agents, int random_agents, int learning_agents, String who_are_spies){
        Set<Character> spies_list = new HashSet<Character>();
        int spy_starting_char, spy_size = 0;
        int expert_starting_char = 65;
        int random_starting_char = expert_starting_char + expert_agents;
        int learning_starting_char = random_starting_char + random_agents;

        // Set values to create spies
        spy_size = spyNum[(expert_agents + random_agents + learning_agents) - 5];
        if (Objects.equals("expert", who_are_spies))
            spy_starting_char = expert_starting_char;
        else if (Objects.equals("random", who_are_spies))
            spy_starting_char = random_starting_char;
        else
            spy_starting_char = learning_starting_char;

        // Create spies
        for (int i=0; i<spy_size; i++)
            spies_list.add((char) (spy_starting_char + i));

        Game g = new Game();

        // Create expert agents
        for (int i=0; i<expert_agents; i++){
            g.stopwatchOn();g.addPlayer(new ExpertAgent());g.stopwatchOff(1000, (char) (expert_starting_char + i));
        }

        // Create random agents
        for (int i=0; i<random_agents; i++){
            g.stopwatchOn();g.addPlayer(new RandomAgent());g.stopwatchOff(1000, (char) (random_starting_char + i));
        }

        // Create learning agents
        for (int i=0; i<learning_agents; i++){
            g.stopwatchOn();g.addPlayer(new LearningAgent());g.stopwatchOff(1000, (char) (learning_starting_char + i));
        }

        // Run the game and return the number of fails
        g.setup(spies_list);
        return g.play();
    }

    public static void main(String[] args){
        // 5 arguments has to be passed in
        // [# of games, # of expert agents, # of random agents, # of learning agents, which type of agent the spies are]
        // Otherwise exit the program
        if (args.length != 5)
            System.exit(1);

        // Win counter
        int wins = 0;

        // Run the set config for the number of games
        for (int i=0; i<Integer.valueOf(args[0]); i++){
            if (Objects.equals(args[4], "expert") || Objects.equals(args[4], "learning"))
                wins += run_game_config(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]), args[4]) > 2 ? 1 : 0;
            else
                wins += run_game_config(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]), "random") > 2 ? 0 : 1;
        }

        // Print the number of times we won
        System.out.println("We won: " + Integer.toString(wins));
    }

}
