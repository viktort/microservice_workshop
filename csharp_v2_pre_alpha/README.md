# MicroService Workshop C# Implementation by Fred George
Copyright 2015-17 by Fred George. May be copied with this notice, but not used in classroom training.

C# is easier to setup than Java since there is a primary tool, Visual Studio, for
development. I don't do C# development professionally, so my tool knowledge is limited.
For this exercise, I use the latest Community edition of Visual Studio, plus Resharper
from JetBrains.

There are tests for a common library. Getting those tests to run is _not_ required
for the class.

## Visual Studio setup
Open the sample code:
- From the Start Page, select "Open Project..."
- Navigate to the csharp_v2\src\MicroServiceWorkshop directory
- Select the solution file (not project file) titled "MicroServiceWorkshop"
- Click "Open"

If you have Resharper installed, you can run the tests to confirm all your libraries
have been installed and are accessible. Via the Solution Explorer, select 
"MicroServiceWorkshop.Tests" by right clicking, and select "Run Unit Tests". Don't
worry about this if you don't have Resharper.
 
Now tag the source and test directories:
    - File/Project Structure...
    - Select "Modules"
        -- Tag src directory as Sources
        -- Tag test directory as Tests
        -- Click "OK"

Confirm that everything builds correctly (and necessary libraries exist)

## IntelliJ execution
Each MicroService runs independently. So in IntelliJ, we need to setup up a
configuration for each.

For the Monitor service:
- Run/Edit Configurations...
- Click "+", and select Application
-- Name: Monitor_java
-- Main class: select "..." and click on the Monitor application
-- Program arguments: <IP address of RabbitMQ machine> <port of RabbitMQ>
-- JRE: Select an installed JRE (I used 1.8)
-- Click "OK"

For the Need service:
- Run/Edit Configurations...
- Click "+", and select Application
-- Name: Need_java
-- Main class: select "..." and click on the Need application
-- Program arguments: <IP address of RabbitMQ machine> <port of RabbitMQ>
-- JRE: Select an installed JRE (I used 1.8)
-- Click "OK"

Start each of the services:
- Run/Run.../Monitor_java
-- Service should start with message "[*] Waiting for messages. To exit press CTRL+C"
-- If you get a stack trace of any kind, look carefully to see what connection aspect 
is failing
- Run/Run.../Need_java
-- If you get a stack trace of any kind, look carefully to see what connection aspect 
is failing
-- Service should show JSON packets being sent
-- Monitor service should show JSON packets being received

## Next Steps
You're running MicroServices. Now let's write some more to run with these two.


