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

package net.shibboleth.idp.attribute.resolver.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;

import com.google.common.base.Function;

/**
 * A function that returns {@link AttributeResolutionContext#getAttributeRecipientID()} if available from a
 * {@link AttributeResolutionContext} obtained via a lookup function, by default a child of the
 * {@link ProfileRequestContext}.
 * 
 * <p>
 * If a specific setting is unavailable, a null value is returned.
 * </p>
 */
public class AttributeRecipientIdLookupFunction implements Function<ProfileRequestContext, String> {

    /**
     * Strategy used to locate the {@link AttributeResolutionContext} associated with a given
     * {@link ProfileRequestContext}.
     */
    @Nonnull private Function<ProfileRequestContext, AttributeResolutionContext> 
        attributeResolutionContextLookupStrategy;

    /** Constructor. */
    public AttributeRecipientIdLookupFunction() {
        attributeResolutionContextLookupStrategy =
                new ChildContextLookup<ProfileRequestContext, AttributeResolutionContext>(
                        AttributeResolutionContext.class, false);
    }

    /**
     * Sets the strategy used to locate the {@link AttributeResolutionContext} associated with a given
     * {@link ProfileRequestContext}.
     * 
     * @param strategy strategy used to locate the {@link AttributeResolutionContext} associated with a given
     *            {@link ProfileRequestContext}
     */
    public synchronized void setAttributeResolutionContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, AttributeResolutionContext> strategy) {
        attributeResolutionContextLookupStrategy =
                Constraint.isNotNull(strategy, "RelyingPartyContext lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override @Nullable public String apply(@Nullable final ProfileRequestContext input) {
        if (input != null) {
            final AttributeResolutionContext arc = attributeResolutionContextLookupStrategy.apply(input);
            if (arc != null) {
                return arc.getAttributeRecipientID();
            }
        }

        return null;
    }

}