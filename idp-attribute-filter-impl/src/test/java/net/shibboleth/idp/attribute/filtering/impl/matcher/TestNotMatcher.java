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

package net.shibboleth.idp.attribute.filtering.impl.matcher;

import java.util.Collection;

import net.shibboleth.idp.attribute.Attribute;
import net.shibboleth.idp.attribute.filtering.AttributeFilteringException;
import net.shibboleth.idp.attribute.filtering.AttributeValueMatcher;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/** Test the {@link NotMatcher} matcher. */
public class TestNotMatcher {

    /**
     * Test the {@link NotMatcher} matcher in a variety of configurations.
     * 
     * @throws AttributeFilteringException if the filter has issues.
     * @throws ComponentInitializationException if initialize fails when we didn't exdpect it to
     * @throws ComponentValidationException never
     */
    @Test public void notMatcherTest() throws AttributeFilteringException, ComponentInitializationException,
            ComponentValidationException {
        final Attribute<String> attribute = new Attribute<String>("attribute");
        final Collection<String> values = Lists.newArrayList("zero", "one", "two", "three");
        attribute.setValues(values);

        NotMatcher not = new NotMatcher();
        boolean threw = false;
        try {
            not.getMatchingValues(attribute, null);
            Assert.assertTrue(false, "unreachable code (new)");
        } catch (AttributeFilteringException e) {
            threw = true;
        }
        Assert.assertTrue(threw, "Non initialized filter should throw");

        threw = false;
        try {
            not.initialize();
            Assert.assertTrue(false, "initialized should fail if no paraeter set");
        } catch (ComponentInitializationException e) {
            threw = true;
        }
        Assert.assertTrue(threw, "Initialize of non setup filter should throw");

        // starrt with a new one
        not = new NotMatcher();
        not.setSubMatcher(AttributeValueMatcher.MATCHES_ALL);
        not.initialize();

        Assert.assertTrue(not.getMatchingValues(attribute, null).isEmpty(), "Not of everything is nothing");

        final OrMatcher or = new OrMatcher();
        or.setSubMatchers(Lists.newArrayList(
                (AttributeValueMatcher) DestroyableValidatableAttributeValueStringMatcher.newMatcher("zero", true),
                DestroyableValidatableAttributeValueStringMatcher.newMatcher("two", true)));
        not.setSubMatcher(or);
        not.initialize();

        Collection<String> expected = Lists.newArrayList("one", "three");
        Assert.assertEquals(not.getMatchingValues(attribute, null), expected, "simple not");

        expected = Lists.newArrayList("zero", "two");
        NotMatcher notNot = new NotMatcher();
        // Play a trick to stop the submatcher being reinitialized.
        notNot.setSubMatcher(AttributeValueMatcher.MATCHES_ALL);
        notNot.initialize();
        notNot.setSubMatcher(not);
        Assert.assertEquals(notNot.getMatchingValues(attribute, null), expected, "not of not");

        DestroyableValidatableAttributeValueStringMatcher destroyTester =
                DestroyableValidatableAttributeValueStringMatcher.newMatcher("ONE", false);
        not.setSubMatcher(destroyTester);

        Assert.assertFalse(destroyTester.isValidated(), "Has validate not yet been passed down");
        not.validate();
        Assert.assertTrue(destroyTester.isValidated(), "Has validate been passed down");

        // test destroy
        Assert.assertFalse(destroyTester.isDestroyed(), "Has destroy not yet been passed down");
        not.destroy();
        Assert.assertTrue(destroyTester.isDestroyed(), "destroyable sub matcher not destroyed");
        try {
            not.getMatchingValues(attribute, null);
            Assert.assertTrue(false, "unreachable code (destroy)");
        } catch (AttributeFilteringException e) {
            threw = true;
        }
        Assert.assertTrue(threw, "should not be able to match after destrouction");

    }
}
