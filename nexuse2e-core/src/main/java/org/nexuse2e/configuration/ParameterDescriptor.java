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

import java.io.Serializable;

/**
 * Instances of this class describe generic parameters as they
 * are supported by <code>Configurable</code>s.
 * 
 * @author jonas.reese
 */
public class ParameterDescriptor implements Serializable {

    private static final long serialVersionUID = 125531871459686485L;

    private ParameterType     parameterType;
    private String            label;
    private boolean           required;
    private String            description;
    private Object            defaultValue;
    
    private Runnable          updater;

    /**
     * Constructs a new <code>ParameterDescriptor</code>.
     * @param parameterType The described parameter's type.
     * @param required If <code>true</code>, the parameter is
     * required, otherwise it is optional.
     * @param label A human-readable short label.
     * @param description A human-readable description.
     * @param defaultValue The default value for the parameter.
     * This shall be an object of <code>parameterType.getType()</code>.
     * @throws ClassCastException if the <code>defaultValue</code>
     * type does not match the parameter type.
     */
    public ParameterDescriptor( ParameterType parameterType, boolean required, String label, String description,
            Object defaultValue ) {

        this.parameterType = parameterType;
        this.required = required;
        this.label = label;
        this.description = description;
        this.defaultValue = defaultValue;
        this.updater = null;
        if ( defaultValue != null && parameterType != ParameterType.SERVICE
                && !parameterType.getType().isInstance( defaultValue ) ) {
            throw new ClassCastException( "defaultValue for parameter: " + label + ", type " + parameterType
                    + " must be instanceof " + parameterType.getType() );
        }
    }

    /**
     * Constructs a new <code>ParameterDescriptor</code> for
     * a required (non-optional) parameter.
     * @param parameterType The described parameter's type.
     * required, otherwise it is optional.
     * @param label A human-readable short label.
     * @param description A human-readable description.
     * @param defaultValue The default value for the parameter.
     * @throws ClassCastException if the 
     */
    public ParameterDescriptor( ParameterType parameterType, String label, String description, Object defaultValue ) {

        this( parameterType, true, label, description, defaultValue );
    }
    
    /**
     * Sets a <code>Runnable</code> object that will be invoked when
     * the {@link #getDefaultValue()} method is called. This mechanism
     * can be used to update the data structures certain parameter types,
     * e.g. {@link ParameterType#LIST} directly before they are retrieved
     * by a caller.
     * @param updater The <code>Runnable</code> that shall be invoked,
     * or <code>null</code> if no invocation shall be performed.
     */
    public void setUpdater( Runnable updater ) {
        this.updater = updater;
    }

    /**
     * Gets the parameter's default value.
     * @param <T> The cast return value.
     * @return the defaultValue The default value. It can be cast
     * to <code>getParameterType().getType()</code>.
     * @throws ClassCastException if the return type does not match.
     */
    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue() {

        if (this.updater != null) {
            Runnable updater = this.updater;
            this.updater = null;
            updater.run();
            this.updater = updater;
        }
        return (T) defaultValue;
    }

    /**
     * Gets the description.
     * @return A human-readable description.
     */
    public String getDescription() {

        return description;
    }

    /**
     * Gets the label.
     * @return A human-readable short label. 
     */
    public String getLabel() {

        return label;
    }

    /**
     * Gets the parameter type
     * @return The parameter type.
     */
    public ParameterType getParameterType() {

        return parameterType;
    }

    /**
     * Gets the <code>required</code> flag.
     * @return <code>true</code> if the parameter is required,
     * <code>false</code> if it is optional.
     */
    public boolean isRequired() {

        return required;
    }

}
