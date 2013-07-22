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

package net.shibboleth.idp.authn.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import net.shibboleth.idp.authn.AuthenticationFlowDescriptor;

import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link AuthenticationContext} unit test. */
public class AuthenticationContextTest {

    /** Tests initiation instant instantiation. */
    @Test public void testInitiationInstant() throws Exception {
        long start = System.currentTimeMillis();
        // this is here to allow the event's creation time to deviate from the 'start' time
        Thread.sleep(50);

        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertTrue(ctx.getInitiationInstant() > start);
    }

    /** Tests mutating forcing authentication. */
    @Test public void testForcingAuthentication() throws Exception {
        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertFalse(ctx.isForceAuthn());

        ctx.setForceAuthn(true);
        Assert.assertTrue(ctx.isForceAuthn());
    }

    /** Tests active workflows. */
    @Test public void testActiveWorkFlows() throws Exception {
        final AuthenticationFlowDescriptor descriptor = new AuthenticationFlowDescriptor("test");

        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertTrue(ctx.getActiveFlows().isEmpty());
        
        ctx.setActiveFlows(Arrays.asList(descriptor));

        Assert.assertEquals(ctx.getActiveFlows().size(), 1);
        Assert.assertEquals(ctx.getActiveFlows().get("test"), descriptor);
    }
    
    /** Tests potential workflow instantiation. */
    @Test public void testPotentialWorkflows() throws Exception {
        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertTrue(ctx.getPotentialFlows().isEmpty());

        ctx = new AuthenticationContext(Collections.EMPTY_LIST);
        Assert.assertTrue(ctx.getPotentialFlows().isEmpty());

        AuthenticationFlowDescriptor descriptor = new AuthenticationFlowDescriptor("test");
        ctx = new AuthenticationContext(Arrays.asList(descriptor));
        Assert.assertEquals(ctx.getPotentialFlows().size(), 1);
        Assert.assertEquals(ctx.getPotentialFlows().get("test"), descriptor);
    }

    /** Tests mutating requested workflows. */
    @Test public void testRequestedWorkflows() throws Exception {
        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertTrue(ctx.getRequestedFlows().isEmpty());

        ctx.setRequestedFlows(Collections.EMPTY_LIST);
        Assert.assertTrue(ctx.getRequestedFlows().isEmpty());

        AuthenticationFlowDescriptor descriptor1 = new AuthenticationFlowDescriptor("test1");
        AuthenticationFlowDescriptor descriptor2 = new AuthenticationFlowDescriptor("test2");

        ctx.setRequestedFlows(Arrays.asList(descriptor1, descriptor2));

        Iterator<AuthenticationFlowDescriptor> iterator = ctx.getRequestedFlows().values().iterator();
        Assert.assertEquals(ctx.getRequestedFlows().size(), 2);
        Assert.assertEquals(iterator.next(), descriptor1);
        Assert.assertEquals(iterator.next(), descriptor2);
    }

    /** Tests mutating attempted workflows. */
    @Test public void testAttemptedWorkflow() throws Exception {
        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertNull(ctx.getAttemptedFlow());

        AuthenticationFlowDescriptor descriptor = new AuthenticationFlowDescriptor("test");
        ctx.setAttemptedFlow(descriptor);
        Assert.assertEquals(ctx.getAttemptedFlow(), descriptor);
    }

    /** Tests setting completion instant. */
    @Test public void testCompletionInstant() throws Exception {
        AuthenticationContext ctx = new AuthenticationContext(null);
        Assert.assertEquals(ctx.getCompletionInstant(), 0);

        long now = System.currentTimeMillis();
        // this is here to allow the event's creation time to deviate from the 'start' time
        Thread.sleep(50);

        ctx.setCompletionInstant();
        Assert.assertTrue(ctx.getCompletionInstant() > now);
    }
}
