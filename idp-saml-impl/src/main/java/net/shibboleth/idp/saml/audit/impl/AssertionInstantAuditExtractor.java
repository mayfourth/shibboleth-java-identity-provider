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

package net.shibboleth.idp.saml.audit.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.ArtifactResponse;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import net.shibboleth.utilities.java.support.logic.Constraint;

/** {@link Function} that returns the IssueInstant attribute from the assertions in a response. */
public class AssertionInstantAuditExtractor implements Function<ProfileRequestContext,Collection<DateTime>> {

    /** Lookup strategy for message to read from. */
    @Nonnull private final Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;
    
    /**
     * Constructor.
     *
     * @param strategy lookup strategy for message
     */
    public AssertionInstantAuditExtractor(@Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Collection<DateTime> apply(@Nullable final ProfileRequestContext input) {
        SAMLObject response = responseLookupStrategy.apply(input);
        if (response != null) {
            
            // Step down into ArtifactResponses.
            if (response instanceof ArtifactResponse) {
                response = ((ArtifactResponse) response).getMessage();
            }
            
            if (response instanceof org.opensaml.saml.saml2.core.Response) {
                
                final List<org.opensaml.saml.saml2.core.Assertion> assertions =
                        ((org.opensaml.saml.saml2.core.Response) response).getAssertions();
                if (!assertions.isEmpty()) {
                    return Collections2.transform(assertions,
                            new Function<org.opensaml.saml.saml2.core.Assertion,DateTime>() {
                                    public DateTime apply(org.opensaml.saml.saml2.core.Assertion input) {
                                        return input.getIssueInstant();
                                    }
                                });
                }
                
            } else if (response instanceof org.opensaml.saml.saml1.core.Response) {

                final List<org.opensaml.saml.saml1.core.Assertion> assertions =
                        ((org.opensaml.saml.saml1.core.Response) response).getAssertions();
                if (!assertions.isEmpty()) {
                    return Collections2.transform(assertions,
                            new Function<org.opensaml.saml.saml1.core.Assertion,DateTime>() {
                                    public DateTime apply(org.opensaml.saml.saml1.core.Assertion input) {
                                        return input.getIssueInstant();
                                    }
                                });
                }
                
            }
        }
        
        return Collections.emptyList();
    }

}