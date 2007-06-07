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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.Engine;
import org.nexuse2e.Constants.Runlevel;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.pojo.MappingPojo;
import org.nexuse2e.tools.mapping.xmldata.MappingDefinition;

public class DataConversionService extends AbstractService {

    private static Logger      LOG                     = Logger.getLogger( DataConversionService.class );

    public final static String MAPPINGTABLE_LEFT2RIGHT = "left";
    public final static String MAPPINGTABLE_RIGHT2LEFT = "right";
    public final static String DATEFORMAT              = "dateformat";
    public final static String STATIC                  = "static";

    @Override
    public void fillParameterMap( Map<String, ParameterDescriptor> parameterMap ) {

    }

    @Override
    public Runlevel getActivationRunlevel() {

        return Runlevel.OUTBOUND_PIPELINES;
    }

    /**
     * @param value
     * @param definition
     * @return
     */
    public String processConversion( String value, MappingDefinition definition ) {

        String command = definition.getCommand();

        Pattern pattern = Pattern.compile( "[a-z]+" );
        Matcher matcher = pattern.matcher( command );

        if ( matcher.find() ) {

            String commandName = matcher.group();
            int endIndex = matcher.end();

            LOG.debug( "command(" + endIndex + "): " + commandName );

            pattern = Pattern.compile( "[a-zA-Z0-9\\'\\,\\:\\$\\.\\\\\\-\\_\\. ]+" );
            matcher = pattern.matcher( command );
            ArrayList<String> paramList = null;
            if ( matcher.find( endIndex ) ) {
                paramList = new ArrayList<String>();
                String params = matcher.group();
                LOG.debug( "parameterlist:" + params );

                pattern = Pattern.compile( "((\\'([a-zA-Z0-9\\,\\:\\-\\_\\. ]|\\\\')+\\')|(\\$[a-zA-Z0-9\\_]+))+" );
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
            return dispatchCommand( value, commandName, paramArray, definition );
        }

        return null;
    }

    /**
     * @param value
     * @param name
     * @param params
     * @param definition
     * @return
     */
    public String dispatchCommand( String value, String name, String[] params, MappingDefinition definition ) {

        if ( name == null || value == null ) {
            return null;
        }
        if ( name.equals( MAPPINGTABLE_LEFT2RIGHT ) ) {
            LOG.debug( "dispatching: Left2right" );
            return processDBMapping( definition.getCategory(), true, value, params );
        } else if ( name.equals( MAPPINGTABLE_RIGHT2LEFT ) ) {
            LOG.debug( "dispatching: right2left" );
            return processDBMapping( definition.getCategory(), false, value, params );
        } else if ( name.equals( DATEFORMAT ) ) {
            LOG.debug( "dispatching: dateformat" );
            return processDateFormat( value, params, definition );
        } else if ( name.equals( STATIC ) ) {
            LOG.debug( "dispatching: static" );
            return processStatic( value, params, definition );
        }

        return null;
    }

    /**
     * @param value
     * @param params
     * @param definition
     * @return
     */
    private String processStatic( String value, String[] params, MappingDefinition definition ) {

        if ( value == null ) {
            LOG.error( "value must not be null!" );
            return null;
        }
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
     * @return
     */
    private String processDBMapping( String category, boolean left, String value, String[] params ) {

        if ( category == null ) {
            LOG.error( "category must not be empty!" );
            return null;
        }
        if ( value == null ) {
            LOG.error( "value must not be null" );
            return null;
        }
        MappingPojo pojo = Engine.getInstance().getActiveConfigurationAccessService()
                .getMappingByCategoryDirectionAndKey( category, left, value );
        if ( pojo != null ) {
            return left ? pojo.getRightValue() : pojo.getLeftValue();
        } else {
            LOG.error( "no mapping entry found for " + value + " and category " + category );
        }
        return null;
    }

    /**
     * @param value
     * @param params
     * @param definition
     */
    private String processDateFormat( String value, String[] params, MappingDefinition definition ) {

        if ( value == null ) {
            LOG.error( "value must not be null!" );
            return null;
        }
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
                date = sourceFormat.parse( value.trim() );
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
            LOG.debug( "Parameter: "+param+ "is not valid" );
        }
        return null;
    }

}
