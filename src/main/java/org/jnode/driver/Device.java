/*
 * $Id$
 *
 * Copyright (C) 2003-2015 JNode.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; If not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
 
package org.jnode.driver;

import java.util.HashMap;
import java.util.Set;

/**
 * A software representation of a hardware device.
 * <p/>
 * Every device is controlled by a Driver. These drivers are found by DeviceToDriverMapper
 * instances.
 *
 * @author Ewout Prangsma (epr@users.sourceforge.net)
 */
public class Device {

    /**
     * My identifier
     */
    private String id;

    /**
     * The API's implemented by this device
     */
    private final HashMap<Class<? extends DeviceAPI>, DeviceAPI> apis =
        new HashMap<Class<? extends DeviceAPI>, DeviceAPI>();


    /**
     * Create a new instance
     *
     * @param bus
     * @param id
     */
    public Device(String id) {
        this.id = id;
    }

    /**
     * @return The id of this device
     * @see org.jnode.driver.Device#getId()
     */
    public final String getId() {
        return id;
    }

    /**
     * Change the id of this device, only called by devicemanager
     *
     * @param newId
     */
    final void setId(String newId) {
        this.id = newId;
    }

    /**
     * Add an API implementation to the list of API's implemented by this device.
     *
     * @param apiInterface
     * @param apiImplementation
     */
    public final <T extends DeviceAPI> void registerAPI(Class<T> apiInterface, T apiImplementation) {
        if (!apiInterface.isInstance(apiImplementation)) {
            throw new IllegalArgumentException("API implementation does not implement API interface");
        }
        if (!apiInterface.isInterface()) {
            throw new IllegalArgumentException("API interface must be an interface");
        }
        apis.put(apiInterface, apiImplementation);
        final Class[] interfaces = apiInterface.getInterfaces();
        if (interfaces != null) {
            for (Class intf : interfaces) {
                if (DeviceAPI.class.isAssignableFrom(intf)) {
                    if (!apis.containsKey(intf)) {
                        apis.put((Class<? extends DeviceAPI>) intf, apiImplementation);
                    }
                }
            }
        }
    }

    /**
     * Remove an API implementation from the list of API's implemented by this device.
     *
     * @param apiInterface
     */
    public final void unregisterAPI(Class<? extends DeviceAPI> apiInterface) {
        apis.remove(apiInterface);
    }

    /**
     * Does this device implement the given API?
     *
     * @param apiInterface
     * @return boolean
     */
    public final boolean implementsAPI(Class<? extends DeviceAPI> apiInterface) {
        //lookup is classname based to handle multi isolate uscases
        for (Class clazz : apis.keySet()) {
            if (clazz.getName().equals(apiInterface.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all implemented API's?
     *
     * @return A set of Class instances
     */
    public final Set<Class<? extends DeviceAPI>> implementedAPIs() {
        return apis.keySet();
    }

    /**
     * Gets the implementation of a given API.
     *
     * @param apiInterface
     * @return The api implementation (guaranteed not null)
     * @throws ApiNotFoundException The given api has not been found
     */
    public final <T extends DeviceAPI> T getAPI(Class<T> apiInterface) throws ApiNotFoundException {
        //lookup is classname based to handle multi isolate uscases
        Class apiInterface2 = null;
        for (Class clazz : apis.keySet()) {
            if (clazz.getName().equals(apiInterface.getName())) {
                apiInterface2 = clazz;
                break;
            }
        }
        final T impl = apiInterface.cast(apis.get(apiInterface2));
        if (impl == null) {
            throw new ApiNotFoundException(apiInterface.getName());
        }
        return impl;
    }
}
