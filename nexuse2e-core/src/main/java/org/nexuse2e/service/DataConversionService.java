/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;
import org.w3c.dom.Document;

public class DataConversionService extends AbstractService {

    private static Logger      LOG                     = Logger.getLogger( DataConversionService.class );

    public final static String MAPPINGTABLE_LEFT2RIGHT = "left";
    public final static String MAPPINGTABLE_RIGHT2LEFT = "right";
    public final static String DATEFORMAT              = "dateformat";
    public final static String STATIC                  = "static";
    public final static String SUBSTRING               = "substring";
    public final static String REGEX                   = "regex";
    public final static String VALIDATEPATTERN         = "validatePattern";
    public final static String VALIDATELENGTH          = "validateLength";
    public final static String MODIFY                  = "modify";
    public final static String REPLACE_STRING          = "replaceString";
    public final static String FILL_LEFT               = "fillLeft";
    public final static String FILL_RIGHT              = "fillRight";
    public final static String STRIP_LEADING           = "stripLeading";
    public final static String CONCAT                  = "concat";
    public final static String XPATH                   = "xpath";
    public final static String NUMBERFORMAT            = "numberFormat";
    public final static String ADD                     = "add";
    public final static String SUBTRACT                = "subtract";
    public final static String MULTIPLY                = "multiply";
    public final static String DIVIDE                  = "divide";

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

    }

    @Override
    public Layer getActivationLayer() {

        return Layer.CORE;
    }

    /**
     * Process a conversion operation.
     * @param value The value to be converted.
     * @param definition The mapping definition.
     * @return The converted value.
     */
    public String processConversion( String value, MappingDefinition definition ) throws DataConversionException {

        return processConversion( null, null, value, definition, null );
    }

    /**
     * Process a conversion operation.
     * @param xPath The <code>XPath</code> object to be used for XPath statement lookups. Can be
     * <code>null</code> if not operating on an XML document.
     * @param document The XML document. Can be <code>null</code> if not operating on an XML document.
     * @param value The value to be converted.
     * @param definition The mapping definition.
     * @return The converted value.
     */
    public String processConversion( XPath xPath, Document document, String value, MappingDefinition definition ) throws DataConversionException {

        return processConversion( xPath, document, value, definition, null );
    }

    /**
     * Process a conversion operation.
     * @param value The value to be converted
     * @param definition The mapping definition.
     * @param additionalValues Additional values that can be used for conversion operations.
     * May be <code>null</code>.
     * @return The converted value.
     */
    public String processConversion( String value, MappingDefinition definition, Map<String, String> additionalValues ) throws DataConversionException {

        return processConversion( null, null, value, definition, additionalValues );
    }

    /**
     * Process a conversion operation.
     * @param xPath The <code>XPath</code> object to be used for XPath statement lookups. Can be
     * <code>null</code> if not operating on an XML document.
     * @param document The XML document. Can be <code>null</code> if not operating on an XML document.
     * @param value The value to be converted.
     * @param definition The mapping definition.
     * @param additionalValues Additional values that can be used for conversion operations.
     * May be <code>null</code>.
     * @return The converted value.
     */
    public String processConversion( XPath xPath, Document document, String value, MappingDefinition definition,
            Map<String, String> additionalValues ) throws DataConversionException {

        // We return the unchanged value in case no command was found
        String result = value;

        String command = definition.getCommand();

        if ( additionalValues == null ) {
            additionalValues = new HashMap<String, String>();
        }
        if ( value != null ) {
            additionalValues.put( "$value", value );
        }

        Pattern pattern = Pattern.compile( "[a-zA-Z]+" );
        Matcher matcher = pattern.matcher( command );

        if ( matcher.find() ) {

            String commandName = matcher.group();
            int endIndex = matcher.end();
            // LOG.trace( "command(" + endIndex + "): " + commandName );

            pattern = Pattern.compile( "[a-zA-Z0-9/\\(\\)\\'\\,\\:\\$\\#\\.\\\\\\-\\_\\. \\@\\[\\]\\+]+" );
            matcher = pattern.matcher( command );
            ArrayList<String> paramList = null;
            if ( matcher.find( endIndex ) ) {
                paramList = new ArrayList<String>();
                String params = matcher.group();
                // LOG.trace( "parameterlist:" + params );

                pattern = Pattern
                        .compile( "((\\'([a-zA-Z0-9/\\(\\)\\,\\:\\-\\_\\. \\@\\#\\[\\]\\+]|\\\\')+\\')|(\\$[a-zA-Z0-9\\_]+))+" );
                matcher = pattern.matcher( params );
                while ( matcher.find() ) {
                    String param = matcher.group();
                    paramList.add( param );
                    // LOG.trace( "param: " + param );
                }
            }
            String[] paramArray = null;
            if ( paramList != null && paramList.size() > 0 ) {
                paramArray = paramList.toArray( new String[paramList.size()] );
            }
            result = executeCommand( xPath, document, commandName, paramArray, definition, additionalValues );
        }

        // Process any formatting rules (length etc.)
        if ( ( definition.getLengthTarget() != 0 ) && ( result.length() > definition.getLengthTarget() ) ) {
            result = result.substring( 0, definition.getLengthTarget() );
        }

        return result;
    }

    private String executeCommand( XPath xPath, Document document, String name, String[] params,
            MappingDefinition definition, Map<String, String> additionalValues ) throws DataConversionException {

        if ( name == null ) {
            return null;
        }
        if ( name.equals( MAPPINGTABLE_LEFT2RIGHT ) ) {
            LOG.trace( "dispatching: Left2right" );
            return processDBMapping( definition.getCategory(), true, params, additionalValues );
        } else if ( name.equals( MAPPINGTABLE_RIGHT2LEFT ) ) {
            LOG.trace( "dispatching: right2left" );
            return processDBMapping( definition.getCategory(), false, params, additionalValues );
        } else if ( name.equals( DATEFORMAT ) ) {
            LOG.trace( "dispatching: dateformat" );
            return processDateFormat( params, definition, additionalValues );
        } else if ( name.equals( STATIC ) ) {
            LOG.trace( "dispatching: static" );
            return processStatic( params, definition, additionalValues );
        } else if ( name.equals( SUBSTRING ) ) {
            LOG.trace( "dispatching: substring" );
            return processSubstring( params, definition, additionalValues );
        } else if ( name.equals( REGEX ) ) {
            LOG.trace( "dispatching: regex" );
            return processRegex( params, definition, additionalValues );
        } else if ( name.equals( VALIDATEPATTERN ) ) {
            LOG.trace( "dispatching: validatepattern" );
            return processValidatePattern( params, definition, additionalValues );
        } else if ( name.equals( VALIDATELENGTH ) ) {
            LOG.trace( "dispatching: validatelength" );
            return processValidateLength( params, definition, additionalValues );
        } else if ( name.equals( MODIFY ) ) {
            LOG.trace( "dispatching: modify" );
            return processModify( params, definition, additionalValues );
        } else if ( name.equals( REPLACE_STRING ) ) {
            LOG.trace( "dispatching: replaceString" );
            return processReplaceString( params, definition, additionalValues );
        } else if ( name.equals( FILL_LEFT ) ) {
            LOG.trace( "dispatching: fillLeft" );
            return processFill( true, params, definition, additionalValues );
        } else if ( name.equals( FILL_RIGHT ) ) {
            LOG.trace( "dispatching: fillRight" );
            return processFill( false, params, definition, additionalValues );
        } else if ( name.equals( STRIP_LEADING ) ) {
            LOG.trace( "dispatching: stripLeading" );
            return processStripLeading( params, definition, additionalValues );
        } else if ( name.equals( CONCAT ) ) {
            LOG.trace( "dispatching: concat" );
            return processConcat( xPath, document, params, definition, additionalValues );
        } else if ( name.equals( XPATH ) ) {
            LOG.trace( "dispatching: xpath" );
            return processXPath( xPath, document, params, definition, additionalValues );
        } else if ( name.equals( NUMBERFORMAT ) ) {
            LOG.trace( "dispatching: " + name );
            return processNumberFormat( params, additionalValues );
        } else if ( name.equals( ADD ) ) {
            LOG.trace( "dispatching: " + name );
            return processAdd( params, additionalValues );
        } else if ( name.equals( SUBTRACT ) ) {
            LOG.trace( "dispatching: " + name );
            return processSubtract( params, additionalValues );
        } else if ( name.equals( MULTIPLY ) ) {
            LOG.trace( "dispatching: " + name );
            return processMultiply( params, additionalValues );
        } else if ( name.equals( DIVIDE ) ) {
            LOG.trace( "dispatching: " + name );
            return processDivide( params, additionalValues );
        } else {
            LOG.trace( "Method: " + name + " is not a valid conversion method!" );
        }

        return null;
    }

    /**
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processModify( String[] params, MappingDefinition definition, Map<String, String> additionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG.error( "modify requires at least 3 parameter: modify[$value,'aa.aa','aa,aa']" );
            return null;
        }
        System.out.println( "doing..." );
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processValidateLength( String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG.error( "validateLength requires at least 3 parameter: validateLength[$value,'30','true']" );
            return null;
        }
        String value = additionalValues.get( "$value" );
        String lengthStr = stripParameter( params[1] );
        String emptyAllowed = stripParameter( params[2] );
        if ( emptyAllowed.toLowerCase().equals( "true" ) ) {
            if ( value.length() == 0 ) {
                return value;
            }
        } else {
            if ( value.length() == 0 ) {
                return null;
            }
        }
        try {
            int length = Integer.parseInt( lengthStr );
            if ( value.length() > length ) {
                return value.substring( 0, length );
            } else {
                return value;
            }
        } catch ( NumberFormatException e ) {
            LOG.debug( ">" + lengthStr + "< is not a valid value for length" );
        }

        return null;
    }

    /**
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processValidatePattern( String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG
                    .error( "pattern validation requires at least 3 parameter! e.g: validatePattern[$value,'decimal','.','true'] / validatePattern[$value,'numeric','true']" );
            return null;
        }

        String value = additionalValues.get( "$value" );
        String pattern = stripParameter( params[1] );

        if ( pattern.equals( "decimal" ) ) {
            String delimiter = stripParameter( params[2] );
            String emptyAllowed = stripParameter( params[3] );
            if ( emptyAllowed.toLowerCase().equals( "true" ) ) {
                if ( value.length() == 0 ) {
                    return value;
                }
            } else {
                if ( value.length() == 0 ) {
                    return null;
                }
            }
            Pattern p = Pattern.compile( "[0-9]+[" + delimiter + "]*[0-9]*" );
            Matcher matcher = p.matcher( value );
            if ( matcher.matches() ) {
                return value;
            }
        } else if ( pattern.equals( "numeric" ) ) {
            //max length (MAX_INT)
            //            if ( value.length() > 10 ) {
            //                LOG.error( "value: " + value + " exceeds max_int" );
            //                return null;
            //            }
            String emptyAllowed = stripParameter( params[2] );
            if ( emptyAllowed.toLowerCase().equals( "true" ) ) {
                if ( value.length() == 0 ) {
                    return value;
                }
            } else {
                if ( value.length() == 0 ) {
                    return null;
                }
            }
            Pattern p = Pattern.compile( "[0-9]+" );
            Matcher matcher = p.matcher( value );
            if ( matcher.matches() ) {
                return value;
            }
        }

        return null;
    }

    /**
     * @param value
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processRegex( String[] params, MappingDefinition definition, Map<String, String> additionalValues ) {

        if ( params == null || params.length < 1 ) {
            LOG.error( "regex requires at least 1 parameter: regex['the_static_value']" );
            return null;
        }
        if ( params == null || params.length < 2 ) {
            LOG.error( "regex requires at least 2 parameter: regex[$value,'regular_expression']" );
            return null;
        }
        String value = null;
        try {
            if ( isVariable( params[0] ) ) {
                value = substitute( params[0], additionalValues );
            } else {
                value = stripParameter( params[0] );
            }
            String exp = stripParameter( params[1] );

            if ( !StringUtils.isEmpty( exp ) ) {
                Pattern pattern = Pattern.compile( exp );

                Matcher matcher = pattern.matcher( value );

                if ( matcher.find() ) {

                    String result = matcher.group();
                    LOG.trace( "Found match: " + result + " - " + matcher.start() + " - " + matcher.end() );
                    return result;
                } else {
                    LOG.error( "No match found: " + exp + " - " + value );
                }
            }

        } catch ( ParseException e ) {
            LOG.error( "Error while parsing parameters: " + e );
        }

        return null;
    }

    /**
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processReplaceString( String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        if ( params == null || params.length < 2 ) {
            LOG.error( "replaceString requires at least 2 parameter: replaceString['.',',']" );
            return null;
        }
        try {
            String value = additionalValues.get( "$value" );
            if ( !StringUtils.isEmpty( value ) ) {
                String target = stripParameter( params[0] );
                String replacement = stripParameter( params[1] );

                return value.replace( target, replacement );
            }
        } catch ( Exception e ) {
            LOG.error( "Error while replacing String: " + e );
        }
        // TODO Auto-generated method stub
        return null;
    }

    private String processFill( boolean left, String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        String n;
        if ( left ) {
            n = "fillLeft";
        } else {
            n = "fillRight";
        }

        if ( params == null || params.length < 2 ) {
            LOG.error( n + " requires at least 2 parameters: fillLeft[char,count]" );
            return null;
        }
        try {
            String value = additionalValues.get( "$value" );
            if ( !StringUtils.isEmpty( value ) ) {
                String character = stripParameter( params[0] );
                if ( StringUtils.isEmpty( character ) ) {
                    LOG.error( n + ": char parameter must not be empty" );
                    return null;
                }
                char c = character.charAt( 0 );
                int count = 0;
                try {
                    count = Integer.parseInt( stripParameter( params[1] ) );
                } catch ( NumberFormatException nfex ) {
                    LOG.error( n + ": invalid count parameter: " + params[1] );
                    return null;
                }
                int fillLen = count - value.length();
                if ( fillLen > 0 ) {
                    char[] filler = new char[fillLen];
                    for ( int i = 0; i < filler.length; i++ ) {
                        filler[i] = c;
                    }
                    if ( left ) {
                        value = new String( filler ) + value;
                    } else {
                        value += new String( filler );
                    }
                }
                return value;
            }
        } catch ( Exception e ) {
            LOG.error( "Error while " + n + ": " + e );
        }
        return null;
    }

    private String processStripLeading( String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        if ( params == null || params.length < 1 ) {
            LOG.error( "stripLeading requires at least one parameters: stripLeading[char]" );
            return null;
        }

        String value = additionalValues.get( "$value" );
        if ( value != null ) {
            String character = stripParameter( params[0] );
            if ( StringUtils.isEmpty( character ) ) {
                LOG.error( "stripLeading: char parameter must not be empty" );
                return null;
            }
            char c = character.charAt( 0 );
            int offset;
            char[] chars = value.toCharArray();
            for ( offset = 0; offset < chars.length && chars[offset] == c; offset++ )
                ;
            value = new String( chars, offset, chars.length - offset );
        }
        return value;
    }

    private String processConcat( XPath xPath, Document document, String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        if ( xPath == null || document == null ) {
            LOG.error( "concat can only be executed on XML documents" );
        }
        try {
            String value = additionalValues.get( "$value" );
            if ( !StringUtils.isEmpty( value ) ) {
                StringBuilder sb = new StringBuilder( value );
                if ( params != null ) {
                    for ( String parameter : params ) {
                        String xPathStatement = stripParameter( parameter );
                        sb.append( xPath.evaluate( xPathStatement, document ) );
                    }
                }

                return sb.toString();
            }
        } catch ( Exception e ) {
            LOG.error( "Error while replacing String: " + e );
        }
        return null;
    }

    private String processXPath( XPath xPath, Document document, String[] params, MappingDefinition definition,
            Map<String, String> additionalValues ) {

        Object xPathResult = null;

        if ( params == null || params.length < 1 ) {
            LOG.error( "xpath requires at least one parameters: xpath[xpath statement]" );
            return null;
        }

        XPath xPathObj = XPathFactory.newInstance().newXPath();
        try {
            String xPathStatement = stripParameter( params[0] );
            LOG.trace( "Executing XPATH: " + xPathStatement );
            xPathResult = xPathObj.evaluate( xPathStatement, document, XPathConstants.STRING );
        } catch ( XPathExpressionException e ) {
            LOG.error( "Error executing XPath statement: " + e );
            e.printStackTrace();
        }
        if ( !( xPathResult instanceof String ) ) {
            LOG.error( "XPath did not return String result: " + xPathResult );
            return null;
        }

        return (String) xPathResult;
    }

    /**
     * @param value
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processSubstring( String[] params, MappingDefinition definition, Map<String, String> additionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG.error( "substring requires at least 3 parameter: substring[$value,'startIndex','endIndex']" );
            return null;
        }

        try {
            String value = null;
            if ( isVariable( params[0] ) ) {
                value = substitute( params[0], additionalValues );
                if ( StringUtils.isEmpty( value ) ) {
                    LOG.error( "unable to substitute variable: " + params[0] );
                    return null;
                }
            } else {
                value = stripParameter( params[0] );
            }
            params[1] = stripParameter( params[1] );
            params[2] = stripParameter( params[2] );
            try {
                int startIndex = Integer.parseInt( params[1] );
                int endIndex = Integer.parseInt( params[2] );
                if ( endIndex > value.length() || endIndex < 0 ) {
                    LOG.error( "invalid endIndex: " + endIndex );
                    return null;
                }
                if ( startIndex > value.length() || startIndex < 0 ) {
                    LOG.error( "invalid startIndex: " + startIndex );
                    return null;
                }
                if ( endIndex < startIndex ) {
                    LOG.error( "endIndex(" + endIndex + ") < startIndex(" + startIndex + ")!" );
                }
                return value.substring( startIndex, endIndex );

            } catch ( NumberFormatException e ) {
                LOG.error( "Invalid Numberformat for start or end" );
                return null;
            }

        } catch ( ParseException e ) {
            LOG.error( "Error while parsing parameters: " + e );
        }

        return null;
    }

    /**
     * @param value
     * @param params
     * @param definition
     * @param additionalValues
     * @return
     */
    private String processStatic( String[] params, MappingDefinition definition, Map<String, String> additionalValues ) {

        if ( params == null || params.length < 1 ) {
            LOG.error( "static requires at least 1 parameter: static['the_static_value']" );
            return null;
        }
        return stripParameter( params[0] );
    }

    /**
     * @param category
     * @param left
     * @param value
     * @param additionalValues
     * @return
     */
    private String processDBMapping( String category, boolean left, String[] params, Map<String, String> additionalValues ) {

        if ( category == null ) {
            LOG.error( "category must not be empty!" );
            return null;
        }

        MappingPojo pojo = Engine.getInstance().getActiveConfigurationAccessService()
                .getMappingByCategoryDirectionAndKey( category, left, additionalValues.get( "$value" ) );
        if ( pojo != null ) {
            return left ? pojo.getRightValue() : pojo.getLeftValue();
        } else {
            LOG.error( "no mapping entry found for " + additionalValues.get( "$value" ) + " and category " + category );
        }
        return null;
    }

    /**
     * @param value
     * @param params
     * @param definition
     * @param additionalValues
     */
    private String processDateFormat( String[] params, MappingDefinition definition, Map<String, String> additionalValues ) {

        if ( params == null || params.length < 2 ) {
            LOG
                    .error( "dateformat requires at least 2 parameters: dateformat['sourceformat','targetformat'], third parameter timezone is optional. '-0300' or 'local'" );
            return null;
        }

        try {
            Date date = null;
            if ( isVariable( params[0] ) ) {
                if ( params[0].equals( "$now" ) ) {
                    date = new Date();
                }
            } else {
                String dateValue = additionalValues.get( "$value" ).trim();

                // TODO: configurable  ?

                if ( dateValue.substring( dateValue.length() - 3 ).startsWith( ":" ) ) {
                    LOG.info( "dateValue needs to be modified, unparesable ':' in timezone found" );
                    String newDate = dateValue.substring( 0, dateValue.length() - 3 )
                            + dateValue.substring( dateValue.length() - 2 );
                    dateValue = newDate;
                }

                // TODO configurable  ?
                if ( dateValue.endsWith( "Z" ) ) {
                    LOG.info( "dateValue ends with Z. UTC is expected and Z is replaced with '-0000'" );
                    String newDate = dateValue.substring( 0, dateValue.length() - 1 ) + "+0000";
                    dateValue = newDate;
                }

                String datePattern = stripParameter( params[0] );
                SimpleDateFormat sourceFormat = new SimpleDateFormat( datePattern );
                date = sourceFormat.parse( dateValue );
            }
            SimpleDateFormat targetFormat = new SimpleDateFormat( stripParameter( params[1] ) );

            if ( params.length > 2 ) {
                System.out.println( "timezone transformation: " + params[2] );
                String timezone = stripParameter( params[2] );
                if ( !StringUtils.isEmpty( timezone ) ) {
                    if ( timezone.toLowerCase().equals( "local" ) ) {
                        // SimpleDateFormat uses server local time as base.
                    } else {
                        TimeZone zone = TimeZone.getTimeZone( timezone );
                        targetFormat.setTimeZone( zone );
                    }
                }
            }

            return targetFormat.format( date );
        } catch ( ParseException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "testdate";

    }

    /**
     * @param param
     * @return
     * @throws ParseException
     */
    private boolean isVariable( String param ) throws ParseException {

        if ( param.startsWith( "$" ) ) {
            return true;
        } else if ( param.startsWith( "'" ) && param.endsWith( "'" ) ) {
            return false;
        }
        throw new ParseException( "parameter: " + param + " is not a valid parameter.", 0 );
    }

    /**
     * @param param
     * @return
     */
    private String stripParameter( String param ) {

        if ( param == null ) {
            return null;
        }
        if ( param.startsWith( "'" ) && param.endsWith( "'" ) ) {
            String resultparam = param.substring( 1, param.length() - 1 );
            // LOG.debug( "Stripped Parameter: " + resultparam );
            resultparam = StringUtils.replace( resultparam, "\\'", "'" );
            // LOG.trace( "Stripped Parameter: " + resultparam );
            return resultparam;

        } else if ( param.startsWith( "$" ) ) {
            // TODO why is this here?
            LOG.debug( "Parameter starts with $: " + param );
        } else {
            LOG.debug( "Parameter: " + param + "is not valid" );
        }
        return null;
    }

    /**
     * @param param
     * @param additionalValues
     * @return
     */
    private String substitute( String param, Map<String, String> additionalValues ) throws ParseException {

        if ( StringUtils.isEmpty( param ) ) {
            throw new ParseException( "Parameter must not be empty!", 0 );
        }
        if ( !param.startsWith( "$" ) ) {
            throw new ParseException( "replaceable variables must start with $", 0 );
        }
        return additionalValues.get( param );
    }
    
    /**
     * Converts a number into a particular format.
     * @param params An array of Strings containing the parameters for the conversion.
     * 			     The first element is the input pattern for parsing the original number (as understood by {@link java.text.DecimalFormat}).
     *               The second element is the language code for which the input pattern is valid (must be a language code as specified by ISO-639).
     * 				 The third element is the output pattern for the number after conversion (as understood by {@link java.text.DecimalFormat}).
     *               The fourth element is the language code for which the output pattern is valid (must be a language code as specified by ISO-639).
     * @param additionalValues A mapping that should contain at least the value (mapped as <code>"$value"</code>)
     *                         that should be the operand. It must be a number in the format of the input pattern (<code>params[0]</code>)
     *                         which is valid for the specified language (<code>params[1]</code>).
     * @return The converted number or the value of <code>$value</code>, or <code>$value</code>, if it is <code>null</code> or an empty string.
     * @throws DataConversionException if an error occurs during conversion.
     */
    private String processNumberFormat( String[] params, Map<String, String> additionalValues ) throws DataConversionException {
        String result = null;
        String value = additionalValues.get( "$value" );
        
        if ( value != null && value.length() > 0 ) {
            if ( params.length >= 4 ) {
                String pattern_in = stripParameter( params[0] );
                String lang_code_in = stripParameter( params[1] );
                String pattern_out = stripParameter( params[2] );
                String lang_code_out = stripParameter( params[3] );
                try {
	                result = new DecimalFormat( pattern_out, new DecimalFormatSymbols( new Locale( lang_code_out ) ) ).format(
	                    new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( value ) );
                } catch ( Exception e ) {
                	throw new DataConversionException( "Cannot convert " + value + " from " + pattern_in + " (" + lang_code_in + ") to "
                			+ pattern_out + " (" + lang_code_out + ")", e );
                }
            } else {
                LOG.error( "numberFormat requires at least 4 parameters: numberFormat['pattern_in','lang_code_in','pattern_out','lang_code_out'] (patterns as understood by java.text.DecimalFormat, language codes as specified by ISO-639)" );
            }
        } else {
            result = value;
        }
        
        return result;
    }
    
    /**
     * Adds a scalar number to a numeric value.
     * @param params An array of Strings containing the parameters for the operation.
     * 			     The first element is the input pattern for parsing the original number (as understood by {@link java.text.DecimalFormat}).
     *               The second element is the language code for which the input pattern is valid (must be a language code as specified by ISO-639).
     * 				 The third element is the output pattern for the number after conversion (as understood by {@link java.text.DecimalFormat}).
     *               The fourth element is the language code for which the output pattern is valid (must be a language code as specified by ISO-639).
     *               The fifth element is the operand for the operation. It is assumed that it is in the format of the input pattern/language.
     * @param additionalValues A mapping that should contain at least the value (mapped as <code>"$value"</code>)
     *                         that should be the other operand. It must be a number in the format of the input pattern (<code>params[0]</code>)
     *                         which is valid for the specified language (<code>params[1]</code>).
     * @return The sum of the value of <code>$value</code> and the scalar number, or <code>$value</code>, if it is <code>null</code> or an empty string.
     * @throws DataConversionException if an error occurs during conversion.
     */
    private String processAdd( String[] params, Map<String, String> additionalValues ) throws DataConversionException {
    	String result = null;
        String value = additionalValues.get( "$value" );
        
        if ( value != null && value.length() > 0 ) {
            if ( params.length >= 5 ) {
            	String pattern_in = stripParameter( params[0] );
                String lang_code_in = stripParameter( params[1] );
                String pattern_out = stripParameter( params[2] );
                String lang_code_out = stripParameter( params[3] );
                String operand = stripParameter( params[4] );
                try {
	                Number valueNumber = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( value );
	                Number operandValue = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( operand );
	                double resultValue = valueNumber.doubleValue() + operandValue.doubleValue(); 
	                result = new DecimalFormat( pattern_out, new DecimalFormatSymbols( new Locale( lang_code_out ) ) ).format( resultValue );
                } catch ( Exception e ) {
                	throw new DataConversionException( "Cannot add " + operand + " to " + value, e );
                }
            } else {
            	LOG.error( "add requires at least 5 parameters: add['pattern_in','lang_code_in','pattern_out','lang_code_out','operand'] (patterns as understood by java.text.DecimalFormat, language codes as specified by ISO-639; it is assumed that the operand is in format of pattern_in/lang_code_in)" );
            }
        } else {
            result = value;
        }
        
        return result;
    }
    
    /**
     * Subtracts a scalar number to a numeric value.
     * @param params An array of Strings containing the parameters for the operation.
     * 			     The first element is the input pattern for parsing the original number (as understood by {@link java.text.DecimalFormat}).
     *               The second element is the language code for which the input pattern is valid (must be a language code as specified by ISO-639).
     * 				 The third element is the output pattern for the number after conversion (as understood by {@link java.text.DecimalFormat}).
     *               The fourth element is the language code for which the output pattern is valid (must be a language code as specified by ISO-639).
     *               The fifth element is the operand for the operation. It is assumed that it is in the format of the input pattern/language.
     * @param additionalValues A mapping that should contain at least the value (mapped as <code>"$value"</code>)
     *                         that should be the other operand. It must be a number in the format of the input pattern (<code>params[0]</code>)
     *                         which is valid for the specified language (<code>params[1]</code>).
     * @return The difference of the value of <code>$value</code> and the scalar number, or <code>$value</code>, if it is <code>null</code> or an empty string.
     * @throws DataConversionException if an error occurs during conversion.
     */
    private String processSubtract( String[] params, Map<String, String> additionalValues ) throws DataConversionException {
    	String result = null;
        String value = additionalValues.get( "$value" );
        
        if ( value != null && value.length() > 0 ) {
            if ( params.length >= 5 ) {
            	String pattern_in = stripParameter( params[0] );
                String lang_code_in = stripParameter( params[1] );
                String pattern_out = stripParameter( params[2] );
                String lang_code_out = stripParameter( params[3] );
                String operand = stripParameter( params[4] );
                try {
                	Number valueNumber = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( value );
	                Number operandValue = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( operand );
	                double resultValue = valueNumber.doubleValue() - operandValue.doubleValue(); 
	                result = new DecimalFormat( pattern_out, new DecimalFormatSymbols( new Locale( lang_code_out ) ) ).format( resultValue );
                } catch ( Exception e ) {
                	throw new DataConversionException( "Cannot subtract " + operand + " from " + value, e );
                }
            } else {
            	LOG.error( "subtract requires at least 5 parameters: subtract['pattern_in','lang_code_in','pattern_out','lang_code_out','operand'] (patterns as understood by java.text.DecimalFormat, language codes as specified by ISO-639; it is assumed that the operand is in format of pattern_in/lang_code_in)" );
            }
        } else {
            result = value;
        }
        
        return result;
    }
    
    /**
     * Multiplies a numeric value by a scalar number.
     * @param params An array of Strings containing the parameters for the operation.
     * 			     The first element is the input pattern for parsing the original number (as understood by {@link java.text.DecimalFormat}).
     *               The second element is the language code for which the input pattern is valid (must be a language code as specified by ISO-639).
     * 				 The third element is the output pattern for the number after conversion (as understood by {@link java.text.DecimalFormat}).
     *               The fourth element is the language code for which the output pattern is valid (must be a language code as specified by ISO-639).
     *               The fifth element is the operand for the operation. It is assumed that it is in the format of the input pattern/language.
     * @param additionalValues A mapping that should contain at least the value (mapped as <code>"$value"</code>)
     *                         that should be the other operand. It must be a number in the format of the input pattern (<code>params[0]</code>)
     *                         which is valid for the specified language (<code>params[1]</code>).
     * @return The product of the value of <code>$value</code> and the scalar number, or <code>$value</code>, if it is <code>null</code> or an empty string.
     * @throws DataConversionException if an error occurs during conversion.
     */
    private String processMultiply( String[] params, Map<String, String> additionalValues ) throws DataConversionException {
    	String result = null;
        String value = additionalValues.get( "$value" );
        
        if ( value != null && value.length() > 0 ) {
            if ( params.length >= 5 ) {
            	String pattern_in = stripParameter( params[0] );
                String lang_code_in = stripParameter( params[1] );
                String pattern_out = stripParameter( params[2] );
                String lang_code_out = stripParameter( params[3] );
                String operand = stripParameter( params[4] );
                try {
                	Number valueNumber = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( value );
	                Number operandValue = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( operand );
	                double resultValue = valueNumber.doubleValue() + operandValue.doubleValue(); 
	                result = new DecimalFormat( pattern_out, new DecimalFormatSymbols( new Locale( lang_code_out ) ) ).format( resultValue );
                } catch ( Exception e ) {
                	throw new DataConversionException( "Cannot multiply " + value + " by " + operand, e );
                }
            } else {
            	LOG.error( "multiply requires at least 5 parameters: multiply['pattern_in','lang_code_in','pattern_out','lang_code_out','operand'] (patterns as understood by java.text.DecimalFormat, language codes as specified by ISO-639; it is assumed that the operand is in format of pattern_in/lang_code_in)" );
            }
        } else {
            result = value;
        }
        
        return result;
    }
    
    /**
     * Divides a numeric value by a scalar number.
     * @param params An array of Strings containing the parameters for the operation.
     * 			     The first element is the input pattern for parsing the original number (as understood by {@link java.text.DecimalFormat}).
     *               The second element is the language code for which the input pattern is valid (must be a language code as specified by ISO-639).
     * 				 The third element is the output pattern for the number after conversion (as understood by {@link java.text.DecimalFormat}).
     *               The fourth element is the language code for which the output pattern is valid (must be a language code as specified by ISO-639).
     *               The fifth element is the operand for the operation. It is assumed that it is in the format of the input pattern/language.
     * @param additionalValues A mapping that should contain at least the value (mapped as <code>"$value"</code>)
     *                         that should be the other operand. It must be a number in the format of the input pattern (<code>params[0]</code>)
     *                         which is valid for the specified language (<code>params[1]</code>).
     * @return The quotient of the value of <code>$value</code> and the scalar number, or <code>$value</code>, if it is <code>null</code> or an empty string.
     * @throws DataConversionException if an error occurs during conversion.
     */
    private String processDivide( String[] params, Map<String, String> additionalValues ) throws DataConversionException {
    	String result = null;
        String value = additionalValues.get( "$value" );
        
        if ( value != null && value.length() > 0 ) {
            if ( params.length >= 5 ) {
            	String pattern_in = stripParameter( params[0] );
                String lang_code_in = stripParameter( params[1] );
                String pattern_out = stripParameter( params[2] );
                String lang_code_out = stripParameter( params[3] );
                String operand = stripParameter( params[4] );
                try {
                	Number valueNumber = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( value );
	                Number operandValue = new DecimalFormat( pattern_in, new DecimalFormatSymbols( new Locale( lang_code_in ) ) ).parse( operand );
	                double resultValue = valueNumber.doubleValue() / operandValue.doubleValue(); 
	                result = new DecimalFormat( pattern_out, new DecimalFormatSymbols( new Locale( lang_code_out ) ) ).format( resultValue );
                } catch ( Exception e ) {
                	throw new DataConversionException( "Cannot divide " + value + " by " + operand, e );
                }
            } else {
            	LOG.error( "divide requires at least 5 parameters: divide['pattern_in','lang_code_in','pattern_out','lang_code_out','operand'] (patterns as understood by java.text.DecimalFormat, language codes as specified by ISO-639; it is assumed that the operand is in format of pattern_in/lang_code_in)" );
            }
        } else {
            result = value;
        }
        
        return result;
    }    

}
