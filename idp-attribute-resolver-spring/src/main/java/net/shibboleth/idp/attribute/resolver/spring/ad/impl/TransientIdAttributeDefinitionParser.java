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

package net.shibboleth.idp.attribute.resolver.spring.ad.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.idp.attribute.resolver.spring.ad.BaseAttributeDefinitionParser;
import net.shibboleth.idp.attribute.resolver.spring.impl.AttributeResolverNamespaceHandler;
import net.shibboleth.idp.saml.attribute.resolver.impl.TransientIdAttributeDefinition;
import net.shibboleth.idp.saml.nameid.impl.StoredTransientIdGenerationStrategy;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.utilities.java.support.xml.DOMTypeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Spring bean definition parser for {@link TransientIdAttributeDefinition} using a
 * {@link StoredTransientIdGenerationStrategy}.
 */
@SuppressWarnings("deprecation")
public class TransientIdAttributeDefinitionParser extends BaseAttributeDefinitionParser {

    /** Schema type name - ad: (legacy). */
    @Nonnull public static final QName TYPE_NAME_AD =
            new QName(AttributeDefinitionNamespaceHandler.NAMESPACE, "TransientId");

    /** Schema type name. - resolver: */
    @Nonnull public static final QName TYPE_NAME_RESOLVER =
            new QName(AttributeResolverNamespaceHandler.NAMESPACE, "TransientId");

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(TransientIdAttributeDefinitionParser.class);

    /** {@inheritDoc} */
    @Override protected Class<TransientIdAttributeDefinition> getBeanClass(@Nullable final Element element) {
        return TransientIdAttributeDefinition.class;
    }

    /** {@inheritDoc} */
    @Override protected void doParse(@Nonnull final Element config, @Nonnull final ParserContext parserContext,
            @Nonnull final BeanDefinitionBuilder builder) {
        super.doParse(config, parserContext, builder);

        final BeanDefinitionBuilder strategyBuilder =
                BeanDefinitionBuilder.genericBeanDefinition(StoredTransientIdGenerationStrategy.class);

        strategyBuilder.setInitMethodName("initialize");
        strategyBuilder.addPropertyValue("id", "StoredTransientIdGenerationStrategy:" + getDefinitionId());

        if (config.hasAttributeNS(null, "lifetime")) {
            strategyBuilder.addPropertyValue("idLifetime",
                    StringSupport.trimOrNull(config.getAttributeNS(null, "lifetime")));
        }

        String idStore = "shibboleth.StorageService";
        if (config.hasAttributeNS(null, "storageServiceRef")) {
            idStore = StringSupport.trimOrNull(config.getAttributeNS(null, "storageServiceRef"));
        }

        log.debug("{} idStore '{}'", getLogPrefix(), idStore);
        strategyBuilder.addPropertyReference("idStore", idStore);

        builder.addPropertyValue("transientIdGenerationStrategy", strategyBuilder.getBeanDefinition());

        DeprecationSupport.warnOnce(ObjectType.XSITYPE, DOMTypeSupport.getXSIType(config).toString(),
                parserContext.getReaderContext().getResource().getDescription(),
                "via NameID Generation Service configuration");
    }

}