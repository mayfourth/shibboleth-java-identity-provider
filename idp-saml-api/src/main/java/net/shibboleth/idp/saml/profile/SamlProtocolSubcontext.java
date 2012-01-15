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

package net.shibboleth.idp.saml.profile;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.AbstractSubcontext;
import org.opensaml.messaging.context.SubcontextContainer;

/**
 * Subcontext that carries information about the ongoing SAML protocol. This context usually appears as a subcontext of
 * the {@link org.opensaml.messaging.context.MessageContext} that carries the actual SAML message.
 */
public class SamlProtocolSubcontext extends AbstractSubcontext {

    /** The SAML protocol in use. */
    private String protocol;

    /** The relay state associated with the message. */
    private String relayState;

    /** The role in which the SAML entity is operating. */
    private QName role;

    /**
     * Constructor.
     * 
     * @param parent parent context, may be null
     */
    public SamlProtocolSubcontext(SubcontextContainer parent) {
        super(parent);
    }

    /**
     * Gets the SAML protocol in use.
     * 
     * @return SAML protocol in use, may be null
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the SAML protocol in use.
     * 
     * @param samlProtocol SAML protocol in use
     */
    public void setProtocol(String samlProtocol) {
        protocol = StringSupport.trimOrNull(samlProtocol);
    }

    /**
     * Gets the role in which the SAML entity is operating.
     * 
     * @return role in which the SAML entity is operating, may be null
     */
    public QName getRole() {
        return role;
    }

    /**
     * Sets the role in which the SAML entity is operating.
     * 
     * @param samlRole role in which the SAML entity is operating
     */
    public void setRole(QName samlRole) {
        role = samlRole;
    }

    /**
     * Gets the relay state associated with this protocol exchange.
     * 
     * @return relay state associated with this protocol exchange, may be null
     */
    public String getRelayState() {
        return relayState;
    }

    /**
     * Sets the relay state associated with this protocol exchange.
     * 
     * @param state relay state associated with this protocol exchange
     */
    public void setRelayState(String state) {
        relayState = StringSupport.trimOrNull(state);
    }
}