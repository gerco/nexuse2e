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
 * A thread-local bound STORAGE map for data. Usually to be used for pre-setting conversation and message ID so all NexusException instances will always have the information available during an entire pipeline's execution.
 * Created by JJerke on 02.10.2014.
 */
public class NexusThreadStorage {

    private static final ThreadLocal<Map<String, Object>> STORAGE = new ThreadLocal<Map<String, Object>>();

    public static void set(String identifier, Object data) {
        if (StringUtils.isBlank(identifier) || null == data) {
            return;
        }
        if (null == STORAGE.get()) {
            STORAGE.set(new HashMap<String, Object>());
        }
        STORAGE.get().put(identifier, data);
    }

    public static void remove(String identifier) {
        if (null == STORAGE.get() || StringUtils.isBlank(identifier)) {
            return;
        }
        STORAGE.get().remove(identifier);
        if (STORAGE.get().isEmpty()) {
            STORAGE.remove();
        }
    }

    public static Object get(String identifier) {
        if (null == STORAGE.get() || StringUtils.isBlank(identifier)) {
            return null;
        }
        return STORAGE.get().get(identifier);
    }
}
