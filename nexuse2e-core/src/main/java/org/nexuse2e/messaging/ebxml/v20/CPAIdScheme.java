package org.nexuse2e.messaging.ebxml.v20;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nexuse2e.pojo.MessagePojo;

/**
 * Schemes for generating / parsing CPAId in ebXML. First in line is the default scheme. 
 * @author ssc
 */
public enum CPAIdScheme {
	CHOREOGRAPHYID {

		@Override
		public String makeCPAId(MessagePojo messagePojo) {
			return messagePojo.getConversation().getChoreography().getName();
		}

		@Override
		public String getChoreographyIdFromCPAId(String CPAId) {
			return CPAId;
		}
		
		@Override
		public String getDescription() {
			return "Choreography ID";
		}

	},
	URI {

		@Override
		public String makeCPAId(MessagePojo messagePojo) {
			return "uri://"
					+ messagePojo.getParticipant().getLocalPartner().getPartnerId()
					+ "/"
					+ messagePojo.getConversation().getPartner().getPartnerId()
					+ "/" + messagePojo.getConversation().getChoreography().getName();
		}

		@Override
		public String getChoreographyIdFromCPAId(String CPAId) {
			Matcher m = Pattern.compile( "uri://[^/]+/[^/]+/(.+)" ).matcher( CPAId );
			return ( m.matches() ? m.group( 1 ) : CPAId );
		}

		@Override
		public String getDescription() {
			return "uri://&lt;sender&gt;/&lt;recipient&gt;/&lt;choreography&gt;";
		}

	};

	public abstract String makeCPAId(MessagePojo messagePojo);

	public abstract String getChoreographyIdFromCPAId(String CPAId);
	
	public abstract String getDescription();
}
