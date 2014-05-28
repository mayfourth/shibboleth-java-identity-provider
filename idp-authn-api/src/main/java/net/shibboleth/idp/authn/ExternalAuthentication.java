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

package net.shibboleth.idp.authn;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/** Public interface supporting external authentication outside the webflow engine. */
public class ExternalAuthentication {

    /** Parameter supplied to identify the per-conversation structure in the session. */
    @Nonnull @NotEmpty public static final String CONVERSATION_KEY = "conversation";
    
    /** Request attribute to which user's principal should be bound. */
    @Nonnull @NotEmpty public static final String PRINCIPAL_KEY = "principal";

    /** Request attribute to which user's principal name should be bound. */
    @Nonnull @NotEmpty public static final String PRINCIPAL_NAME_KEY = "principal_name";

    /** Request attribute to which user's subject should be bound. */
    @Nonnull @NotEmpty public static final String SUBJECT_KEY = "subject";

    /** Request attribute to which an authentication timestamp may be bound. */
    @Nonnull @NotEmpty public static final String AUTHENTICATION_INSTANT_KEY = "authnInstant";
    
    /** Request attribute to which an error message may be bound. */
    @Nonnull @NotEmpty public static final String AUTHENTICATION_ERROR_KEY = "authnError";

    /** Request attribute to which an {@link AuthenticationException} may be bound. */
    @Nonnull @NotEmpty public static final String AUTHENTICATION_EXCEPTION_KEY = "authnException";

    /** Request attribute to which a signal not to cache the result may be bound. */
    @Nonnull @NotEmpty public static final String DONOTCACHE_KEY = "doNotCache";
    
    /** Request attribute that indicates whether the authentication request requires forced authentication. */
    @Nonnull @NotEmpty public static final String FORCE_AUTHN_PARAM = "forceAuthn";

    /** Request attribute that indicates whether the authentication requires passive authentication. */
    @Nonnull @NotEmpty public static final String PASSIVE_AUTHN_PARAM = "isPassive";

    /** Request attribute that provides which authentication method should be attempted. */
    @Deprecated @Nonnull @NotEmpty public static final String AUTHN_METHOD_PARAM = "authnMethod";

    /** Request attribute that provides the entity ID of the relying party that is requesting authentication. */
    @Nonnull @NotEmpty public static final String RELYING_PARTY_PARAM = "relyingParty";
    
    /**
     * Initialize a request for external authentication by seeking out the information stored in
     * the servlet session and exposing it as request attributes.
     * 
     * @param request servlet request
     * 
     * @return a handle to subsequent use of
     *      {@link #finishExternalAuthentication(HttpServletRequest, HttpServletResponse)}
     * 
     * @throws ExternalAuthenticationException if an error occurs
     */
    @Nonnull @NotEmpty public static String startExternalAuthentication(@Nonnull final HttpServletRequest request)
            throws ExternalAuthenticationException {
        final String conv = request.getParameter(CONVERSATION_KEY);
        if (conv == null) {
            throw new ExternalAuthenticationException("No conversation key found in request");
        }
        
        final Object obj = request.getSession().getAttribute(CONVERSATION_KEY + conv);
        if (obj == null || !(obj instanceof ExternalAuthentication)) {
            throw new ExternalAuthenticationException("No conversation state found in session");
        }
        
        ((ExternalAuthentication) obj).doStart(request);
        return conv;
    }
    
    /**
     * Complete a request for external authentication by seeking out the information stored in
     * request attributes and transferring to the session's conversation state, and then transfer
     * control back to the authentication web flow.
     * 
     * @param key   the value returned by {@link #startExternalAuthentication(HttpServletRequest)}
     * @param request servlet request
     * @param response servlet response
     * 
     * @throws ExternalAuthenticationException if an error occurs
     * @throws IOException if the redirect cannot be issued
     */
    public static void finishExternalAuthentication(@Nonnull @NotEmpty final String key,
            @Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response)
            throws ExternalAuthenticationException, IOException {
        
        final Object obj = request.getSession().getAttribute(CONVERSATION_KEY + key);
        if (obj == null || !(obj instanceof ExternalAuthentication)) {
            throw new ExternalAuthenticationException("No conversation state found in session");
        }
        
        request.getSession().removeAttribute(CONVERSATION_KEY + key);
        
        ((ExternalAuthentication) obj).doFinish(request, response);
    }

    /**
     * Initialize a request for external authentication by seeking out the information stored in
     * the servlet session and exposing it as request attributes.
     * 
     * @param request servlet request
     * 
     * @throws ExternalAuthenticationException if an error occurs
     */
    protected void doStart(@Nonnull final HttpServletRequest request) throws ExternalAuthenticationException {
        throw new ExternalAuthenticationException("Not implemented");
    }

    /**
     * Complete a request for external authentication by seeking out the information stored in
     * request attributes and transferring to the session's conversation state, and then transfer
     * control back to the authentication web flow.
     * 
     * @param request servlet request
     * @param response servlet response
     * 
     * @throws ExternalAuthenticationException if an error occurs
     * @throws IOException if the redirect cannot be issued
     */
    protected void doFinish(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response)
            throws ExternalAuthenticationException, IOException {
        throw new ExternalAuthenticationException("Not implemented");
    }
    
}