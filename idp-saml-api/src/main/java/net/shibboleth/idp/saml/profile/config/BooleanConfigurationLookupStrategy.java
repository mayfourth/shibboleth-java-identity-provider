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

package net.shibboleth.idp.saml.profile.config;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A strategy function that examines SAML metadata associated with a relying party and derives Boolean-valued
 * configuration settings based on EntityAttribute extension tags.
 * 
 * @since 3.4.0
 */
public class BooleanConfigurationLookupStrategy extends AbstractMetadataDrivenConfigurationLookupStrategy<Boolean> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BooleanConfigurationLookupStrategy.class);

    /** {@inheritDoc} */
    @Override
    @Nullable protected Boolean doTranslate(@Nonnull final Attribute tag) {
        
        final List<XMLObject> values = tag.getAttributeValues();
        if (values.size() != 1) {
            log.error("Tag '{}' contained multiple values, returning none");
            return null;
        }
        
        log.debug("Converting tag '{}' to Boolean property", tag.getName());
        return xmlObjectToBoolean(values.get(0));
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Convert an XMLObject to a Boolean if the type is supported.
     * 
     * @param object object to convert
     * 
     * @return the converted value, or null
     */
    @Nullable private Boolean xmlObjectToBoolean(@Nonnull final XMLObject object) {
        if (object instanceof XSString) {
            final String value = ((XSString) object).getValue();
            if (value != null) {
                return XSBooleanValue.valueOf(value).getValue();
            } else {
                return null;
            }
        } else if (object instanceof XSBoolean) {
            final XSBooleanValue value = ((XSBoolean) object).getValue();
            return value != null ? value.getValue() : null;
        } else if (object instanceof XSInteger) {
            final Integer value = ((XSInteger) object).getValue();
            return value != null ? value != 0 : null;
        } else if (object instanceof XSAny) {
            final XSAny wc = (XSAny) object;
            if (wc.getUnknownAttributes().isEmpty() && wc.getUnknownXMLObjects().isEmpty()) {
                final String value = wc.getTextContent();
                if (value != null) {
                    return XSBooleanValue.valueOf(value).getValue();
                } else {
                    return null;
                }
            }
        }
        
        log.error("Unsupported conversion to Boolean from XMLObject type ({})", object.getClass().getName());
        return null;
    }
// Checkstyle: CyclomaticComplexity ON
    
}