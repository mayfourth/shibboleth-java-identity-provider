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

package net.shibboleth.idp.attribute.resolver.spring.ad;

import net.shibboleth.idp.attribute.resolver.ad.impl.PrescopedAttributeDefinition;
import net.shibboleth.idp.attribute.resolver.spring.BaseAttributeDefinitionParserTest;
import net.shibboleth.idp.attribute.resolver.spring.ad.impl.PrescopedAttributeDefinitionParser;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link PrescopedAttributeDefinitionParser}.
 */
public class PrescopedAttributeDefinitionParserTest extends BaseAttributeDefinitionParserTest {

    @Test public void withScope() {
        PrescopedAttributeDefinition attrDef =
                getAttributeDefn("prescopedWith.xml", PrescopedAttributeDefinition.class);

        Assert.assertEquals(attrDef.getId(), "prescopedWith");
        Assert.assertEquals(attrDef.getScopeDelimiter(), "#");
    }

    @Test public void resolver() {
        PrescopedAttributeDefinition attrDef =
                getAttributeDefn("resolver/prescopedWith.xml", PrescopedAttributeDefinition.class);

        Assert.assertEquals(attrDef.getId(), "prescopedWith");
        Assert.assertEquals(attrDef.getScopeDelimiter(), "#");
    }

    @Test public void withoutScope() {
        PrescopedAttributeDefinition attrDef =
                getAttributeDefn("resolver/prescopedWithout.xml", PrescopedAttributeDefinition.class);

        Assert.assertEquals(attrDef.getId(), "prescopedWith");
        Assert.assertEquals(attrDef.getScopeDelimiter(), "@");
    }
}