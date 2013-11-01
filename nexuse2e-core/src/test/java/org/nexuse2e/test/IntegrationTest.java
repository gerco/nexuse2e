package org.nexuse2e.test;

import java.io.File;
import java.util.Properties;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntegrationTest {

	/** The tomcat instance. */
	private Tomcat mTomcat;
	/** The temporary directory in which Tomcat and the app are deployed. */
	private String mWorkingDir = "c:\\temp_deployment"; //System.getProperty("java.io.tmpdir");
	
	
	@Before
	public void setup() throws Throwable {
	  mTomcat = new Tomcat();
	  mTomcat.setPort(0);
	  mTomcat.setBaseDir(mWorkingDir);
	  mTomcat.getHost().setAppBase(mWorkingDir);
	  mTomcat.getHost().setAutoDeploy(true);
	  mTomcat.getHost().setDeployOnStartup(true);
	  
	  String contextPath = "/NEXUSe2e";
	  File webApp = new File(mWorkingDir, "NEXUSe2e");
	  File oldWebApp = new File(webApp.getAbsolutePath());
	  //FileUtils.deleteDirectory(oldWebApp);
	   
	  mTomcat.addWebapp("/NEXUSe2e", "NEXUSe2e");
	  
	  mTomcat.start();

	}

	
	@After
	public final void teardown() throws Throwable {
	  if (mTomcat.getServer() != null
	            && mTomcat.getServer().getState() != LifecycleState.DESTROYED) {
	        if (mTomcat.getServer().getState() != LifecycleState.STOPPED) {
	              mTomcat.stop();
	        }
	        mTomcat.destroy();
	    }
	}
	
	@Test
	public void test1() {
		int port = mTomcat.getConnector().getLocalPort();
		System.out.println("port: "+port);
		
	}
}
