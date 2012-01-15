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

package net.shibboleth.idp.attribute.resolver.impl;

import java.util.Collection;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;
import net.shibboleth.idp.attribute.Attribute;
import net.shibboleth.idp.attribute.resolver.AttributeResolutionContext;
import net.shibboleth.idp.attribute.resolver.AttributeResolutionException;
import net.shibboleth.idp.attribute.resolver.BaseAttributeDefinition;
import net.shibboleth.idp.attribute.resolver.ResolverPluginDependency;
import net.shibboleth.utilities.java.support.collection.LazySet;

/**
 * A Simple Attribute definition. Basically it copies all inputs to outputs.
 * 
 * Note that a given name will be resolved both from attributes and from data providers and so an element of cobination
 * can be achieved.
 * */
@ThreadSafe
public class SimpleAttributeDefinition extends BaseAttributeDefinition {

    /** {@inheritDoc} */
    protected Attribute<?> doAttributeResolution(final AttributeResolutionContext resolutionContext)
            throws AttributeResolutionException {
        final Set<ResolverPluginDependency> depends = getDependencies();
        if (null == depends) {
            return null;
        }

        final Collection<Object> resultValues = new LazySet<Object>();
        for (ResolverPluginDependency dep : depends) {
            final Attribute<?> dependentAttribute = dep.getDependentAttribute(resolutionContext);
            if (null != dependentAttribute) {
                CollectionSupport.addNonNull(dependentAttribute.getValues(), resultValues);
            }
        }

        final Attribute<Object> result = new Attribute<Object>(getId());
        result.setValues(resultValues);
        return result;
    }

}
