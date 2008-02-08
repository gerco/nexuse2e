How to develop on NEXUSe2e Server?
==================================

This document describes the steps that are necessary to create the
NEXUSe2e Server project under Eclipse and build it with Maven.
Of course you can use a different IDE instead of Eclipse.

1 Download and install software

1.1 Install Eclipse SDK from http://www.eclipse.org

1.2 Install an Eclipse plugin that supports Subversion
e.g. "Subversive" from http://www.polarion.org

1.3 Install Maven from http://maven.apache.org and
add the Maven subfolder "bin" to the PATH variable
of your os.

1.4 Install Tomcat 6 from http://tomcat.apache.org

2. Create a new Java project in Eclipse

2.1 Create a new SVN repository location with the URL
https://nexuse2e-server.svn.sourceforge.net/svnroot/nexuse2e-server
and your sourceforge.net login and password.

2.2 Select the trunk or a certain branch from the new repository
location and choose "Check Out" or "Find/Check Out As..." from
the context menu.

The newly created project will show a lot of errors and problems
that we will be fixed with the following steps.

2.3 
Close the project in Eclipse and replace the file .classpath in the 
root directory of your project with the one from the dev subdirectory.
Reopen the project in Eclipse.
or
In the Properties for the newly created project
(Context Menu > "Properties" > "Java Build Path" > "Source")
select as source folders: "src/main/java" and "src/main/resources"
Define "src/main/webapp/WEB-INF/classes" as output folder
(these paths are directly under the projects root). You will have to 
add all required libraries manually. 

2.4 Add a new classpath variable to the global Eclipse preferences
("Window" > "Preferences" > "Java" > "Build Path" > "Classpath")
named "M2_REPO" that points to the directory ".m2/repository" in
your home directory (e.g. "/Users/<username>/.m2/repository" for OS X).
The ".m2" directory will be automatically created by Maven later.
Add another classpath variable named "TOMCAT_HOME" that points to
your Tomcat 6 installation directory.

3. Download necessary libraries

Most libraries NEXUSe2e depends on are managed by Maven and will be
automatically downloaded later. But there are some libraries that
must be downloaded manually, because of license issues.

3.1 Go into the subfolder "lib" under the project root. There is
one *.readme file for each needed library. Each *.readme file contains
a download URL and a library version number.

3.2 Copy all downloaded libraries into the "lib" folder and check
whether their file names match the names specified in the "maven_imports.txt"
under the project root.
The "maven_imports.txt" contains one line for each library in the "lib" folder.
Each line ends with a "-Dfile" parameter (e.g. "-Dfile=lib/jta-1_0_1B-classes.zip").
If the file name of a downloaded library does not match the file name specified
by the "-Dfile" parameter, rename the according file in the "lib" folder.

4. Run Maven to build the project

4.1 Open a terminal console and cd to the project root (typically below your Eclipse
workspace). Ensure that the "bin" folder from the Maven install directory is in your
PATH (as recommended in 3.). Otherwise you have to type the entire path to the "mvn"
script in the following steps.

4.1.1 Copy the lines from the "maven_imports.txt" to the console and execute them.
Maven will now install the libraries.

4.1.2 Finally type "mvn package" and hit return. Maven will now download all the other
nessessary libraries and build the project.

5. Install Reporting package (optional). Go to http://www.eclipse.org/birt/ and download
the latest version of the BIRT reporting runtime package. Extract the "ReportEngine"
directory to the web application's WEB-INF directory and rename it to "platform".

If everything worked right your Eclipse project should not signal any errors or problems
anymore. If not, try to "Project" > "Clean".
The built "NEXUSe2e.war" file can be found in the "target" folder under the project root.




