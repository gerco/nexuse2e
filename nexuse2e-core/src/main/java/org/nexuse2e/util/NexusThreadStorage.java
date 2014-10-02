/*
 * Copyright (c) 2014 solutions direkt Gesellschaft für Lösungsentwicklung mbH
 *
 * Eigentum der solutions direkt GmbH.
 * Alle Rechte vorbehalten.
 *
 * Property of solutions direkt GmbH.
 * All rights reserved.
 *
 * solutions direkt Gesellschaft für Lösungsentwicklung mbH
 * Griegstraße 75, Haus 26
 * 22763 Hamburg
 * Deutschland / Germany
 *
 * Phone: +49 40 88155-0
 * Fax: +49 40 88155-5400
 * Email: info@direkt-gruppe.de
 * Web: http://www.direkt-gruppe.de
 */
package org.nexuse2e.util;


import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * A thread-local bound storage map for data. Usually to be used for pre-setting conversation and message ID so all NexusException instances will always have the information available during an entire pipeline's execution.
 * Created by JJerke on 02.10.2014.
 */
public class NexusThreadStorage {

    private static final ThreadLocal<Map<String, Object>> storage = new ThreadLocal<Map<String, Object>>();

    public static void set(String identifier, Object data) {
        if (StringUtils.isBlank(identifier) || null == data) {
            return;
        }
        if (null == storage.get()) {
            storage.set(new HashMap<String, Object>());
        }
        storage.get().put(identifier, data);
    }

    public static void remove(String identifier) {
        if (null == storage.get() || StringUtils.isBlank(identifier)) {
            return;
        }
        storage.get().remove(identifier);
        if (storage.get().isEmpty()) {
            storage.remove();
        }
    }

    public static Object get(String identifier) {
        if (null == storage.get() || StringUtils.isBlank(identifier)) {
            return null;
        }
        return storage.get().get(identifier);
    }
}
