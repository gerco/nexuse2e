/**
 * 
 */
package org.nexuse2e.service.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.DataConversionService;

/**
 * This class serves as a dummy implementation of a data mapping service.
 * While it takes a full configuration and accepts input like a normal service, it will always
 * reply with the String "42".
 * 
 * @author JJerke
 */
public class DummyMappingService extends AbstractService implements DataMapper {
	
	/*
	 * ---------------------
	 * CONSTANTS & VARIABLES
	 * ---------------------
	 */
	
	private static Logger	LOG                     = Logger.getLogger( DummyMappingService.class );
	
	private static final String BACKEND_ACCESS_URL  = "backend access url";
	private static final String BACKEND_ACCESS_USERNAME = "backend username";
	private static final String BACKEND_ACCESS_PASSWORD = "backend password";
	
	private String backendAccessUrl					= null;
	private String backendAccessUsername			= null;
	private String backendAccessPassword			= null;

	
	
	/*
	 * -------------------------
	 * PARAMETERS & CONSTRUCTION
	 * -------------------------
	 */

	/* (non-Javadoc)
	 * @see org.nexuse2e.service.AbstractService#fillParameterMap(java.util.Map)
	 */
	@Override
	public void fillParameterMap(Map<String, ParameterDescriptor> parameterMap) {
		parameterMap.put( BACKEND_ACCESS_URL, new ParameterDescriptor( ParameterType.STRING, "Backend Access URL",
                "The full address under which the backend can be queried", "https://" ) );
        parameterMap.put( BACKEND_ACCESS_USERNAME, new ParameterDescriptor( ParameterType.STRING, "Backend Username",
                "Username for backend authentication", "" ) );
        parameterMap.put( BACKEND_ACCESS_PASSWORD, new ParameterDescriptor( ParameterType.PASSWORD, "Backend Password",
                "Password for backend authentication", "" ) );
	}
	
	/**
	 * Initializes this {@link DummyMappingService} with the configuration options entered by the user in the GUI.
	 */
	public void initialize( EngineConfiguration config ) throws InstantiationException {
		
		backendAccessUrl = getParameter(BACKEND_ACCESS_URL);
		if (null == backendAccessUrl || "".equals(backendAccessUrl)) {
			LOG.error("No access URL specified in my configuration options.");
		}
		
		backendAccessUsername = getParameter(BACKEND_ACCESS_USERNAME);
		if (null == backendAccessUsername || "".equals(backendAccessUsername)) {
			LOG.info("No username specified in my configuration options, I'll try without authentification.");
		}
		
		backendAccessPassword = getParameter(BACKEND_ACCESS_PASSWORD);
		if (null == backendAccessPassword || "".equals(backendAccessPassword)) {
			LOG.info("No password given in my configuration options, I'll try without authentification.");
		}
	}
	
	
	
	/*
	 * -------
	 * METHODS
	 * -------
	 */
	
	/* (non-Javadoc)
	 * @see org.nexuse2e.service.mapping.DataMapper#processConversion(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String processConversion(String input, String source, String target) {
		if (!getPossibleTypes().contains(source) || !getPossibleTypes().contains(target)) {
			LOG.error("dummy mapping service only accepts source or target types which are known by service");
			return "Meep";
		}
		return "42";
	}

	/* (non-Javadoc)
	 * @see org.nexuse2e.service.mapping.DataMapper#getPossibleTypes()
	 */
	public List<String> getPossibleTypes() {
		// For purposes of the dummy, a static list is used - ofc an actual implementation should query this from the backend.
		@SuppressWarnings("serial")
		List<String> supportedTypes = new ArrayList<String>() {{
			add("dummy");
			add("theType");
		}};
		
		return supportedTypes;
	}

	/* (non-Javadoc)
	 * @see org.nexuse2e.service.AbstractService#getActivationLayer()
	 */
	@Override
	public Layer getActivationLayer() {
		// TODO Auto-generated method stub
		return null;
	}
}
