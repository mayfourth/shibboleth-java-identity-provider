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

package net.shibboleth.idp.metadata.impl;

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.opensaml.xml.security.Criteria;

/** Represents a criteria that a particular binding is supported. */
public class SupportedBindingCriteria implements Criteria {

    /** Binding that must be supported by the entity. */
    private String binding;

    /**
     * Constructor.
     * 
     * @param supportedBinding binding that must be supported by the entity, never null and must contain a namespace
     */
    public SupportedBindingCriteria(String supportedBinding) {
        binding = StringSupport.trimOrNull(supportedBinding);
        Assert.isNotNull(binding, "Supported binding can not be null or empty");
    }

    /**
     * Gets the binding that must be supported by the entity.
     * 
     * @return binding that must be supported by the entity, never null or empty
     */
    public String getSupportedBinding() {
        return binding;
    }
}