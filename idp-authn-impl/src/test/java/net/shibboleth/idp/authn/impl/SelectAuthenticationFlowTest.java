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

package net.shibboleth.idp.authn.impl;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.Subject;

import net.shibboleth.idp.authn.AuthenticationResult;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.RequestedPrincipalContext;
import net.shibboleth.idp.authn.principal.TestPrincipal;
import net.shibboleth.idp.authn.principal.impl.ExactPrincipalEvalPredicateFactory;
import net.shibboleth.idp.profile.ActionTestingSupport;

import org.opensaml.profile.ProfileException;
import org.springframework.webflow.execution.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/** {@link SelectAuthenticationFlow} unit test. */
public class SelectAuthenticationFlowTest extends PopulateAuthenticationContextTest {
    
    private SelectAuthenticationFlow action; 
    
    @BeforeMethod public void setUp() throws Exception {
        super.setUp();
     
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        authCtx.getPrincipalEvalPredicateFactoryRegistry().register(
                TestPrincipal.class, "exact", new ExactPrincipalEvalPredicateFactory());
        
        action = new SelectAuthenticationFlow();
        action.setId("test");
        action.initialize();
    }
    
    @Test public void testNoRequestNoneActive() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        
        final Event event = action.execute(src);
        
        Assert.assertNull(authCtx.getAuthenticationResult());
        Assert.assertEquals(authCtx.getAttemptedFlow(), authCtx.getPotentialFlows().get(event.getId()));
        Assert.assertEquals(authCtx.getAttemptedFlow().getId(), "test1");
    }

    @Test public void testNoRequestNoneActiveIntermediate() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        authCtx.getIntermediateFlows().put("test1", authCtx.getPotentialFlows().get("test1"));
        
        final Event event = action.execute(src);
        
        Assert.assertNull(authCtx.getAuthenticationResult());
        Assert.assertEquals(authCtx.getAttemptedFlow(), authCtx.getPotentialFlows().get(event.getId()));
        Assert.assertEquals(authCtx.getAttemptedFlow().getId(), "test2");
    }
    
    @Test public void testNoRequestActive() throws ProfileException {
        final AuthenticationResult active = new AuthenticationResult("test2", new Subject());
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        authCtx.setActiveResults(Arrays.asList(active));
        
        final Event event = action.execute(src);
        
        ActionTestingSupport.assertProceedEvent(event);
        Assert.assertEquals(active, authCtx.getAuthenticationResult());
    }

    @Test public void testNoRequestForced() throws ProfileException {
        final AuthenticationResult active = new AuthenticationResult("test2", new Subject());
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        authCtx.setActiveResults(Arrays.asList(active));
        authCtx.setForceAuthn(true);
        
        final Event event = action.execute(src);
        
        Assert.assertNull(authCtx.getAuthenticationResult());
        Assert.assertEquals(authCtx.getAttemptedFlow(), authCtx.getPotentialFlows().get(event.getId()));
    }

    @Test public void testRequestNoMatch() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        final RequestedPrincipalContext rpc = new RequestedPrincipalContext();
        rpc.setOperator("exact");
        rpc.setRequestedPrincipals(Arrays.<Principal>asList(new TestPrincipal("foo")));
        authCtx.addSubcontext(rpc, true);
        
        final Event event = action.execute(src);
        
        ActionTestingSupport.assertEvent(event, AuthnEventIds.REQUEST_UNSUPPORTED);
        Assert.assertNull(rpc.getMatchingPrincipal());
    }

    @Test public void testRequestNoneActive() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        final List<Principal> principals = Arrays.<Principal>asList(new TestPrincipal("test3"));
        final RequestedPrincipalContext rpc = new RequestedPrincipalContext();
        rpc.setOperator("exact");
        rpc.setRequestedPrincipals(principals);
        authCtx.addSubcontext(rpc, true);
        authCtx.getPotentialFlows().get("test3").setSupportedPrincipals(principals);
        
        action.execute(src);
        
        Assert.assertNull(authCtx.getAuthenticationResult());
        Assert.assertEquals(authCtx.getAttemptedFlow().getId(), "test3");
        Assert.assertEquals(rpc.getMatchingPrincipal().getName(), "test3");
    }

    @Test public void testRequestNoneActiveIntermediate() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        authCtx.getIntermediateFlows().put("test2", authCtx.getPotentialFlows().get("test2"));
        final List<Principal> principals = Arrays.<Principal>asList(new TestPrincipal("test3"),
                new TestPrincipal("test2"));
        final RequestedPrincipalContext rpc = new RequestedPrincipalContext();
        rpc.setOperator("exact");
        rpc.setRequestedPrincipals(principals);
        authCtx.addSubcontext(rpc, true);
        authCtx.getPotentialFlows().get("test2").setSupportedPrincipals(principals);
        authCtx.getPotentialFlows().get("test3").setSupportedPrincipals(principals);
        
        action.execute(src);
        
        Assert.assertNull(authCtx.getAuthenticationResult());
        Assert.assertEquals(authCtx.getAttemptedFlow().getId(), "test3");
        Assert.assertEquals(rpc.getMatchingPrincipal().getName(), "test3");
    }
    
    @Test public void testRequestPickInactive() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        final List<Principal> principals = Arrays.<Principal>asList(new TestPrincipal("test3"),
                new TestPrincipal("test2"));
        final RequestedPrincipalContext rpc = new RequestedPrincipalContext();
        rpc.setOperator("exact");
        rpc.setRequestedPrincipals(principals);
        authCtx.addSubcontext(rpc, true);
        final AuthenticationResult active = new AuthenticationResult("test2", new Subject());
        active.getSubject().getPrincipals().add(new TestPrincipal("test2"));
        authCtx.setActiveResults(Arrays.asList(active));
        authCtx.getPotentialFlows().get("test3").setSupportedPrincipals(ImmutableList.of(principals.get(0)));
        
        action.execute(src);
        
        Assert.assertNull(authCtx.getAuthenticationResult());
        Assert.assertEquals(authCtx.getAttemptedFlow(), authCtx.getPotentialFlows().get("test3"));
        Assert.assertEquals(rpc.getMatchingPrincipal().getName(), "test3");
    }

    @Test public void testRequestPickActive() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        final List<Principal> principals = Arrays.<Principal>asList(new TestPrincipal("test3"),
                new TestPrincipal("test2"));
        final RequestedPrincipalContext rpc = new RequestedPrincipalContext();
        rpc.setOperator("exact");
        rpc.setRequestedPrincipals(principals);
        authCtx.addSubcontext(rpc, true);
        final AuthenticationResult active = new AuthenticationResult("test3", new Subject());
        active.getSubject().getPrincipals().add(new TestPrincipal("test3"));
        authCtx.setActiveResults(Arrays.asList(active));
        authCtx.getPotentialFlows().get("test3").setSupportedPrincipals(ImmutableList.of(principals.get(0)));
        
        final Event event = action.execute(src);
        
        ActionTestingSupport.assertProceedEvent(event);
        Assert.assertEquals(active, authCtx.getAuthenticationResult());
        Assert.assertEquals(rpc.getMatchingPrincipal().getName(), "test3");
    }

    @Test public void testRequestFavorSSO() throws ProfileException {
        final AuthenticationContext authCtx = prc.getSubcontext(AuthenticationContext.class, false);
        final List<Principal> principals = Arrays.<Principal>asList(new TestPrincipal("test3"),
                new TestPrincipal("test2"));
        final RequestedPrincipalContext rpc = new RequestedPrincipalContext();
        rpc.setOperator("exact");
        rpc.setRequestedPrincipals(principals);
        authCtx.addSubcontext(rpc, true);
        final AuthenticationResult active = new AuthenticationResult("test2", new Subject());
        active.getSubject().getPrincipals().add(new TestPrincipal("test2"));
        authCtx.setActiveResults(Arrays.asList(active));
        authCtx.getPotentialFlows().get("test3").setSupportedPrincipals(ImmutableList.of(principals.get(0)));
        
        action.setFavorSSO(true);
        final Event event = action.execute(src);
        
        ActionTestingSupport.assertProceedEvent(event);
        Assert.assertEquals(active, authCtx.getAuthenticationResult());
        Assert.assertEquals(rpc.getMatchingPrincipal().getName(), "test2");
    }
}