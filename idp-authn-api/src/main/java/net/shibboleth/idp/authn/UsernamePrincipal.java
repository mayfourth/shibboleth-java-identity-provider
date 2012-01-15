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

import java.security.Principal;

import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/** Principal based on a username. */
public final class UsernamePrincipal implements Principal {

    /** The username. */
    private final String username;

    /**
     * Constructor.
     * 
     * @param user the username, can not be null or empty
     */
    public UsernamePrincipal(final String user) {
        username = Assert.isNotNull(StringSupport.trimOrNull(user), "Username can not be null or empty");
    }

    /** {@inheritDoc} */
    public String getName() {
        return username;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return username.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof UsernamePrincipal) {
            return username.equals(((UsernamePrincipal) other).getName());
        }

        return false;
    }
}