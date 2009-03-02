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
package org.nexuse2e.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nexuse2e.Configurable;
import org.nexuse2e.configuration.Constants.ParameterType;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.util.EncryptionUtil;

/**
 * This utility class provides functionality that helps to
 * "fill" <code>Configurable</code>s.
 * 
 * @author jonas.reese
 */
public class ConfigurationUtil {

    // static class - hide constructor
    private ConfigurationUtil() {

    }

    /**
     * Converts a <code>String</code> parameter value into an object
     * of it's domain type.
     * 
     * @param pd The <code>ParameterDescriptor</code> that contains the type.
     * @param value The <code>String</code> representation to be converted.
     * @return The domain type, except for types ENUMERATION and DROPDOWN
     */
    private static Object getParameterValue( ParameterDescriptor pd, String value ) {

        switch ( pd.getParameterType() ) {
            case UNKNOWN:
            case STRING:
            case PASSWORD:
            case SERVICE:
                return value;
            case BOOLEAN:
                return Boolean.valueOf( value );
            default:
                return null;
        }
    }

    public static String getParameterStringValue( PipeletParamPojo param ) {

        if ( (param == null) || (param.getParameterDescriptor() == null) ) {
            return null;
        }
        switch ( param.getParameterDescriptor().getParameterType() ) {
            case PASSWORD:
                return EncryptionUtil.decryptString( param.getValue() );
            default:
                return param.getValue();
        }
    }

    public static void setParameterStringValue( PipeletParamPojo param, String value ) {

        switch ( param.getParameterDescriptor().getParameterType() ) {
            case PASSWORD:
                param.setValue( EncryptionUtil.encryptString( value ) );
                break;
            default:
                param.setValue( value );
        }
    }

    // pojo-independent helper method for a single configuration parameter
    private static void configure(
            Configurable configurable, Map<String, ParameterDescriptor> map, String key, String value, String label ) {

        ParameterDescriptor pd = map.get( key );
        if ( pd != null ) {
            // handle ENUMERATION types later
            if ( pd.getParameterType() == ParameterType.LIST ) {
                ListParameter dropdown = pd.getDefaultValue();
                if ( !dropdown.setSelectedValue( value ) ) {
                    dropdown.setSelectedIndex( 0 );
                }
                configurable.setParameter( key, dropdown );
            } else if ( pd.getParameterType() == ParameterType.ENUMERATION ) {
                EnumerationParameter enumeration = configurable.getParameter( key );
                if ( enumeration == null ) {
                    enumeration = pd.getDefaultValue();
                    configurable.setParameter( key, enumeration );
                }
                enumeration.putElement( label, value );
            } else {
                Object val = getParameterValue( pd, value );
                configurable.setParameter( key, val );
            }
        }
    }

    /**
     * Configure the given <code>Configurable</code> with a list of
     * <code>PipeletParamPojo</code> objects.
     * @param configurable The <code>Configurable</code> to be configured.
     * @param pojos The pojos that contain the configuration. It will be ignored
     * if the parameter name is not supported by the <code>Configurable</code>.
     */
    public static void configurePipelet( Configurable configurable, Collection<PipeletParamPojo> pojos ) {

        if ( pojos == null ) {
            return;
        }
        Map<String, ParameterDescriptor> map = configurable.getParameterMap();
        for ( PipeletParamPojo pojo : pojos ) {
            ParameterDescriptor parameterDescriptor = map.get( pojo.getParamName() );
            if ( parameterDescriptor != null ) {
                pojo.setParameterDescriptor( parameterDescriptor );
            }
            String key = pojo.getParamName();
            String value = getParameterStringValue( pojo ); // pojo.getValue()
            String label = pojo.getLabel();
            configure( configurable, map, key, value, label );
        }
    }

    /**
     * Configure the given <code>Configurable</code> with a list of
     * <code>LoggerParamPojo</code> objects.
     * @param configurable The <code>Configurable</code> to be configured.
     * @param pojos The pojos that contain the configuration. It will be ignored
     * if the parameter name is not supported by the <code>Configurable</code>.
     */
    public static void configureLogger( Configurable configurable, Collection<LoggerParamPojo> pojos ) {

        if ( pojos == null ) {
            return;
        }
        Map<String, ParameterDescriptor> map = configurable.getParameterMap();
        for ( LoggerParamPojo pojo : pojos ) {
            String key = pojo.getParamName();
            String value = pojo.getValue();
            String label = pojo.getLabel();
            configure( configurable, map, key, value, label );
        }
    }

    /**
     * Configure the given <code>Configurable</code> with a list of
     * <code>ServiceParamPojo</code> objects.
     * @param configurable The <code>Configurable</code> to be configured.
     * @param pojos The pojos that contain the configuration. It will be ignored
     * if the parameter name is not supported by the <code>Configurable</code>.
     */
    public static void configureService( Configurable configurable, Collection<ServiceParamPojo> pojos ) {

        if ( pojos == null ) {
            return;
        }
        Map<String, ParameterDescriptor> map = configurable.getParameterMap();
        for ( ServiceParamPojo pojo : pojos ) {
            String key = pojo.getParamName();
            String value = pojo.getValue();
            String label = pojo.getLabel();
            configure( configurable, map, key, value, label );
        }
    }

    /**
     * Gets a String representation of a parameter value.
     * @param value The value. Must be instanceof a <code>ParameterType</code>
     * enumeration element's {@link ParameterType#getType()} method.
     * @return The string representation, or <code>null</code> if <code>value</code>
     * is <code>null</code>.
     */
    private static String toString( Object value ) {

        if ( value == null ) {
            return null;
        }
        return value.toString();
    }
    
    
    private static PipeletParamPojo getPipeletParam(
            PipeletPojo pipeletPojo, ParameterDescriptor pd, int sequenceNum, String key, String enumKey ) {
        PipeletParamPojo pipeletParam = null;
        if ( pipeletPojo.getPipeletParams() != null ) {
            for ( PipeletParamPojo pp : pipeletPojo.getPipeletParams() ) {
                if ( pp.getParamName() != null && pp.getParamName().equals( key )
                        && (pd.getParameterType() != ParameterType.ENUMERATION || enumKey.equals( pp.getLabel() )) ) {
                    pipeletParam = pp;
                    break;
                }
            }
        }
        if ( pipeletParam == null ) {
            pipeletParam = new PipeletParamPojo();
            if (pd.getParameterType() != ParameterType.ENUMERATION) {
                pipeletParam.setLabel( pd.getLabel() );
            } else {
                pipeletParam.setLabel( null );
            }
            pipeletParam.setParamName( key );
            pipeletParam.setCreatedDate( new Date() );
        }
        pipeletParam.setPipelet( pipeletPojo );
        pipeletParam.setParameterDescriptor( pd );
        pipeletParam.setModifiedDate( new Date() );
        pipeletParam.setSequenceNumber( sequenceNum );
        return pipeletParam;
    }

    /**
     * Gets a <code>List</code> of <code>PipeletParamPojo</code> objects for the given
     * <code>Configurable</code> that represents the current configuration 
     * @param configurable The <code>Configurable</code> to extract the current
     * configuration from.
     * @param pipeletPojo The <code>PipeletPojo</code> that shall be set as parent for
     * the resulting <code>PipeletParamPojo</code> objects.
     * @return A <code>List</code> of <code>PipeletParamPojo</code> objects.
     */
    public static List<PipeletParamPojo> getConfiguration( Configurable configurable, PipeletPojo pipeletPojo ) {

        List<PipeletParamPojo> result = new ArrayList<PipeletParamPojo>();
        Map<String, ParameterDescriptor> params = configurable.getParameterMap();
        if ( params != null ) {
            int sequenceNum = 0;
            for ( String key : params.keySet() ) {
                ParameterDescriptor pd = params.get( key );
                if (pd.getParameterType() == ParameterType.ENUMERATION) {
                    EnumerationParameter enumeration = configurable.getParameter( key );
                    if (enumeration == null) {
                        enumeration = pd.getDefaultValue();
                    }
                    Map<String, String> map = enumeration.getElements();
                    for (String enumKey : map.keySet()) {
                        if (enumKey != null) {
                            PipeletParamPojo pipeletParam = getPipeletParam( pipeletPojo, pd, sequenceNum++, key, enumKey );
                            pipeletParam.setLabel( enumKey );
                            pipeletParam.setValue( map.get( enumKey ) );
                            result.add( pipeletParam );
                        }
                    }
                    // add a single pojo with null label/value representing the placeholder for new entries
                    PipeletParamPojo pipeletParam = new PipeletParamPojo();
                    pipeletParam.setSequenceNumber( 1000000000 );
                    pipeletParam.setParamName( key );
                    pipeletParam.setPipelet( pipeletPojo );
                    pipeletParam.setParameterDescriptor( pd );
                    result.add( pipeletParam );
                } else {
                    PipeletParamPojo pipeletParam = getPipeletParam( pipeletPojo, pd, sequenceNum++, key, null );
                    if ( pd.getParameterType() == ParameterType.LIST ) {
                        ListParameter dropdown = configurable.getParameter( key );
                        if ( dropdown != null ) {
                            setParameterStringValue( pipeletParam, dropdown.getSelectedValue() );
                        }
                    } else {
                        Object value = configurable.getParameter( key );
                        if ( value == null ) {
                            setParameterStringValue( pipeletParam, toString( pd.getDefaultValue() ) );
                        } else {
                            setParameterStringValue( pipeletParam, toString( value ) );
                        }
                    }
                    result.add( pipeletParam );
                }
            }
        }
        return result;
    }

    /**
     * Gets a <code>List</code> of <code>LoggerParamPojo</code> objects for the given
     * <code>Configurable</code> that represents the current configuration 
     * @param configurable The <code>Configurable</code> to extract the current
     * configuration from.
     * @param loggerPojo The <code>LoggerPojo</code> that shall be set as parent for
     * the resulting <code>LoggerParamPojo</code> objects.
     * @return A <code>List</code> of <code>LoggerParamPojo</code> objects.
     */
    public static List<LoggerParamPojo> getConfiguration( Configurable configurable, LoggerPojo loggerPojo ) {

        List<LoggerParamPojo> result = new ArrayList<LoggerParamPojo>();
        Map<String, ParameterDescriptor> params = configurable.getParameterMap();
        if ( params != null ) {
            int sequenceNum = 0;
            for ( String key : params.keySet() ) {
                ParameterDescriptor pd = params.get( key );
                LoggerParamPojo loggerParam = null;
                if ( loggerPojo.getLoggerParams() != null ) {
                    for ( LoggerParamPojo lp : loggerPojo.getLoggerParams() ) {
                        if ( lp.getParamName() != null && lp.getParamName().equals( key ) ) {
                            loggerParam = lp;
                            break;
                        }
                    }
                }
                if ( loggerParam == null ) {
                    loggerParam = new LoggerParamPojo();
                }
                loggerParam.setParameterDescriptor( pd );
                loggerParam.setCreatedDate( new Date() );
                loggerParam.setModifiedDate( new Date() );
                loggerParam.setLabel( pd.getLabel() );
                loggerParam.setParamName( key );
                loggerParam.setLogger( loggerPojo );
                loggerParam.setSequenceNumber( sequenceNum++ );
                if ( pd.getParameterType() == ParameterType.LIST ) {
                    ListParameter dropdown = configurable.getParameter( key );
                    if ( dropdown != null ) {
                        loggerParam.setValue( dropdown.getSelectedValue() );
                    }
                } else if ( pd.getParameterType() == ParameterType.ENUMERATION ) {
                    // TODO: implement this
                } else {
                    Object value = configurable.getParameter( key );
                    if ( value == null ) {
                        loggerParam.setValue( toString( pd.getDefaultValue() ) );
                    } else {
                        loggerParam.setValue( toString( value ) );
                    }
                }
                result.add( loggerParam );
            }
        }
        return result;
    }

    /**
     * Gets a <code>List</code> of <code>ServiceParamPojo</code> objects for the given
     * <code>Configurable</code> that represents the current configuration 
     * @param configurable The <code>Configurable</code> to extract the current
     * configuration from.
     * @param servicePojo The <code>ServicePojo</code> that shall be set as parent for
     * the resulting <code>ServiceParamPojo</code> objects.
     * @return A <code>List</code> of <code>ServiceParamPojo</code> objects.
     */
    public static List<ServiceParamPojo> getConfiguration( Configurable configurable, ServicePojo servicePojo ) {

        List<ServiceParamPojo> result = new ArrayList<ServiceParamPojo>();
        Map<String, ParameterDescriptor> params = configurable.getParameterMap();
        if ( params != null ) {
            int sequenceNum = 0;
            for ( String key : params.keySet() ) {
                ParameterDescriptor pd = params.get( key );
                ServiceParamPojo serviceParam = null;
                if ( servicePojo.getServiceParams() != null ) {
                    for ( ServiceParamPojo sp : servicePojo.getServiceParams() ) {
                        if ( sp.getParamName() != null && sp.getParamName().equals( key ) ) {
                            serviceParam = sp;
                            break;
                        }
                    }
                }
                if ( serviceParam == null ) {
                    serviceParam = new ServiceParamPojo();
                }
                serviceParam.setParameterDescriptor( pd );
                serviceParam.setCreatedDate( new Date() );
                serviceParam.setModifiedDate( new Date() );
                serviceParam.setLabel( pd.getLabel() );
                serviceParam.setParamName( key );
                serviceParam.setService( servicePojo );
                serviceParam.setSequenceNumber( sequenceNum++ );
                if ( pd.getParameterType() == ParameterType.LIST ) {
                    ListParameter dropdown = configurable.getParameter( key );
                    if ( dropdown != null ) {
                        serviceParam.setValue( dropdown.getSelectedValue() );
                    }
                } else if ( pd.getParameterType() == ParameterType.ENUMERATION ) {
                    // TODO: implement this
                } else {
                    Object value = configurable.getParameter( key );
                    if ( value == null ) {
                        serviceParam.setValue( toString( pd.getDefaultValue() ) );
                    } else {
                        serviceParam.setValue( toString( value ) );
                    }
                }
                result.add( serviceParam );
            }
        }
        return result;
    }
}
