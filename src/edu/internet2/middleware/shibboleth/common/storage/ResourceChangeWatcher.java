/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.storage;

import java.util.List;
import java.util.TimerTask;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import edu.internet2.middleware.shibboleth.common.storage.ResourceListener.ResourceChange;

/**
 * A watcher that invokes a callback when a resource update/deletion has been detected.
 */
public class ResourceChangeWatcher extends TimerTask{

    /** Logger */
    private final static Logger log = Logger.getLogger(ResourceChangeWatcher.class);

    /** Default polling frequency, 12 hours */
    public final static long DEFAULT_POLL_FREQUENCY = 1000 * 60 * 60 * 12;

    /** Default maximum retry attempts, 0 */
    public final static int DEFAULT_MAX_RETRY_ATTEMPTS = 0;

    /** Resource being watched */
    private Resource watchedResource;

    /** Frequency, in milliseconds, the resource is polled for changes */
    private long pollFrequency;

    /** Max number of polls to try before considering the resource inaccessible */
    private int maxRetryAttempts;

    /** Number of times the resource has been polled but generated an error */
    private int currentRetryAttempts;

    /** Whether the resource currently exists */
    private boolean resourceExist;

    /** Last time the resource was modified */
    private DateTime lastModification;

    /** Registered listeners of resource change notifications */
    private FastList<ResourceListener> resourceListeners;

    /**
     * Constructor
     * 
     * @param resource the resource to be watched
     * 
     * @throws ResourceException thrown if resource existance or last modification time can not be determined
     */
    public ResourceChangeWatcher(Resource resource) throws ResourceException {
        this(resource, DEFAULT_POLL_FREQUENCY, DEFAULT_MAX_RETRY_ATTEMPTS);
    }

    /**
     * Constructor
     * 
     * @param resource the resource to be watched
     * @param pollFrequency the frequency, in milliseconds, to poll the resource for changes
     * 
     * @throws ResourceException thrown if resource existance or last modification time can not be determined
     */
    public ResourceChangeWatcher(Resource resource, long pollFrequency) throws ResourceException {
        this(resource, pollFrequency, DEFAULT_MAX_RETRY_ATTEMPTS);
    }

    /**
     * Constructor
     * 
     * @param resource the resource to be watched
     * @param pollFrequency the frequency, in milliseconds, to poll the resource for changes
     * @param maxRetryAttempts maximum number of poll attempts before the resource is considered inaccessible
     * 
     * @throws ResourceException thrown if resource existance or last modification time can not be determined
     */
    public ResourceChangeWatcher(Resource resource, long pollFrequency, int maxRetryAttempts) throws ResourceException {
        if (resource == null) {
            throw new NullPointerException("Watched resource is null");
        }

        if (pollFrequency <= 0) {
            throw new IllegalArgumentException("Polling frequency must be greater than zero");
        }

        if (maxRetryAttempts < 0) {
            throw new IllegalArgumentException("Max retry attempts must be greater than, or equal to, zero");
        }

        watchedResource = resource;
        this.pollFrequency = pollFrequency;
        this.maxRetryAttempts = maxRetryAttempts;
        currentRetryAttempts = 0;

        if (watchedResource.exists()) {
            resourceExist = true;
            lastModification = watchedResource.getLastModifiedTime();
        } else {
            resourceExist = false;
        }

        resourceListeners = new FastList<ResourceListener>();
    }

    /**
     * Gets the frequency, in milliseonds, the watched resource should be polled.
     * 
     * @return frequency the watched resource should be polled
     */
    public long getPollingFrequency() {
        return pollFrequency;
    }

    /**
     * Gets the list of registered resource listeners. New listeners may be registered with the list or old ones
     * removed.
     * 
     * @return list of registered resource listeners
     */
    public List<ResourceListener> getResourceListeners() {
        return resourceListeners;
    }

    /** {@inheritDoc} */
    public void run() {
        try {
            if (watchedResource.exists()) {
                if (!resourceExist) {
                    signalListeners(ResourceChange.CREATION);
                    lastModification = watchedResource.getLastModifiedTime();
                } else {
                    if (lastModification.isBefore(watchedResource.getLastModifiedTime())) {
                        signalListeners(ResourceChange.UPDATE);
                        lastModification = watchedResource.getLastModifiedTime();
                    }
                }
            } else {
                if (resourceExist) {
                    signalListeners(ResourceChange.DELETE);
                }
            }
        } catch (ResourceException e) {
            log.warn("Resource " + watchedResource.getLocation() + " could not be accessed", e);
            currentRetryAttempts++;
            if (currentRetryAttempts >= maxRetryAttempts) {
                cancel();
                log.error("Resource "
                                + watchedResource.getLocation()
                                + " was not accessible for max number of retry attempts.  This resource will no longer be watched");
            }
        }
    }

    /**
     * Signals all registered listeners of a resource change
     * 
     * @param changeType the resource change type
     */
    protected void signalListeners(ResourceChange changeType) {
        synchronized (resourceListeners) {
            switch (changeType) {
                case CREATION:
                    for (ResourceListener listener : resourceListeners) {
                        listener.onResourceCreate();
                    }
                case UPDATE:
                    for (ResourceListener listener : resourceListeners) {
                        listener.onResourceUpdate();
                    }
                case DELETE:
                    for (ResourceListener listener : resourceListeners) {
                        listener.onResourceDelete();
                    }
            }
        }
    }
}