*********************************************************
CITS3001 - Algorithms, Agents and Artifical Intelligence
                Resistance Project
**********************************************************

Project Authors:
    * Tim Metcalf (21515553)
    * Don Wimodya Randula Athukorala (21440859)

The 2 agents developed are in the s21515553_and_21440859 directory and are:
    * ExpertAgent
    * LearningAgent

An automated runner was also developed and is placed in the automated_runner directory.
To use the automated_runner use the following command:
    make run PROGRAM_ARGS="<# of games> <# of ExpertAgents> <# of RandomAgents> <# of LearningAgent> <Who the spies are>"

Ex - make run PROGRAM_ARGS="100 2 3 0 expert" <--- This runs 100 games wit 2 expert agents as the spies and 3 random agents as the resistance

The runner outputs the net win for each agent to a file as well as its performance in the game.
These can be plotted using the following commands:
    make graph-net-win
    make graph-game-mission-failures

However for these graphs to work, gnuplot must be installed.
