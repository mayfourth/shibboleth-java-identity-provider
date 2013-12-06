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

package net.shibboleth.idp.attribute.resolver.spring.enc;

import net.shibboleth.idp.spring.BaseSpringNamespaceHandler;

// TODO incomplete
/** Namespace handler for the attribute resolver. */
public class AttributeEncoderNamespaceHandler extends BaseSpringNamespaceHandler {

    /** Namespace for this handler. */
    public static final String NAMESPACE = "urn:mace:shibboleth:2.0:attribute:encoder";

    /** {@inheritDoc} */
    public void init() {
        registerBeanDefinitionParser(SAML1StringAttributeEncoderParser.TYPE_NAME,
                new SAML1StringAttributeEncoderParser());

        registerBeanDefinitionParser(SAML1Base64AttributeEncoderParser.TYPE_NAME,
                new SAML1Base64AttributeEncoderParser());

        registerBeanDefinitionParser(SAML1ScopedStringAttributeEncoderParser.TYPE_NAME,
                new SAML1ScopedStringAttributeEncoderParser());

        registerBeanDefinitionParser(SAML1XMLObjectAttributeEncoderParser.TYPE_NAME,
                new SAML1XMLObjectAttributeEncoderParser());

        registerBeanDefinitionParser(SAML1StringNameIdentifierEncoderParser.SCHEMA_TYPE,
                new SAML1StringNameIdentifierEncoderParser());

        registerBeanDefinitionParser(Saml2StringAttributeEncoderParser.TYPE_NAME,
                new Saml2StringAttributeEncoderParser());

        registerBeanDefinitionParser(Saml2ScopedStringAttributeEncoderParser.TYPE_NAME,
                new Saml2ScopedStringAttributeEncoderParser());

        registerBeanDefinitionParser(Saml2Base64AttributeEncoderParser.TYPE_NAME,
                new Saml2Base64AttributeEncoderParser());

        registerBeanDefinitionParser(Saml2XmlObjectAttributeEncoderParser.TYPE_NAME,
                new Saml2XmlObjectAttributeEncoderParser());

        registerBeanDefinitionParser(Saml2StringNameIDEncoderParser.SCHEMA_TYPE,
                new Saml2StringNameIDEncoderParser());

 
    }
}