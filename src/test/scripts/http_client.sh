#!/bin/sh
export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home
/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home/bin/java -version
/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home/bin/java -classpath ./lib/commons-codec-1.3.jar:./lib/commons-httpclient-3.0.1.jar:./lib/commons-logging-1.0.4.jar:./lib/nexuse2e_int-http.jar org.nexuse2e.integration.client.HttpIntegrationClient -url http://localhost:8080/NEXUSe2e/integration/http -choreography GenericFile -participant roma9080 -action SendFile -key /Volumes/NexusE2E/a_test.xml