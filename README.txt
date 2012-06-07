========= OSU Senior Project Capstone =========

Mt. Hood Meadows Ski App

GitHub repo: https://github.com/thetwoj/Meadows.git
Google Play link: https://play.google.com/store/apps/details?id=com.osu.sc.meadows

This application and all code contained within is considered open source by its developers. Clone away!

Requires: Android 2.3 or higher



========= Server Database Setup =========

This is meant to be an explanation of how one can setup this application's database on a server. The serving computer must be running mysql, appache, and php.

1) On the machine, create a new database using “mysqladmin create databaseName”.
2) Execute all of the statements within Code/Scripts/createTables.sql in order to create the necessary tables in the new database
3) Put the Meadows folder in whatever directory is available online.  Make sure that a web browser can connect to the folder, and take note of the URL.
4) Open up “Meadows/DatabaseFunctions.php”. From lines 9 to 13, edit the global variables db_server, db_username, db_password, and db_database to reflect the values of the new mysql database. Save, and close your IDE.
5) Open up the application's HttpPostTask.java file located at “MeadowsActivity/src/server/HttpPostTask .java”. Edit the global variable “baseUrl” to point to the URL of the Meadows folder noted in step 3. Save the file.
6) Push the updated code to the world quickly! Users will not connect to the new server until they get the update to the application.

After these steps, the application will connect to the server, executing the copied php scripts and interacting with the duplicated SQL database.



========= Server Database Transfer =========

This is meant to be an explanation of how one could move this application's database from one computer to another. The receiving computer must be running mysql, appache, and php.

1) Connect via shell to the old SQL server. Input “select DB_NAME() as DataBaseName” to get the name of the database. Dump the database into a file using:
 “mysqldump databaseName > dump.sql”.
2) Transfer dump.sql from the old machine to the new one.
3) On the new machine, create a new database using “mysqladmin create databaseName”.
4) Populate the database by running “mysql databaseName < dump.sql”.
5) Transfer the Meadows folder from the old machine to the new one. Put the Meadows folder in whatever directory is available online.  Make sure that a web browser can connect to the folder, and take note of the URL.
6) Open up “Meadows/DatabaseFunctions.php”. From lines 9 to 13, edit the global variables db_server, db_username, db_password, and db_database to reflect the values of the new mysql database. Save, and close your IDE.
7) Open up the application's HttpPostTask.java file located at “MeadowsActivity/src/server/HttpPostTask .java”. Edit the global variable “baseUrl” to point to the URL of the Meadows folder noted in step 5. Save the file.
8) Push the updated code to the world quickly! Users will not connect to the new server until they get the update to the application.

After these steps, the application will connect to the new server, executing the copied php scripts and interacting with the duplicated SQL database.