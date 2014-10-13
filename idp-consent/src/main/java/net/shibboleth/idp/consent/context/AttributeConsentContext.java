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

package net.shibboleth.idp.consent.context;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.BaseContext;

import com.google.common.collect.MapConstraints;

/**
 * Context for attribute consent. Holds the attributes for which consent is obtained.
 */
public class AttributeConsentContext extends BaseContext {

    /** Attributes to be consented to. */
    @Nonnull @NonnullElements private Map<String, IdPAttribute> consentableAttributes;

    /** Constructor. */
    public AttributeConsentContext() {
        consentableAttributes = Collections.emptyMap();
    }

    /**
     * Get the attributes to be consented to.
     * 
     * @return the attributes to be consented to
     */
    @Nonnull @NonnullElements public Map<String, IdPAttribute> getConsentableAttributes() {
        return consentableAttributes;
    }

    /**
     * Set the attributes to be consented to.
     * 
     * @param attributes the attributes to be consented to
     */
    public void setConsentableAttributes(@Nonnull @NonnullElements final Map<String, IdPAttribute> attributes) {
        Constraint.isNotNull(attributes, "Consentable attributes cannot be null");

        consentableAttributes = MapConstraints.constrainedMap(attributes, MapConstraints.notNull());
    }
}