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

package net.shibboleth.idp.profile.spring.relyingparty.metadata.filter.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.ext.spring.util.SpringSupport;
import net.shibboleth.idp.profile.spring.relyingparty.metadata.AbstractMetadataProviderParser;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.opensaml.saml.metadata.resolver.filter.MetadataFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parser for a &lt;ChainingFilter&gt; filter.
 * 
 * @deprecated
 */
public class ChainingParser extends AbstractSingleBeanDefinitionParser {

    /** Element name. */
    @Nonnull public static final QName TYPE_NAME =
            new QName(AbstractMetadataProviderParser.METADATA_NAMESPACE, "ChainingFilter");

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ChainingParser.class);

    /** {@inheritDoc} */
    @Override protected Class<?> getBeanClass(final Element element) {
        return MetadataFilterChain.class;
    }

    /** {@inheritDoc} */
    @Override protected void doParse(final Element element, final ParserContext parserContext,
            final BeanDefinitionBuilder builder) {
     
        DeprecationSupport.warn(ObjectType.XSITYPE, TYPE_NAME.toString(),
                parserContext.getReaderContext().getResource().getDescription(), null);
        
        final List<Element> children =
                ElementSupport.getChildElements(element, AbstractMetadataProviderParser.METADATA_FILTER_ELEMENT_NAME);

        if (null != children && !children.isEmpty()) {
            builder.addPropertyValue("filters", SpringSupport.parseCustomElements(children, parserContext));
        }
    }

    /** {@inheritDoc} */
    @Override protected boolean shouldGenerateId() {
        return true;
    }
    
}