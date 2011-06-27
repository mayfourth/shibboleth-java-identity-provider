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

package net.shibboleth.idp.attribute.resolver;

import java.util.ArrayList;

import org.opensaml.xml.security.EvaluableCriteria;
import org.opensaml.xml.security.StaticResponseEvaluableCritieria;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Unit test for {@link BaseResolverPlugin}. */
public class BaseResolverPluginTest {

    /** Test an instantiated object has the proper state. */
    @Test
    public void testInstantiation() {
        MockBaseResolverPlugin plugin = new MockBaseResolverPlugin(" foo ", "bar");

        Assert.assertEquals(plugin.getId(), "foo");
        Assert.assertFalse(plugin.isPropagateResolutionExceptions());
        Assert.assertNull(plugin.getEvaluationCondition());
        Assert.assertNotNull(plugin.getDependencies());
        Assert.assertTrue(plugin.getDependencies().isEmpty());
    }

    /** Test setters to {@link BaseResolverPlugin#setPropagateResolutionExceptions(boolean)}. */
    @Test
    public void testPropogateSetters() {
        MockBaseResolverPlugin plugin = new MockBaseResolverPlugin("foo", "bar");

        plugin.setPropagateResolutionExceptions(true);
        Assert.assertTrue(plugin.isPropagateResolutionExceptions());

        plugin.setPropagateResolutionExceptions(true);
        Assert.assertTrue(plugin.isPropagateResolutionExceptions());

        plugin.setPropagateResolutionExceptions(false);
        Assert.assertFalse(plugin.isPropagateResolutionExceptions());
    }

    /** Testing setting and evaluating expression conditions. */
    @Test
    public void testExpressionCondition() {
        AttributeResolutionContext context = new AttributeResolutionContext(null);

        MockBaseResolverPlugin plugin = new MockBaseResolverPlugin("foo", "bar");
        Assert.assertTrue(plugin.isApplicable(context));

        EvaluableCriteria<AttributeResolutionContext> criteria = StaticResponseEvaluableCritieria.FALSE_RESPONSE;
        plugin.setEvaluationCondition(criteria);
        Assert.assertNotNull(plugin.getEvaluationCondition());
        Assert.assertEquals(plugin.getEvaluationCondition(), criteria);
        Assert.assertFalse(plugin.isApplicable(context));

        criteria = StaticResponseEvaluableCritieria.TRUE_RESPONSE;
        plugin.setEvaluationCondition(criteria);
        Assert.assertNotNull(plugin.getEvaluationCondition());
        Assert.assertEquals(plugin.getEvaluationCondition(), criteria);
        Assert.assertTrue(plugin.isApplicable(context));

        plugin.setEvaluationCondition(null);
        Assert.assertNull(plugin.getEvaluationCondition());
        Assert.assertTrue(plugin.isApplicable(context));
    }

    /** Test add, removing, setting dependencies. */
    @Test
    public void testDependencies() {
        MockBaseResolverPlugin plugin = new MockBaseResolverPlugin("foo", "bar");

        plugin.setDependencies(null);
        Assert.assertNotNull(plugin.getDependencies());
        Assert.assertTrue(plugin.getDependencies().isEmpty());

        ArrayList<ResolverPluginDependency> depdencies = new ArrayList<ResolverPluginDependency>();
        plugin.setDependencies(depdencies);
        Assert.assertNotNull(plugin.getDependencies());
        Assert.assertTrue(plugin.getDependencies().isEmpty());

        depdencies.add(null);
        plugin.setDependencies(depdencies);
        Assert.assertNotNull(plugin.getDependencies());
        Assert.assertTrue(plugin.getDependencies().isEmpty());

        ResolverPluginDependency dep1 = new ResolverPluginDependency("foo", "bar");
        ResolverPluginDependency dep2 = new ResolverPluginDependency("foo", "baz");

        depdencies.add(dep1);
        depdencies.add(dep1);
        depdencies.add(dep2);

        plugin.setDependencies(depdencies);
        Assert.assertNotNull(plugin.getDependencies());
        Assert.assertTrue(plugin.getDependencies().size() == 2);

        try {
            plugin.getDependencies().add(dep1);
            Assert.fail("able to add entry to supossedly unmodifiable collection");
        } catch (UnsupportedOperationException e) {
            // expected this
        }
    }

    /** Test {@link BaseResolverPlugin#resolve(AttributeResolutionContext)}. */
    @Test
    public void testResolver() throws Exception {
        AttributeResolutionContext context = new AttributeResolutionContext(null);
        MockBaseResolverPlugin plugin = new MockBaseResolverPlugin("foo", "bar");

        Assert.assertEquals(plugin.resolve(context), "bar");
    }

    /**
     * This class implements the minimal level of functionality and is meant only as a means of testing the abstract
     * {@link BaseResolverPlugin}.
     */
    private static final class MockBaseResolverPlugin extends BaseResolverPlugin<String> {

        /** Static value return by {@link #doResolve(AttributeResolutionContext)}. */
        private String resolverValue;

        /**
         * Constructor.
         * 
         * @param id id of this plugin
         * @param value value returned by {@link #doResolve(AttributeResolutionContext)}
         */
        public MockBaseResolverPlugin(String id, String value) {
            super(id);
            resolverValue = value;
        }

        /** {@inheritDoc} */
        protected String doResolve(AttributeResolutionContext resolutionContext) throws AttributeResolutionException {
            return resolverValue;
        }
    }
}