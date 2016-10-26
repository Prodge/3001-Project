import java.util.*;
import java.io.*;

public class Runner {

    private static final int[] SPY_NUM = {2,2,3,3,3,4}; //SPY_NUM[n-5] is the number of spies in an n player game
    private static final LinkedHashMap<String, String> PLAYER_TO_CLASS = new LinkedHashMap<String, String>(){{
        put("expert", "ExpertAgent");
        put("random", "RandomAgent");
        put("learning", "LearningAgent");
    }};

    private static int runGameConfig(ArrayList<Player> player_list, String who_are_spies){
        Set<Character> spies_list = new HashSet<Character>();
        HashMap<String, Integer> player_starting_char = new HashMap<String, Integer>();
        int total_players = 0;
        int spy_starting_char = 0;
        int spy_size = 0;
        int starting_char = 65;

        // Set player character names
        for (Player player : player_list){
            player_starting_char.put(player.name, starting_char);
            starting_char += player.size;
            total_players += player.size;
        }

        // Set values to create spies
        spy_size = SPY_NUM[(total_players) - 5];
        spy_starting_char = player_starting_char.get(who_are_spies);

        // Create spies
        for (int i=0; i<spy_size; i++)
            spies_list.add((char) (spy_starting_char + i));

        Game g = new Game();

        // Create agents
        try{
            for (Player player : player_list){
                for (int i=0; i<player.size; i++){
                    g.stopwatchOn();
                    g.addPlayer((Agent) Class.forName(PLAYER_TO_CLASS.get(player.name)).newInstance());
                    g.stopwatchOff(1000, (char) (player_starting_char.get(player.name) + i));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        // Run the game and return the number of fails
        g.setup(spies_list);
        return g.play();
    }

    private static int calculateWin(int round_win, String spy, String player){
        if (Objects.equals(spy, player))
            return round_win > 2 ? 1 : 0;
        return round_win > 2 ? 0 : 1;
    }

    public static void main(String[] args){
        // 5 arguments has to be passed in
        // [# of games, # of expert agents, # of random agents, # of learning agents, which type of agent the spies are]
        // Otherwise exit the program
        if (args.length != 5)
            System.exit(1);

        // Setup players
        ArrayList<Player> player_list = new ArrayList<Player>();
        int c = 1;
        for (String player : PLAYER_TO_CLASS.keySet()){
            if (!Objects.equals(args[c], "0"))
                player_list.add(new Player(player, Integer.valueOf(args[c])));
            c++;
        }

        // Run the set config for the number of games
        for (int i=0; i<Integer.valueOf(args[0]); i++){
            int round_win = runGameConfig(player_list, args[4]);
            // Save results for each player
            for (Player player : player_list)
                player.saveResults(calculateWin(round_win, args[4], player.name), round_win);
        }

        // Print the number of times we won
        System.out.println("\n\n-------------------------------------------\n\n");
        for (Player player : player_list){
            String player_type = Objects.equals(player.name, args[4]) ? "spy" : "resistance";
            System.out.println(player.name + " as " + player_type  + " won: " + player.current_wins);
            player.closeWriter();
        }
        System.out.println("\n\n-------------------------------------------\n\n");
    }

}

class Player{

    public String name;
    private File file;
    private FileWriter writer;
    private File failure_file;
    private FileWriter failure_writer;
    private int last_game;
    private int last_score;
    public int size;
    public int current_wins;

    public Player(String name, int size){
        try {
            file = new File(name + "learning_file.txt");
            file.createNewFile();
            failure_file = new File(name + "failure_file.txt");
            file.createNewFile();
            String[] last_line = getLastLineFromFile(name + "learning_file.txt").split(" +");
            last_game = Integer.valueOf(last_line[0]);
            last_score = Integer.valueOf(last_line[1]);
            writer = new FileWriter(file, true);
            failure_writer = new FileWriter(failure_file, true);
        } catch (IOException e){
            System.out.println(e);
        }
        this.name = name;
        this.file = file;
        this.size = size;
        current_wins = 0;
    }

    private String getLastLineFromFile(String file_name){
        String line_string = "-1    0";
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file_name));
            String sCurrentLine = null;
            while ((sCurrentLine = reader.readLine()) != null)
                line_string = sCurrentLine;
            reader.close();
        } catch (IOException e){
            System.out.println(e);
        }
        return line_string;
    }

    public void saveResults(int win, int fails){
        try{
            current_wins += win;
            last_game ++;
            last_score += win == 0 ? -1 : 1;
            writer.write("" + last_game + "    " + last_score  + "\n");
            writer.flush();
            failure_writer.write("" + last_game + "    " + fails  + "\n");
            failure_writer.flush();
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public void closeWriter(){
        try {
            writer.close();
            failure_writer.close();
        } catch (IOException e){
            System.out.println(e);
        }
    }

}
