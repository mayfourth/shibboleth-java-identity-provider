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

package net.shibboleth.idp.profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.idp.profile.AbstractIdentityProviderAction.UninitializedActionException;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link AbstractIdentityProviderAction}. */
public class AbstractIdentityProviderActionTest {

    @Test
    public void testActionId() {
        MockIdentityProviderAction action = new MockIdentityProviderAction(null);
        Assert.assertNull(action.getId());

        action.setId(null);
        Assert.assertNull(action.getId());

        action.setId("   ");
        Assert.assertNull(action.getId());

        action.setId(" mock");
        Assert.assertEquals(action.getId(), "mock");
    }

    @Test
    public void testActionIinitialization() {
        MockIdentityProviderAction action = new MockIdentityProviderAction(null);
        Assert.assertFalse(action.isInitialized());

        try {
            action.initialize();
            Assert.fail();
        } catch (ComponentInitializationException e) {
            // expected this
        }

        action.setId("mock");
        try {
            action.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail();
        }

        action.setId("foo");
        Assert.assertEquals(action.getId(), "mock");
    }

    @Test
    public void testActionExecution() throws Exception {
        RequestContext springRequestContext = new RequestContextBuilder().buildRequestContext();

        MockIdentityProviderAction action = new MockIdentityProviderAction(null);
        action.setId("mock");
        action.initialize();

        Event result = action.execute(springRequestContext);
        Assert.assertTrue(action.isExecuted());
        ActionTestingSupport.assertProceedEvent(result);

        action = new MockIdentityProviderAction(new ProfileException());
        action.setId("mock");
        action.initialize();

        try {
            action.execute(springRequestContext);
            Assert.fail();
        } catch (ProfileException e) {
            // expected this
        }

        action = new MockIdentityProviderAction(new ProfileException());
        action.setId("mock");

        try {
            action.execute(springRequestContext);
            Assert.fail();
        } catch (UninitializedActionException e) {
            // expected this
        }
    }

    /** Mock {@link AbstractIdentityProviderAction}. */
    private class MockIdentityProviderAction extends AbstractIdentityProviderAction {

        private ProfileException thrownException;

        private boolean executed;

        public MockIdentityProviderAction(ProfileException exception) {
            thrownException = exception;
        }

        public boolean isExecuted() {
            return executed;
        }

        /** {@inheritDoc} */
        protected Event doExecute(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                RequestContext springRequestContext, ProfileRequestContext profileRequestContext)
                throws ProfileException {

            executed = true;

            if (thrownException != null) {
                throw thrownException;
            }

            return ActionSupport.buildProceedEvent(this);
        }
    }
}