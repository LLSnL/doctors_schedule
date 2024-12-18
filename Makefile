analysis:
	java -jar Code/schedule/target/schedule-app-1.0-SNAPSHOT.jar -a $(filter-out $@,$(MAKECMDGOALS))

doctors:
	java -jar Code/schedule/target/schedule-app-1.0-SNAPSHOT.jar -d

forecast:
	java -jar Code/schedule/target/schedule-app-1.0-SNAPSHOT.jar -f $(filter-out $@,$(MAKECMDGOALS))

schedule:
	java -jar Code/schedule/target/schedule-app-1.0-SNAPSHOT.jar -s $(filter-out $@,$(MAKECMDGOALS))

help:
	java -jar Code/schedule/target/schedule-app-1.0-SNAPSHOT.jar -h

%:
	@: