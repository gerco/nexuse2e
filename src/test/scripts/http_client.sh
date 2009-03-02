#!/bin/sh
#
#  NEXUSe2e Business Messaging Open Source
#  Copyright 2000-2009, Tamgroup and X-ioma GmbH
#
#  This is free software; you can redistribute it and/or modify it
#  under the terms of the GNU Lesser General Public License as
#  published by the Free Software Foundation version 2.1 of
#  the License.
#
#  This software is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
#  Lesser General Public License for more details.
#
#  You should have received a copy of the GNU Lesser General Public
#  License along with this software; if not, write to the Free
#  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
#  02110-1301 USA, or see the FSF site: http://www.fsf.org.
#
export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home
/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home/bin/java -version
/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Home/bin/java -classpath ./lib/commons-codec-1.3.jar:./lib/commons-httpclient-3.0.1.jar:./lib/commons-logging-1.0.4.jar:./lib/nexuse2e_int-http.jar org.nexuse2e.integration.client.HttpIntegrationClient -url http://localhost:8080/NEXUSe2e/integration/http -choreography GenericFile -participant roma9080 -action SendFile -key /Volumes/NexusE2E/a_test.xml