# Project 2

## Accessing the database commands  
### Master account  
`psql -h csce-315-db.engr.tamu.edu -U csce315331_team_44_master -d csce315331_team_44`

### Personal account  
`psql -h csce-315-db.engr.tamu.edu -U csce315331_[user_name] -d csce315331_team_44`  
User name is your last name. 

### JavaDoc Command
`javadoc -d doc --source-path . *.java -subpackages Utils, Controller --module-path path/to/javaFX --add-modules javafx.controls,javafx.fxml`