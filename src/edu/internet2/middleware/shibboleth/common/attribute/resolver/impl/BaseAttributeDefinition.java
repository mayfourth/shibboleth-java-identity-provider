/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.attribute.resolver.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.internet2.middleware.shibboleth.common.attribute.Attribute;
import edu.internet2.middleware.shibboleth.common.attribute.AttributeEncoder;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeDefinition;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.DataConnector;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.ResolutionContext;

/**
 * Base class for {@link AttributeDefinition} plug-ins.
 */
public abstract class BaseAttributeDefinition extends AbstractResolutionPlugIn<Attribute> implements
        AttributeDefinition {

    /** Whether this attribute definition is only a dependency and thus its values should never be released. */
    private boolean dependencyOnly;

    /** Attribute encoders associated with this definition. */
    private Map<String, AttributeEncoder> encoders;

    /**
     * Constructor.
     */
    public BaseAttributeDefinition() {
        dependencyOnly = false;
        encoders = new HashMap<String, AttributeEncoder>();
    }

    /** {@inheritDoc} */
    public boolean isDependencyOnly() {
        return dependencyOnly;
    }

    /**
     * Sets whether this attribute definition is only a dependency and thus its values should never be released outside
     * the resolver.
     * 
     * @param isDependencyOnly whether this attribute definition is only a dependency
     */
    public void setDependencyOnly(boolean isDependencyOnly) {
        dependencyOnly = isDependencyOnly;
    }

    /** {@inheritDoc} */
    public Map<String, AttributeEncoder> getAttributeEncoders() {
        return encoders;
    }

    /**
     * Get values from dependencies.
     * 
     * @param context resolution context
     * @return collection of values
     */
    protected Collection<Object> getValuesFromAllDependencies(ResolutionContext context) {
        Set<Object> values = new HashSet<Object>();

        if (!getAttributeDefinitionDependencyIds().isEmpty()) {
            values.addAll(getValuesFromAttributeDependencies(context));
        }

        if (!getDataConnectorDependencyIds().isEmpty()) {
            values.addAll(getValuesFromConnectorDependencies(context));
        }

        return values;
    }

    /**
     * Get values from attribute dependencies.
     * 
     * @param context resolution context
     * @return collection of values
     */
    protected Collection<Object> getValuesFromAttributeDependencies(ResolutionContext context) {
        Set<Object> values = new HashSet<Object>();

        for (String id : getAttributeDefinitionDependencyIds()) {
            AttributeDefinition definition = context.getResolvedAttributeDefinitions().get(id);
            if (definition != null) {
                try {
                    Attribute attribute = definition.resolve(context);
                    for (Object o : attribute.getValues()) {
                        values.add(o);
                    }
                } catch (AttributeResolutionException e) {
                    // TODO Auto-generated catch block
                }
            }
        }

        return values;
    }

    /**
     * Get values from data connectors.
     * 
     * @param context resolution context
     * @return collection of values
     */
    protected Collection<Object> getValuesFromConnectorDependencies(ResolutionContext context) {
        Set<Object> values = new HashSet<Object>();

        for (String connectorId : getDataConnectorDependencyIds()) {
            DataConnector connector = context.getResolvedDataConnectors().get(connectorId);
            if (connector != null) {
                try {
                    Map<String, Attribute> attributes = connector.resolve(context);
                    for (String attributeId : attributes.keySet()) {
                        if (attributeId != null && attributeId.equals(this.getId())) {
                            // TODO do we need any kind of connector mapping like in previous versions?
                            for (Object o : attributes.get(attributeId).getValues()) {
                                values.add(o);
                            }
                        }
                    }
                } catch (AttributeResolutionException e) {
                    // TODO Auto-generated catch block
                }
            }
        }

        return values;
    }

}