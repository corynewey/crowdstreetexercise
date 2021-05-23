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


#Architecture notes

I chose to do this assignment in Spring Boot partly because Spring handles much of the load
with respect to error handling. The classes of errors that can occur are mainly these:
* Validation
* Network
* Bugs in the code

*__Validation__* While Spring can validate all types of incoming parameters, I chose to only have it validate simple
types like Integer. I chose to do it that way because I didn't want to take the time to set up Jackson
to marshall all incoming parameters. So for the JSON objects, I'm doing the validation by parsing the
incoming String myself into a Java object. If the String doesn't parse, it is invalid JSON.

*__Network__* I would check the HTTP status of the third-party call if the code were actually making
the call (the directions said to not make the call, just stub it out). However, if some network
problem occurred that I wasn't handling, an exception would be thrown and Spring would return a 400
HTTP error code. So callers will always know if an error occurred.

*__Bugs__* Once again, if a bug in the code causes an error, it will usually manifest in an exception
which, as stated above, will cause Spring to return an HTTP error code.


In the interest of time, I have simplified my approach and not implemented some things that I normally would (service classes
in particular).
