/**
 * 
 */
package org.nexuse2e.configuration;

/**
 * @author JJerke
 * 
 *         This enumerates certificate types. Used to be part of <code>Constants.java</code>.
 */
public enum CertificateType {
    ALL(0), LOCAL(1), PARTNER(2), CA(3), REQUEST(4), PRIVATE_KEY(5), CACERT_METADATA(6), @Deprecated
    CERT_PART(7), STAGING(8);

    int typeOrdinal = 0;

    CertificateType(int ordinal) {
        this.typeOrdinal = ordinal;
    }

    public int getOrdinal() {
        return this.typeOrdinal;
    }

    public CertificateType getByOrdinal(int ordinal) {
        if (0 <= ordinal) {
            for (CertificateType oneType : CertificateType.values()) {
                if (oneType.getOrdinal() == ordinal) {
                    return oneType;
                }
            }
        }
        throw new IllegalArgumentException("Parameter must be the ordinal of a valid CertificateType!");
    }
}
