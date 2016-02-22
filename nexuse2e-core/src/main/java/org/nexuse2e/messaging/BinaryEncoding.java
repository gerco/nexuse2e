/*
 * Copyright (c) 2016 solutions direkt Gesellschaft für Lösungsentwicklung mbH
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
package org.nexuse2e.messaging;


/**
 * Simple enum to flag which binary encoding a message payload uses.
 * Each enum value should have a parameter giving a user friendly name for itself.
 *
 * Created 20.02.2016.
 */
public enum BinaryEncoding {

    UNSUPPORTED("Unsupported encoding"),
    BINARY("Binary"),
    BASE64("Base 64");

    private final String encoding;

    BinaryEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getName() {
        return encoding;
    }

    public static BinaryEncoding fromString(String desiredEncoding) {
        for (BinaryEncoding b : values()) {
            if (b.toString().equalsIgnoreCase(desiredEncoding)) {
                return b;
            }
        }
        return UNSUPPORTED;
    }
}
