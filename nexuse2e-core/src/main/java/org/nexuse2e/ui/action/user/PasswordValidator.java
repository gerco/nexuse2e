package org.nexuse2e.ui.action.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nexuse2e.Engine;

/**
 * This class will validate any given password with the password validation property from the beans.properties.
 * If no property is set, the constructor will set the matching pattern to match every password.
 * 
 * @author BWestphal
 *
 */

public class PasswordValidator {
	private Pattern pattern;
	private Matcher matcher;
	
	private static String PASSWORD_PATTERN; // Engine.getInstance().getPasswordValidation();
	
	public PasswordValidator() {
		PASSWORD_PATTERN = Engine.getInstance().getPasswordValidation();
		if ( PASSWORD_PATTERN != null) {
			pattern = Pattern.compile(PASSWORD_PATTERN);
		}	else {
				PASSWORD_PATTERN = "(.*)";
				pattern = Pattern.compile(PASSWORD_PATTERN);
			}	
	}
	
	/**
	 * Validate password with regular expression set in bean.properties
	 * @param password password for validation
	 * @return false invalid, true valid
	 */
	public boolean validate(final String password) {
			matcher = pattern.matcher(password);
			return matcher.matches();			
	}
}
