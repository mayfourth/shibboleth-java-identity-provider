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

package net.shibboleth.idp.attribute.filter.impl.policyrule.saml;

import javax.annotation.Nullable;

import net.shibboleth.idp.attribute.filter.context.AttributeFilterContext;
import net.shibboleth.idp.attribute.filter.impl.policyrule.filtercontext.NavigationSupport;
import net.shibboleth.idp.attribute.resolver.context.AttributeRecipientContext;
import net.shibboleth.idp.attribute.resolver.context.AttributeResolutionContext;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A matcher that evaluates to true if attribute requester matches the provided entity group name.
 */
public class AttributeRequesterInEntityGroupPolicyRule extends AbstractEntityGroupPolicyRule {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AttributeRequesterInEntityGroupPolicyRule.class);

    /** {@inheritDoc} */
    @Nullable protected EntityDescriptor getEntityMetadata(final AttributeFilterContext filterContext) {
        final AttributeResolutionContext resolver = NavigationSupport.locateResolverContext(filterContext);
        if (null == resolver) {
            log.warn("{} Could not locate resolver context", getLogPrefix());
            return null;
        }

        final AttributeRecipientContext recipient = NavigationSupport.locateRecipientContext(resolver);

        if (null == recipient) {
            log.warn("{} Could not locate recipient context", getLogPrefix());
            return null;
        }
        final EntityDescriptor result = recipient.getAttributeRecipientMetadata();
        if (null == result) {
            log.warn("{} No Attribute Requester found", getLogPrefix());
        }
        return result;    }
}
