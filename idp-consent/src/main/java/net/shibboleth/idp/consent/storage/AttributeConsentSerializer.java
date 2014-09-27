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

package net.shibboleth.idp.consent.storage;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import net.shibboleth.idp.consent.AttributeConsent;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;

import org.joda.time.DateTime;
import org.opensaml.storage.StorageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AttributeConsentSerializer extends AbstractInitializableComponent implements
        StorageSerializer<Map<String, AttributeConsent>> {

    /** Field name of attribute identifier. */
    @Nonnull @NotEmpty private static final String ATTRIBUTE_ID_FIELD = "a";

    /** Field name of attribute values hash. */
    @Nonnull @NotEmpty private static final String ATTRIBUTE_VALUES_HASH_FIELD = "h";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AttributeConsentSerializer.class);

    /** JSON generator factory. */
    @Nonnull private final JsonGeneratorFactory generatorFactory;

    /** JSON reader factory. */
    @Nonnull private final JsonReaderFactory readerFactory;

    /** Constructor. */
    public AttributeConsentSerializer() {
        generatorFactory = Json.createGeneratorFactory(null);
        readerFactory = Json.createReaderFactory(null);
    }

    /** {@inheritDoc} */
    @Nonnull public Map<String, AttributeConsent> deserialize(final int version,
            @Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable final Long expiration) throws IOException {

        try (final JsonReader reader = readerFactory.createReader(new StringReader(value))) {
            final JsonStructure st = reader.read();

            if (!(st instanceof JsonArray)) {
                throw new IOException("Found invalid data structure while parsing AttributeConsent");
            }
            final JsonArray array = (JsonArray) st;

            final Map<String, AttributeConsent> consents = new LinkedHashMap<>();
            
            for (final JsonValue a : array) {
                if (a.getValueType().equals(ValueType.OBJECT)) {
                    final JsonObject o = (JsonObject) a;

                    final AttributeConsent consent = new AttributeConsent();
                    consent.setAttributeId(o.getString(ATTRIBUTE_ID_FIELD));
                    consent.setValuesHash(o.getString(ATTRIBUTE_VALUES_HASH_FIELD));
                    if (expiration != null) {
                        consent.setExpiration(new DateTime(expiration));
                    }
                    consents.put(consent.getAttributeId(), consent);
                }
            }

            log.debug("Deserialized context '{}' key '{}' value '{}' expiration '{}' as '{}", new Object[] {context,
                    key, value, expiration, consents,});
            return consents;
        } catch (final NullPointerException | ClassCastException | ArithmeticException | JsonException e) {
            log.error("Exception while parsing AttributeConsent", e);
            throw new IOException("Found invalid data structure while parsing AttributeConsent", e);
        }
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String serialize(@Nonnull final Map<String, AttributeConsent> consents)
            throws IOException {

        // TODO what should be returned in this case ?
        if (consents.isEmpty()) {
            return "";
        }

        final StringWriter sink = new StringWriter(128);
        final JsonGenerator gen = generatorFactory.createGenerator(sink);

        gen.writeStartArray();
        for (AttributeConsent consent : consents.values()) {
            gen.writeStartObject();
            gen.write(ATTRIBUTE_ID_FIELD, consent.getAttributeId());
            gen.write(ATTRIBUTE_VALUES_HASH_FIELD, consent.getValuesHash());
            gen.writeEnd();
        }
        gen.writeEnd();
        gen.close();

        final String serialized = sink.toString();
        log.debug("Serialized '{}' as '{}", consents, serialized);
        return serialized;
    }

}