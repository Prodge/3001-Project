MAIN_CLASS=Runner
RUNTME_FLAGS= -Djava.ext.dirs=../deps
OUTPUT_DIR=./class
COMPILE_FLAGS=-Werror
PROGRAM_ARGS=

compile: $(OUTPUT_DIR)/$(MAIN_CLASS).class

$(OUTPUT_DIR)/$(MAIN_CLASS).class: $(OUTPUT_DIR)
	javac $(COMPILE_FLAGS) -d $(OUTPUT_DIR) *.java

$(OUTPUT_DIR):
	mkdir $(OUTPUT_DIR)

run: $(OUTPUT_DIR)/$(MAIN_CLASS).class
	cd $(OUTPUT_DIR); java $(RUNTME_FLAGS) $(MAIN_CLASS) $(PROGRAM_ARGS)

clean:
	rm -rf $(OUTPUT_DIR)/*.class
