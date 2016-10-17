MAIN_CLASS=Runner
RUNTME_FLAGS= -classpath ".:sqlite-jdbc-3.14.2.1.jar"
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

copy-deps: $(OUTPUT_DIR)
	cp deps/* $(OUTPUT_DIR)/

clean:
	rm -rf $(OUTPUT_DIR)
