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

package net.shibboleth.idp.attribute.resolver.impl.ad;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.shibboleth.idp.attribute.Attribute;
import net.shibboleth.idp.attribute.AttributeValue;
import net.shibboleth.idp.attribute.resolver.ResolutionException;
import net.shibboleth.idp.attribute.resolver.BaseAttributeDefinition;
import net.shibboleth.idp.attribute.resolver.ResolverPluginDependency;
import net.shibboleth.idp.attribute.resolver.impl.TestSources;
import net.shibboleth.idp.persistence.PersistenceManager;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;

import org.apache.commons.collections.map.HashedMap;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/** test for {@link net.shibboleth.idp.attribute.resolver.impl.TransientIdAttributeDefinition}. */
public class TransientIdAttributeDefinitionTest {

    /** The name. */
    private static final String TEST_ATTRIBUTE_NAME = "simple";

    private static final long TEST_LIFETIME = 10;

    private static final int TEST_ID_SIZE = 32;

    private void testInitializeFail(BaseAttributeDefinition simple, String message) {
        try {
            simple.initialize();
            Assert.fail(message);
        } catch (ComponentInitializationException e) {
            // OK
        }
    }

    @Test public void testSingle() throws ComponentInitializationException, ResolutionException {
        final TransientIdAttributeDefinition defn = new TransientIdAttributeDefinition();
        defn.setId(TEST_ATTRIBUTE_NAME);
        testInitializeFail(defn, "no dependencies");

        defn.setDependencies(Collections.singleton(new ResolverPluginDependency("foo", "bar")));
        testInitializeFail(defn, "no IdStore");

        final Store store = new Store();
        defn.setIdStore(store);

        defn.initialize();

        final Optional<Attribute> result =
                defn.doAttributeDefinitionResolve(TestSources.createResolutionContext(TestSources.PRINCIPAL_ID,
                        TestSources.IDP_ENTITY_ID, TestSources.SP_ENTITY_ID));

        Set<AttributeValue> vals = result.get().getValues();
        Assert.assertEquals(vals.size(), 1);

        String val = (String) vals.iterator().next().getValue();

        Assert.assertEquals(val, store.getLastValue().getId());
        Assert.assertTrue(val.length() >= defn.getIdSize());

        Assert.assertEquals(store.getLastValue().getPrincipalName(), TestSources.PRINCIPAL_ID);
        Assert.assertEquals(store.getLastValue().getRelyingPartyId(), TestSources.SP_ENTITY_ID);
        Assert.assertTrue(store.getLastId().contains(TestSources.IDP_ENTITY_ID));

    }

    private void constructAndFail(String sp, String idp, String principal, String whyItFailed)
            throws ComponentInitializationException {
        final TransientIdAttributeDefinition defn = new TransientIdAttributeDefinition();
        defn.setId(TEST_ATTRIBUTE_NAME);
        defn.setDependencies(Collections.singleton(new ResolverPluginDependency("foo", "bar")));
        final Store store = new Store();
        defn.setIdStore(store);
        defn.initialize();
        try {
            defn.doAttributeDefinitionResolve(TestSources.createResolutionContext(principal, idp, sp));
            Assert.fail(whyItFailed);
        } catch (ResolutionException e) {
            // OK
        }
    }

    @Test public void testFails() throws ComponentInitializationException {

        constructAndFail(TestSources.SP_ENTITY_ID, null, TestSources.PRINCIPAL_ID, "Null IdP");
        constructAndFail(TestSources.SP_ENTITY_ID, TestSources.IDP_ENTITY_ID, null, "Null principal");
        constructAndFail(null, TestSources.IDP_ENTITY_ID, TestSources.PRINCIPAL_ID, "Null SP");
    }

    @Test public void testGetters() throws ComponentInitializationException {
        final TransientIdAttributeDefinition defn = new TransientIdAttributeDefinition();
        defn.setId(TEST_ATTRIBUTE_NAME);
        defn.setDependencies(Collections.singleton(new ResolverPluginDependency("foo", "bar")));
        final Store store = new Store();
        defn.setIdStore(store);

        defn.setIdLiftetime(TEST_LIFETIME);
        defn.setIdSize(TEST_ID_SIZE);
        defn.initialize();

        Assert.assertEquals(defn.getId(), TEST_ATTRIBUTE_NAME);
        Assert.assertEquals(defn.getIdLifetime(), TEST_LIFETIME);
        Assert.assertEquals(defn.getIdSize(), TEST_ID_SIZE);

        Assert.assertEquals(defn.getIdStore(), store);
    }

    @Test public void testRerun() throws ComponentInitializationException, ResolutionException,
            InterruptedException {
        final TransientIdAttributeDefinition defn = new TransientIdAttributeDefinition();
        defn.setId(TEST_ATTRIBUTE_NAME);
        defn.setIdLiftetime(TEST_LIFETIME);
        defn.setIdSize(TEST_ID_SIZE);
        defn.setDependencies(Collections.singleton(new ResolverPluginDependency("foo", "bar")));
        final Store store = new Store();
        defn.setIdStore(store);
        defn.initialize();

        Optional<Attribute> result =
                defn.doAttributeDefinitionResolve(TestSources.createResolutionContext(TestSources.PRINCIPAL_ID,
                        TestSources.IDP_ENTITY_ID, TestSources.SP_ENTITY_ID));

        Set<AttributeValue> vals = result.get().getValues();
        String firstTime = (String) vals.iterator().next().getValue();

        result =
                defn.doAttributeDefinitionResolve(TestSources.createResolutionContext(TestSources.PRINCIPAL_ID,
                        TestSources.IDP_ENTITY_ID, TestSources.SP_ENTITY_ID));
        Assert.assertEquals(firstTime, vals.iterator().next().getValue());
        Assert.assertTrue(firstTime.length() >= defn.getIdSize());

        Thread.sleep(TEST_LIFETIME * 2);

        result =
                defn.doAttributeDefinitionResolve(TestSources.createResolutionContext(TestSources.PRINCIPAL_ID,
                        TestSources.IDP_ENTITY_ID, TestSources.SP_ENTITY_ID));
        vals = result.get().getValues();
        Assert.assertNotEquals(firstTime, vals.iterator().next().getValue());

    }

    private static class Store implements PersistenceManager<TransientIdEntry> {

        private TransientIdEntry lastValueAdded;

        private String lastIdAdded;

        private Map<String, TransientIdEntry> theMap;

        protected Store() {
            theMap = new HashedMap();
        }

        protected TransientIdEntry getLastValue() {
            return lastValueAdded;
        }

        protected String getLastId() {
            return lastIdAdded;
        }

        /** {@inheritDoc} */
        @Nullable public String getId() {
            return "TestStore";
        }

        /** {@inheritDoc} */
        public void validate() throws ComponentValidationException {
            throw new ComponentValidationException();
        }

        /** {@inheritDoc} */
        public boolean contains(String id) {
            return theMap.containsKey(id);
        }

        /** {@inheritDoc} */
        public boolean contains(TransientIdEntry item) {
            return theMap.containsValue(item);
        }

        /** {@inheritDoc} */
        public TransientIdEntry get(String id) {
            return theMap.get(id);
        }

        /** {@inheritDoc} */
        public TransientIdEntry persist(String id, TransientIdEntry item) {
            lastValueAdded = item;
            lastIdAdded = id;
            theMap.put(id, item);
            return item;
        }

        /** {@inheritDoc} */
        public TransientIdEntry remove(String id) {
            return theMap.remove(id);
        }

        /** {@inheritDoc} */
        public TransientIdEntry remove(TransientIdEntry item) {
            Assert.fail("Not implemented");
            return item;
        }
    }
}