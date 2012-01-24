/**
 * 
 */
package org.nexuse2e.service.mapping;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.nexuse2e.NexusException;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.service.AbstractService;

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
	private static final String BACKEND_LOCAL_NAME  = "backend local name";
	private static final String BACKEND_GLOBAL_NAME = "backend global name";
	private static final String PREEMPTIVE_AUTH_PARAM_NAME = "preemptiveAuth";
	
	private Set<String> supportedTypes				= new HashSet<String>() {
		private static final long serialVersionUID = -1650444491430683482L; 
		{
		add("LOCAL");
		add("GLOBAL");
	}};

	
	
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
        parameterMap.put( BACKEND_LOCAL_NAME, new ParameterDescriptor( ParameterType.STRING, "Local partnerId",
                "Overwrite this server's partnerID.", "" ) );
        parameterMap.put( BACKEND_GLOBAL_NAME, new ParameterDescriptor( ParameterType.STRING, "Global-Type name",
                "Specifies the name of the global type.", "" ) );
        parameterMap.put( PREEMPTIVE_AUTH_PARAM_NAME, new ParameterDescriptor(ParameterType.BOOLEAN, "Preemptive Authentication", 
        		"Check, if the HTTP client should use preemtive authentication.", Boolean.FALSE));
        
	}
	
	
	
	/*
	 * -------
	 * METHODS
	 * -------
	 */
	
	/* (non-Javadoc)
	 * @see org.nexuse2e.service.mapping.DataMapper#processConversion(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String processConversion(String input, String source, String target, String localPartnerId) throws NexusException {
		
		// Replace the "LOCAL" field
		String backendLocalName = getParameter(BACKEND_LOCAL_NAME);
		String backendGlobalName = getParameter(BACKEND_GLOBAL_NAME);
		if ("LOCAL".equals(source)) {
			if (null != backendLocalName && !"".equals(backendLocalName)) {
				source = backendLocalName;
			} else if (null != localPartnerId && !"".equals(localPartnerId)){
				source = localPartnerId;
			}
		} else if ("LOCAL".equals(target)) {
			if (null != backendLocalName && !"".equals(backendLocalName)) {
				target = backendLocalName;
			} else if (null != localPartnerId && !"".equals(localPartnerId)) {
				target = localPartnerId;
			}
		}
		
		// Replace the "GLOBAL" field
		if ("GLOBAL".equals(source)) {
			if (null != backendGlobalName && !"".equals(backendGlobalName)) {
				source = backendGlobalName;
			}
		} else if ("GLOBAL".equals(target)) {
			if (null != backendGlobalName && !"".equals(backendGlobalName)) {
				target = backendGlobalName;
			}
		}
		
		LOG.debug("dummy mapping service invoked, type is " + source + ", Target type is " + target);
		
		// Can we support the requested types?
//		if (!getPossibleTypes().contains(source) || !getPossibleTypes().contains(target)) {
//			LOG.error("dummy mapping service only accepts source or target types which are known by service");
//			return input;
//		}
		
		String output = "";
		int timeout = 5000;
        GetMethod method = null;
        HttpClient client = null;
        URL receiverURL;
		try {
			System.out.println("##MAPPINGSERVICE##: Source is " + source + ", target is " + target + ", input is " + input + ", URL is " + getParameter(BACKEND_ACCESS_URL));
			input = "cdwe1";
			receiverURL = new URL(getParameter(BACKEND_ACCESS_URL) + source + "/" + target + "/" + input);
			String pwd = getParameter(BACKEND_ACCESS_PASSWORD);
			String user = getParameter(BACKEND_ACCESS_USERNAME);
			LOG.debug(new LogMessage("ConnectionURL:" + receiverURL));
			client = new HttpClient();
			
			if (!receiverURL.toString().toLowerCase().startsWith("https")) {
				client.getHostConfiguration().setHost(receiverURL.getHost(), receiverURL.getPort());
			}
			client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
			client.getHttpConnectionManager().getParams().setSoTimeout(timeout);
			method = new GetMethod(receiverURL.getPath());
			method.setFollowRedirects(false);
			method.getParams().setSoTimeout(timeout);
			LOG.trace(new LogMessage("Created new NexusHttpConnection with timeout: " + timeout));

			// Use basic auth if credentials are present
			if ((user != null) && (user.length() != 0) && (pwd != null)) {
			    Credentials credentials = new UsernamePasswordCredentials(user, pwd);
			    LOG.debug(new LogMessage("HTTPBackendConnector: Using basic auth."));
			    client.getParams().setAuthenticationPreemptive((Boolean) getParameter(PREEMPTIVE_AUTH_PARAM_NAME));
			    client.getState().setCredentials(AuthScope.ANY, credentials);
			    method.setDoAuthentication(true);
			}
		} catch (MalformedURLException murle) {
			LOG.error(murle);
			throw new NexusException( "Error creating HTTP GET call: " + murle);
		}
        
		try {
			// Magic
            client.executeMethod(method);
            LOG.debug(new LogMessage("HTTP call done"));
            
            int statusCode = method.getStatusCode();
            if (statusCode == 400) {
            	LOG.warn(new LogMessage("Mapping access failed, input not understood by the server. HTTP status code follows in the next line:"));
            }
            if (statusCode > 299) {
                LOG.error(new LogMessage("Mapping access failed, server " + receiverURL +
                        " responded with status: " + statusCode));
                throw new NexusException("Mapping access failed, server " + receiverURL +
                        " responded with status: " + statusCode);
            } else if (statusCode < 200) {
                LOG.warn(new LogMessage("Partner server " + receiverURL +
                        " responded with status: " + statusCode));
            }
            

            // Read the return value
            byte[] body = method.getResponseBody();
            
            JSONObject resultJson = new JSONObject(new JSONTokener(body.toString()));
            if (null != resultJson.getJSONObject("result") && input.equals(resultJson.getJSONObject("result").get("sourceId").toString())) {
            	output = resultJson.getJSONObject("result").get("targetId").toString();
            }

            method.releaseConnection();

        } catch (ConnectTimeoutException cte) {
            LogMessage lm =  new LogMessage("Mapping access failed, connection timeout for URL: " + receiverURL + " - " + cte);
            LOG.warn(lm, cte);
            throw new NexusException(lm, cte);
        } catch (Exception ex) {
            LogMessage lm = new LogMessage("Mapping access failed failed: " + ex);
            LOG.warn(lm, ex);
            throw new NexusException(lm, ex);
        }
		
		
		return output;
	}

	/* (non-Javadoc)
	 * @see org.nexuse2e.service.mapping.DataMapper#getPossibleTypes()
	 */
	public Set<String> getPossibleTypes() {
		// For purposes of the dummy, a static list is used - ofc an actual implementation should query this from the backend.
		
		return supportedTypes;
	}

	/* (non-Javadoc)
	 * @see org.nexuse2e.service.AbstractService#getActivationLayer()
	 */
	@Override
	public Layer getActivationLayer() {
		return Layer.INTERFACES;
	}
}
