import java.util.*;

public class Runner {

    private static final int[] spyNum = {2,2,3,3,3,4}; //spyNum[n-5] is the number of spies in an n player game

    private static int run_game_config(int expert_agents, int random_agents, String who_are_spies){
        Set<Character> spies_list = new HashSet<Character>();
        int spy_starting_char, spy_size = 0;
        int expert_starting_char = 65;
        int random_starting_char = expert_starting_char + expert_agents;

        // Set values to create spies
        spy_size = spyNum[(expert_agents + random_agents) - 5];
        spy_starting_char = Objects.equals("expert", who_are_spies) ? expert_starting_char : random_starting_char;

        // Create spies
        for (int i=0; i<spy_size; i++)
            spies_list.add((char) (spy_starting_char + i));

        Game g = new Game();

        // Create expert agents
        for (int i=0; i<expert_agents; i++){
            g.stopwatchOn();g.addPlayer(new LearningAgent());g.stopwatchOff(1000, (char) (expert_starting_char + i));
        }

        // Create random agents
        for (int i=0; i<random_agents; i++){
            g.stopwatchOn();g.addPlayer(new RandomAgent());g.stopwatchOff(1000, (char) (random_starting_char + i));
        }

        // Run the game and return the number of fails
        g.setup(spies_list);
        return g.play();
    }

    public static void main(String[] args){
        // 4 arguments has to be passed in
        // [# of games, # of expert agents, # of random agents, which type of agent the spies are]
        // Otherwise exit the program
        if (args.length != 4)
            System.exit(1);

        // Win counter
        int wins = 0;

        // Run the set config for the number of games
        for (int i=0; i<Integer.valueOf(args[0]); i++){
            if (Objects.equals(args[3], "expert"))
                wins += run_game_config(Integer.valueOf(args[1]), Integer.valueOf(args[2]), "expert") > 2 ? 1 : 0;
            else
                wins += run_game_config(Integer.valueOf(args[1]), Integer.valueOf(args[2]), "random") > 2 ? 0 : 1;
        }

        // Print the number of times we won
        System.out.println("We won: " + Integer.toString(wins));
    }

}
