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

package net.shibboleth.idp.relyingparty.impl;

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.idp.profile.config.SecurityConfiguration;
import net.shibboleth.idp.profile.context.RelyingPartyContext;
import net.shibboleth.idp.relyingparty.CriteriaRelyingPartyConfigurationResolver;
import net.shibboleth.idp.relyingparty.RelyingPartyConfiguration;
import net.shibboleth.idp.relyingparty.RelyingPartyConfigurationResolver;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractIdentifiedInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.component.IdentifiableComponent;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * An implementation of {@link CriteriaRelyingPartyConfigurationResolver} which delegates to an instance of
 * {@link RelyingPartyConfigurationResolver}.
 * 
 * <p>
 * One of the following input criteria is required for resolution based on relying party entityID:
 * <ul>
 * <li>{@link EntityIdCriterion}</li>
 * <li>{@link RoleDescriptorCriterion}</li>
 * </ul>
 * </p>
 */
public class DelegatingCriteriaRelyingPartyConfigurationResolver extends AbstractIdentifiedInitializableComponent 
        implements CriteriaRelyingPartyConfigurationResolver, IdentifiableComponent {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(DelegatingCriteriaRelyingPartyConfigurationResolver.class);
    
    /** The RelyingPartyConfigurationResolver to which to delegate. */
    @NonnullAfterInit private RelyingPartyConfigurationResolver delegate;
    
    /** Constructor. */
    public DelegatingCriteriaRelyingPartyConfigurationResolver() {
        super();
    }

    /**
     * Set the {@link RelyingPartyConfigurationResolver} instance to which to delegate.
     * 
     * @param resolver the resolver delegate instance
     */
    public void setDelegate(@Nullable final RelyingPartyConfigurationResolver resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        delegate = resolver;
    }

    /** {@inheritDoc} */
    @Override public void setId(@Nonnull final String componentId) {
        super.setId(componentId);
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (delegate == null) {
            throw new ComponentInitializationException("RelyingPartyConfigurationResolver delegate was null");
        }
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        delegate = null;
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public SecurityConfiguration getDefaultSecurityConfiguration(@Nonnull @NotEmpty final String profileId) {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        return delegate.getDefaultSecurityConfiguration(profileId);
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public RelyingPartyConfiguration resolveSingle(@Nullable final CriteriaSet criteria) 
            throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        final Iterator<RelyingPartyConfiguration> results = resolve(criteria).iterator();
        if (results.hasNext()) {
            return results.next();
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements public Iterable<RelyingPartyConfiguration> resolve(@Nullable final CriteriaSet criteria) 
            throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        final ProfileRequestContext prc = buildContext(criteria);
        if (prc != null) {
            return delegate.resolve(prc);
        } else {
            return Collections.emptyList();
        }
        
    }

    /**
     * Build and populate the synthetic instance of {@link ProfileRequestContext} which will be passed
     * in the resolution call to the delegate.
     * 
     * @param criteria the input criteria
     * @return the synthetic context instance, or null if required data is not supplied
     */
    @Nullable private ProfileRequestContext buildContext(@Nullable final CriteriaSet criteria) {
        if (criteria == null) {
            return null;
        }

        final String entityID = resolveEntityID(criteria);
        log.debug("Resolved effective entityID from criteria: {}", entityID);

        final EntityDescriptor entityDescriptor = resolveEntityDescriptor(criteria);
        log.debug("Resolved effective entity descriptor from criteria: {}", entityDescriptor);

        final RoleDescriptor roleDescriptor = resolveRoleDescriptor(criteria);
        log.debug("Resolved effective role descriptor from criteria: {}", roleDescriptor);

        if (entityID != null || entityDescriptor != null || roleDescriptor != null) {
            final ProfileRequestContext prc = new ProfileRequestContext<>();
            final RelyingPartyContext rpc = prc.getSubcontext(RelyingPartyContext.class, true);
            rpc.setVerified(true);

            rpc.setRelyingPartyId(entityID);

            if (entityDescriptor != null || roleDescriptor != null) {
                final SAMLPeerEntityContext peerContext = prc.getSubcontext(SAMLPeerEntityContext.class, true);
                rpc.setRelyingPartyIdContextTree(peerContext);

                peerContext.setEntityId(entityID);

                if (roleDescriptor != null) {
                    peerContext.setRole(roleDescriptor.getSchemaType() != null
                            ? roleDescriptor.getSchemaType() : roleDescriptor.getElementQName());
                }

                final SAMLMetadataContext metadataContext = peerContext.getSubcontext(SAMLMetadataContext.class, true);
                metadataContext.setEntityDescriptor(entityDescriptor);
                metadataContext.setRoleDescriptor(roleDescriptor);
            }
            return prc;
        } else {
            return null;
        }
    }

    /**
     * Resolve the entityID from the criteria.
     * 
     * @param criteria the input criteria
     * @return the input entityID criterion or null if could not be resolved
     */
    private String resolveEntityID(@Nonnull final CriteriaSet criteria) {
        if (criteria.contains(EntityIdCriterion.class)) {
            return criteria.get(EntityIdCriterion.class).getEntityId();
        }

        final EntityDescriptor ed = resolveEntityDescriptor(criteria);
        if (ed != null) {
            return ed.getEntityID();
        }

        return null;
    }

    /**
     * Resolve the EntityDescriptor from the criteria.
     *
     * @param criteria the input criteria
     * @return the input entity descriptor criterion, or null if could not be resolved
     */
    private EntityDescriptor resolveEntityDescriptor(@Nonnull final CriteriaSet criteria) {
        final RoleDescriptor rd = resolveRoleDescriptor(criteria);
        if (rd != null && rd.getParent() != null && rd.getParent() instanceof EntityDescriptor) {
            return (EntityDescriptor)rd.getParent();
        }

        return null;
    }

    /**
     * Resolve the RoleDescriptor from the criteria.
     *
     * @param criteria the input criteria
     * @return the input role descriptor criterion or null if could not be resolved
     */
    private RoleDescriptor resolveRoleDescriptor(@Nonnull final CriteriaSet criteria) {
        if (criteria.contains(RoleDescriptorCriterion.class)) {
            return criteria.get(RoleDescriptorCriterion.class).getRole();
        }

        return null;
    }
    
}
