/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.idp.consent.flow.ar;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.shibboleth.idp.attribute.IdPAttributeValue;
import net.shibboleth.idp.consent.flow.ConsentFlowDescriptor;
import net.shibboleth.idp.consent.logic.AttributeValuesHashFunction;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import com.google.common.base.Function;

/**
 * Descriptor for a consent flow.
 */
public class AttributeConsentFlowDescriptor extends ConsentFlowDescriptor {

    /** Whether consent to any attribute and to any relying party is allowed. */
    private boolean allowGlobalConsent;

    /** Whether per-attribute consent is enabled. */
    private boolean enablePerAttributeConsent;

    /** Function to create hash of all attribute values. */
    @Nonnull private Function<Collection<IdPAttributeValue<?>>, String> attributeValuesHashFunction;

    /** Constructor. */
    public AttributeConsentFlowDescriptor() {
        attributeValuesHashFunction = new AttributeValuesHashFunction();
    }

    /**
     * Get whether consent to any attribute and to any relying party is allowed.
     * 
     * @return true iff consent to any attribute and to any relying party is allowed
     */
    public boolean allowGlobalConsent() {
        return allowGlobalConsent;
    }

    /**
     * Get whether per-attribute consent is enabled.
     * 
     * @return true iff per-attribute consent is enabled
     */
    public boolean enablePerAttributeConsent() {
        return enablePerAttributeConsent;
    }

    /**
     * Get the function to create hash of all attribute values.
     * 
     * @return function to create hash of all attribute values
     */
    @Nonnull public Function<Collection<IdPAttributeValue<?>>, String> getAttributeValuesHashFunction() {
        return attributeValuesHashFunction;
    }

    /**
     * Set whether consent to any attribute and to any relying party is allowed.
     * 
     * @param flag true iff consent to any attribute and to any relying party is allowed
     */
    public void setAllowGlobalConsent(boolean flag) {
        allowGlobalConsent = flag;
    }

    /**
     * Set whether per-attribute consent is enabled.
     * 
     * @param flag true iff per-attribute consent is enabled
     */
    public void setEnablePerAttributeConsent(boolean flag) {
        enablePerAttributeConsent = flag;
    }

    /**
     * Set the function to create hash of all attribute values.
     * 
     * @param function function to create hash of all attribute values
     */
    public void setAttributeValuesHashFunction(
            @Nonnull final Function<Collection<IdPAttributeValue<?>>, String> function) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        attributeValuesHashFunction = Constraint.isNotNull(function, "Attribute values hash function cannot be null");
    }

}