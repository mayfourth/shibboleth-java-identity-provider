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

package net.shibboleth.idp.saml.impl.attribute.encoding;

import java.util.Collection;

import net.shibboleth.idp.attribute.Attribute;
import net.shibboleth.idp.attribute.AttributeEncodingException;
import net.shibboleth.idp.saml.attribute.encoding.AbstractSaml1NameIdentifierEncoder;

import org.opensaml.saml1.core.NameIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link net.shibboleth.idp.attribute.AttributeEncoder} that produces the SAML 1 NameIdentifier used for the Subject
 * from the first non-null {@link NameIdentifier} value of an {@link net.shibboleth.idp.attribute.Attribute}.
 */
public class Saml1XmlObjectSubjectNameIdentifierEncoder extends AbstractSaml1NameIdentifierEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(Saml1XmlObjectSubjectNameIdentifierEncoder.class);

    /** {@inheritDoc} */
    public NameIdentifier encode(Attribute attribute) throws AttributeEncodingException {
        final String attributeId = attribute.getId();

        final Collection<?> attributeValues = attribute.getValues();
        if (attributeValues == null || attributeValues.isEmpty()) {
            log.debug("Attribute {} contains no value, nothing to encode", attributeId);
            return null;
        }

        for (Object value : attributeValues) {
            if (value == null) {
                log.debug("Skipping null value of attribute {}", attributeId);
                continue;
            }

            if (value instanceof NameIdentifier) {
                NameIdentifier identifier = (NameIdentifier) value;
                log.debug(
                        "Chose NameIdentifier, with value {}, of attribute {} for subject name identifier encoding",
                        identifier.getNameIdentifier(), attributeId);
                return identifier;
            } else {
                log.debug("Skipping value of type {} of attribute {}", value.getClass().getName(), attributeId);
                continue;
            }
        }

        log.debug(
                "Attribute {} did not contain any NameIdentifier values, nothing to encode as subject name identifier",
                attributeId);
        return null;
    }
}