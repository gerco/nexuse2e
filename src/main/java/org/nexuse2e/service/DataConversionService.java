/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.nexuse2e.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.digester.RegexMatcher;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.Layer;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;

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

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

    }

    @Override
    public Layer getActivationLayer() {

        return Layer.CORE;
    }

    /**
     * @param value
     * @param definition
     * @return
     */
    public String processConversion( String value, MappingDefinition definition ) {

        return processConversion( value, definition, null );
    }

    /**
     * @param value
     * @param definition
     * @param aditionalValues
     * @return
     */
    public String processConversion( String value, MappingDefinition definition, Map<String, String> aditionalValues ) {

        // We return the unchanged value in case no command was found
        String result = value;

        String command = definition.getCommand();

        if ( aditionalValues == null ) {
            aditionalValues = new HashMap<String, String>();
        }
        if ( value != null ) {
            aditionalValues.put( "$value", value );
        }

        Pattern pattern = Pattern.compile( "[a-zA-Z]+" );
        Matcher matcher = pattern.matcher( command );

        if ( matcher.find() ) {

            String commandName = matcher.group();
            int endIndex = matcher.end();
            System.out.println( "command(" + endIndex + "): " + commandName );
            LOG.debug( "command(" + endIndex + "): " + commandName );

            pattern = Pattern.compile( "[a-zA-Z0-9\\'\\,\\:\\$\\#\\.\\\\\\-\\_\\. \\@\\[\\]\\+]+" );
            matcher = pattern.matcher( command );
            ArrayList<String> paramList = null;
            if ( matcher.find( endIndex ) ) {
                paramList = new ArrayList<String>();
                String params = matcher.group();
                LOG.debug( "parameterlist:" + params );

                pattern = Pattern
                        .compile( "((\\'([a-zA-Z0-9\\,\\:\\-\\_\\. \\@\\#\\[\\]\\+]|\\\\')+\\')|(\\$[a-zA-Z0-9\\_]+))+" );
                matcher = pattern.matcher( params );
                while ( matcher.find() ) {
                    String param = matcher.group();
                    paramList.add( param );
                    LOG.debug( "param: " + param );
                }
            }
            String[] paramArray = null;
            if ( paramList != null && paramList.size() > 0 ) {
                paramArray = paramList.toArray( new String[paramList.size()] );
            }
            result = executeCommand( commandName, paramArray, definition, aditionalValues );
        }

        // Process any formatting rules (length etc.)
        if ( ( definition.getLengthTarget() != 0 ) && ( result.length() > definition.getLengthTarget() ) ) {
            result = result.substring( 0, definition.getLengthTarget() );
        }

        return result;
    }

    /**
     * @param value
     * @param name
     * @param params
     * @param definition
     * @return
     */
    private String executeCommand( String name, String[] params, MappingDefinition definition,
            Map<String, String> aditionalValues ) {

        if ( name == null ) {
            return null;
        }
        if ( name.equals( MAPPINGTABLE_LEFT2RIGHT ) ) {
            LOG.debug( "dispatching: Left2right" );
            return processDBMapping( definition.getCategory(), true, params, aditionalValues );
        } else if ( name.equals( MAPPINGTABLE_RIGHT2LEFT ) ) {
            LOG.debug( "dispatching: right2left" );
            return processDBMapping( definition.getCategory(), false, params, aditionalValues );
        } else if ( name.equals( DATEFORMAT ) ) {
            LOG.debug( "dispatching: dateformat" );
            return processDateFormat( params, definition, aditionalValues );
        } else if ( name.equals( STATIC ) ) {
            LOG.debug( "dispatching: static" );
            return processStatic( params, definition, aditionalValues );
        } else if ( name.equals( SUBSTRING ) ) {
            LOG.debug( "dispatching: substring" );
            return processSubstring( params, definition, aditionalValues );
        } else if ( name.equals( REGEX ) ) {
            LOG.debug( "dispatching: regex" );
            return processRegex( params, definition, aditionalValues );
        } else if ( name.equals( VALIDATEPATTERN ) ) {
            LOG.debug( "dispatching: validatepattern" );
            return processValidatePattern( params, definition, aditionalValues );
        } else if ( name.equals( VALIDATELENGTH ) ) {
            LOG.debug( "dispatching: validatelength" );
            return processValidateLength( params, definition, aditionalValues );
        } else if ( name.equals( MODIFY ) ) {
            LOG.debug( "dispatching: modify" );
            return processModify( params, definition, aditionalValues );
        } else {
            LOG.error( "Method: " + name + " is not a valid conversion method!" );
        }

        return null;
    }

    /**
     * @param params
     * @param definition
     * @param aditionalValues
     * @return
     */
    private String processModify( String[] params, MappingDefinition definition, Map<String, String> aditionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG.error( "static requires at least 3 parameter: modify[$value,'aa.aa','aa,aa']" );
            return null;
        }
        System.out.println( "doing..." );
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param params
     * @param definition
     * @param aditionalValues
     * @return
     */
    private String processValidateLength( String[] params, MappingDefinition definition,
            Map<String, String> aditionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG.error( "static requires at least 3 parameter: validateLength[$value,'30','true']" );
            return null;
        }
        String value = aditionalValues.get( "$value" );
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
     * @param aditionalValues
     * @return
     */
    private String processValidatePattern( String[] params, MappingDefinition definition,
            Map<String, String> aditionalValues ) {

        if ( params == null || params.length < 4 ) {
            LOG.error( "static requires at least 3 parameter: validatePattern[$value,'decimal','.','true']" );
            return null;
        }

        String value = aditionalValues.get( "$value" );
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
            Pattern p = Pattern.compile( "[0-9]+" + delimiter + "[0-9]+" );
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
     * @param aditionalValues
     * @return
     */
    private String processRegex( String[] params, MappingDefinition definition, Map<String, String> aditionalValues ) {

        if ( params == null || params.length < 1 ) {
            LOG.error( "static requires at least 1 parameter: regex['the_static_value']" );
            return null;
        }
        if ( params == null || params.length < 2 ) {
            LOG.error( "static requires at least 2 parameter: regex[$value,'regular_expression']" );
            return null;
        }
        String value = null;
        try {
            if ( isVariable( params[0] ) ) {
                value = substitute( params[0], aditionalValues );
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
     * @param value
     * @param params
     * @param definition
     * @param aditionalValues
     * @return
     */
    private String processSubstring( String[] params, MappingDefinition definition, Map<String, String> aditionalValues ) {

        if ( params == null || params.length < 3 ) {
            LOG.error( "static requires at least 3 parameter: substring[$value,'startIndex','endIndex']" );
            return null;
        }

        try {
            String value = null;
            if ( isVariable( params[0] ) ) {
                value = substitute( params[0], aditionalValues );
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
     * @param aditionalValues
     * @return
     */
    private String processStatic( String[] params, MappingDefinition definition, Map<String, String> aditionalValues ) {

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
     * @param aditionalValues
     * @return
     */
    private String processDBMapping( String category, boolean left, String[] params, Map<String, String> aditionalValues ) {

        if ( category == null ) {
            LOG.error( "category must not be empty!" );
            return null;
        }

        MappingPojo pojo = Engine.getInstance().getActiveConfigurationAccessService()
                .getMappingByCategoryDirectionAndKey( category, left, aditionalValues.get( "$value" ) );
        if ( pojo != null ) {
            return left ? pojo.getRightValue() : pojo.getLeftValue();
        } else {
            LOG.error( "no mapping entry found for " + aditionalValues.get( "$value" ) + " and category " + category );
        }
        return null;
    }

    /**
     * @param value
     * @param params
     * @param definition
     * @param aditionalValues
     */
    private String processDateFormat( String[] params, MappingDefinition definition, Map<String, String> aditionalValues ) {

        if ( params == null || params.length < 2 ) {
            LOG.error( "dateformat requires at least 2 parameters: dateformat['sourceformat','targetformat']" );
            return null;
        }

        try {
            Date date = null;
            if ( isVariable( params[0] ) ) {
                if ( params[0].equals( "$now" ) ) {
                    date = new Date();
                }
            } else {
                SimpleDateFormat sourceFormat = new SimpleDateFormat( stripParameter( params[0] ) );
                date = sourceFormat.parse( aditionalValues.get( "$value" ).trim() );
            }
            SimpleDateFormat targetFormat = new SimpleDateFormat( stripParameter( params[1] ) );
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
            LOG.debug( "Stripped Parameter: " + resultparam );
            resultparam = StringUtils.replace( resultparam, "\\'", "'" );
            LOG.debug( "Stripped Parameter: " + resultparam );
            return resultparam;

        } else if ( param.startsWith( "$" ) ) {

        } else {
            LOG.debug( "Parameter: " + param + "is not valid" );
        }
        return null;
    }

    /**
     * @param param
     * @param aditionalValues
     * @return
     */
    private String substitute( String param, Map<String, String> aditionalValues ) throws ParseException {

        if ( StringUtils.isEmpty( param ) ) {
            throw new ParseException( "Parameter must not be empty!", 0 );
        }
        if ( !param.startsWith( "$" ) ) {
            throw new ParseException( "replaceable variables must start with $", 0 );
        }
        return aditionalValues.get( param );
    }

}
