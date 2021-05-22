# crowdstreetexercise

This project contains the back end coding exercise for the folks at CrowdStreet.
All code was created by me, Cory Newey, though I did Google a couple of questions since
it's been quite awhile since I've created a Spring JPA app from scratch. I also had to
do some research on how to use the HSQLDB database.

This project requires a little setup unless you already have HSQLDB installed. If you
already have HSQLDB installed, you'll just need to specify the name of the database
in the application.properties file. The values in that file right now, assume there is
a database named "test".

To install and initialize HSQLDB, if needed, follow these instructions:

* Download the zip file from this location: https://sourceforge.net/projects/hsqldb/files/latest/download
* Unzip the file
* Create the database and run the db server
   + cd into the directory where you unzipped hsqldb
   + in a command shell execute: java -cp ./lib/hsqldb.jar org.hsqldb.server.Server
   + after running the above command look in the directory where you unzipped hsldb you should see the following files: test.lck, test.script, and others
   + if you see a different .lck and .script file name, your version must be different than mine and created a database with a different name than mine did -- put that name in the application.properties file instead of using test
   + kill the server with Ctrl-c on your command line
   + restart the server with this command line: java -cp ./lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:hsqldb/test --dbname.0 test
   
After you have the hsqldb server running, build and run this application. To build, run this command:
* mvn clean package

The build creates an executable jar so just start it up with this command line:
* java -jar target/crowdstreetexercise-0.0.1-SNAPSHOT.jar com.newey.crowdstreetexercise.CrowdstreetexerciseApplication


Enjoy...
