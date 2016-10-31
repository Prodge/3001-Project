MAIN_CLASS=automated_runner/Runner
RUNTME_FLAGS= -Djava.ext.dirs=deps
COMPILE_FLAGS=-Werror
PROGRAM_ARGS=

compile:
	javac $(COMPILE_FLAGS) $(MAIN_CLASS).java

run: compile
	java $(RUNTME_FLAGS) $(MAIN_CLASS) $(PROGRAM_ARGS)

clean:
	find . -type f -name '*.class' -delete

clean-result-files:
	rm -rf *.txt
	rm -rf *.db

graph-net-win:
	gnuplot -p graph_net_win.plg

graph-game-mission-failures:
	gnuplot -p graph_game_mission_failures.plg
