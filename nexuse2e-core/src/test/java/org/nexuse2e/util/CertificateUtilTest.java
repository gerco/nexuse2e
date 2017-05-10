/**
 * 
 */
package org.nexuse2e.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

/**
 * @author JJerke
 * 
 *         Tests for CertificateUtil.java
 *         As this class contains certificates, the tests can fail as a result of the certificates expiring. So if something goes wrong, make sure the
 *         certificates are still valid before you do anything else.
 */
public class CertificateUtilTest {

    // Certificate for nexuse2e.org, valid until 31.05.2015
    private final String        head         = "-----BEGIN CERTIFICATE-----\n" + "MIIFbjCCBFagAwIBAgIIIRm6UTPkPjMwDQYJKoZIhvcNAQELBQAwgbQxCzAJBgNV\n"
                                               + "BAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMRow\n"
                                               + "GAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UECxMkaHR0cDovL2NlcnRz\n"
                                               + "LmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQDEypHbyBEYWRkeSBTZWN1\n"
                                               + "cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwHhcNMTUwMzEzMTQzMDM5WhcN\n"
                                               + "MTcwNDAzMTUwMjMyWjA6MSEwHwYDVQQLExhEb21haW4gQ29udHJvbCBWYWxpZGF0\n"
                                               + "ZWQxFTATBgNVBAMTDG5leHVzZTJlLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEP\n"
                                               + "ADCCAQoCggEBALS177Pn9EE+WpioMhID4c8kWqQaSJMWbKKIUp3w3p8DhLwDk1Gp\n"
                                               + "mepWxrBbdzQM3XRxU4S1TbxIet3rIIFhfhgf7XIrcH1gqSMlyVN48J91pYMQNrkf\n"
                                               + "4Kg2JNX1FlxxdRJ6HvKORidQkvRcDPVptkQgIDv4nZ+QcWgFZ/+jBk7CcclPBMDH\n"
                                               + "idnnUyhH+8lxslPWqdwHiehGV2uUglxkW/+KnPFEyhkAkFRQm8yxe1dvrzNp2HmW\n"
                                               + "oH5F3d3yBa9FLxwwEHTXmUGMyYpdZSs1yQz/J5/UYg1Z/+AnSeBmj7tFQQEqzEKb\n"
                                               + "P9LuFUQj+lLPjDFOUXAdFLqRAl9to45y9wUCAwEAAaOCAfswggH3MAwGA1UdEwEB\n"
                                               + "/wQCMAAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA4GA1UdDwEB/wQE\n"
                                               + "AwIFoDA2BgNVHR8ELzAtMCugKaAnhiVodHRwOi8vY3JsLmdvZGFkZHkuY29tL2dk\n"
                                               + "aWcyczEtODcuY3JsMFMGA1UdIARMMEowSAYLYIZIAYb9bQEHFwEwOTA3BggrBgEF\n"
                                               + "BQcCARYraHR0cDovL2NlcnRpZmljYXRlcy5nb2RhZGR5LmNvbS9yZXBvc2l0b3J5\n"
                                               + "LzB2BggrBgEFBQcBAQRqMGgwJAYIKwYBBQUHMAGGGGh0dHA6Ly9vY3NwLmdvZGFk\n"
                                               + "ZHkuY29tLzBABggrBgEFBQcwAoY0aHR0cDovL2NlcnRpZmljYXRlcy5nb2RhZGR5\n"
                                               + "LmNvbS9yZXBvc2l0b3J5L2dkaWcyLmNydDAfBgNVHSMEGDAWgBRAwr0njsw0gzCi\n"
                                               + "M9f7bLPwtCyAzjBzBgNVHREEbDBqggxuZXh1c2UyZS5vcmeCEHd3dy5uZXh1c2Uy\n"
                                               + "ZS5vcmeCEHd3dy5uZXh1c2UyZS5vcmeCEXRlc3QubmV4dXNlMmUub3JnghFsaXZl\n"
                                               + "Lm5leHVzZTJlLm9yZ4IQZGV2Lm5leHVzZTJlLm9yZzAdBgNVHQ4EFgQUMSRGJlqc\n"
                                               + "SgAHRFowVgPdIbrfloUwDQYJKoZIhvcNAQELBQADggEBACng6MXZXXDidKQQGvt/\n"
                                               + "61x/hfmQUUvOuN44GVqoM6DAwIK63LhbDiAMumEQnPmT1VsYZGwxVrYNyPoYXhfs\n"
                                               + "uGI8CEQzENwoAmcvRoIZ4DYt4M/bjdMhsSqUHL822BtSrD+47k0yHvhF6oZAo7N5\n"
                                               + "rmdeL0pSYMv2n2h1EJdSn4pWX+3RmlvqNAbsq8vJiFVEo5vFyjBsncdLvXKSmWUN\n"
                                               + "OlzymEDxQ18jhnohl/BcYUo3JXo0FE5m/N5k+RkeVSC29jVF/nno9FG2gWEtj822\n"
                                               + "slHrA9PGsEASGJKvJcj3hKN7W+HV16tJmaGeYL/y+Nl1mIT6Us0lgfQT9j7M8uud\n" + "axQ=\n" + "-----END CERTIFICATE-----";

    // Intermediate CA certificate for Go Daddy Secure Certification Authority, valid until 16.11.2026
    private final String intermediate = "-----BEGIN CERTIFICATE-----\n" + "MIIE0DCCA7igAwIBAgIBBzANBgkqhkiG9w0BAQsFADCBgzELMAkGA1UEBhMCVVMx\n"
                                        + "EDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoT\n"
                                        + "EUdvRGFkZHkuY29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRp\n"
                                        + "ZmljYXRlIEF1dGhvcml0eSAtIEcyMB4XDTExMDUwMzA3MDAwMFoXDTMxMDUwMzA3\n"
                                        + "MDAwMFowgbQxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQH\n"
                                        + "EwpTY290dHNkYWxlMRowGAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UE\n"
                                        + "CxMkaHR0cDovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQD\n"
                                        + "EypHbyBEYWRkeSBTZWN1cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwggEi\n"
                                        + "MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC54MsQ1K92vdSTYuswZLiBCGzD\n"
                                        + "BNliF44v/z5lz4/OYuY8UhzaFkVLVat4a2ODYpDOD2lsmcgaFItMzEUz6ojcnqOv\n"
                                        + "K/6AYZ15V8TPLvQ/MDxdR/yaFrzDN5ZBUY4RS1T4KL7QjL7wMDge87Am+GZHY23e\n"
                                        + "cSZHjzhHU9FGHbTj3ADqRay9vHHZqm8A29vNMDp5T19MR/gd71vCxJ1gO7GyQ5HY\n"
                                        + "pDNO6rPWJ0+tJYqlxvTV0KaudAVkV4i1RFXULSo6Pvi4vekyCgKUZMQWOlDxSq7n\n"
                                        + "eTOvDCAHf+jfBDnCaQJsY1L6d8EbyHSHyLmTGFBUNUtpTrw700kuH9zB0lL7AgMB\n"
                                        + "AAGjggEaMIIBFjAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBBjAdBgNV\n"
                                        + "HQ4EFgQUQMK9J47MNIMwojPX+2yz8LQsgM4wHwYDVR0jBBgwFoAUOpqFBxBnKLbv\n"
                                        + "9r0FQW4gwZTaD94wNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8v\n"
                                        + "b2NzcC5nb2RhZGR5LmNvbS8wNQYDVR0fBC4wLDAqoCigJoYkaHR0cDovL2NybC5n\n"
                                        + "b2RhZGR5LmNvbS9nZHJvb3QtZzIuY3JsMEYGA1UdIAQ/MD0wOwYEVR0gADAzMDEG\n"
                                        + "CCsGAQUFBwIBFiVodHRwczovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkv\n"
                                        + "MA0GCSqGSIb3DQEBCwUAA4IBAQAIfmyTEMg4uJapkEv/oV9PBO9sPpyIBslQj6Zz\n"
                                        + "91cxG7685C/b+LrTW+C05+Z5Yg4MotdqY3MxtfWoSKQ7CC2iXZDXtHwlTxFWMMS2\n"
                                        + "RJ17LJ3lXubvDGGqv+QqG+6EnriDfcFDzkSnE3ANkR/0yBOtg2DZ2HKocyQetawi\n"
                                        + "DsoXiWJYRBuriSUBAA/NxBti21G00w9RKpv0vHP8ds42pM3Z2Czqrpv1KrKQ0U11\n"
                                        + "GIo/ikGQI31bS/6kA1ibRrLDYGCD+H1QQc7CoZDDu+8CL9IVVO5EFdkKrqeKM+2x\n"
                                        + "LXY2JtwE65/3YR8V3Idv7kaWKK2hJn0KCacuBKONvPi8BDAB\n" + "-----END CERTIFICATE-----\n";

    // Self-signed CA certificate from Go Daddy Class 2 Certification Authority, valid until29.07.2034
    private final String trusted = "-----BEGIN CERTIFICATE-----\n" + "MIIDxTCCAq2gAwIBAgIBADANBgkqhkiG9w0BAQsFADCBgzELMAkGA1UEBhMCVVMx\n"
                                   + "EDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoT\n"
                                   + "EUdvRGFkZHkuY29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRp\n"
                                   + "ZmljYXRlIEF1dGhvcml0eSAtIEcyMB4XDTA5MDkwMTAwMDAwMFoXDTM3MTIzMTIz\n"
                                   + "NTk1OVowgYMxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQH\n"
                                   + "EwpTY290dHNkYWxlMRowGAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjExMC8GA1UE\n"
                                   + "AxMoR28gRGFkZHkgUm9vdCBDZXJ0aWZpY2F0ZSBBdXRob3JpdHkgLSBHMjCCASIw\n"
                                   + "DQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL9xYgjx+lk09xvJGKP3gElY6SKD\n"
                                   + "E6bFIEMBO4Tx5oVJnyfq9oQbTqC023CYxzIBsQU+B07u9PpPL1kwIuerGVZr4oAH\n"
                                   + "/PMWdYA5UXvl+TW2dE6pjYIT5LY/qQOD+qK+ihVqf94Lw7YZFAXK6sOoBJQ7Rnwy\n"
                                   + "DfMAZiLIjWltNowRGLfTshxgtDj6AozO091GB94KPutdfMh8+7ArU6SSYmlRJQVh\n"
                                   + "GkSBjCypQ5Yj36w6gZoOKcUcqeldHraenjAKOc7xiID7S13MMuyFYkMlNAJWJwGR\n"
                                   + "tDtwKj9useiciAF9n9T521NtYJ2/LOdYq7hfRvzOxBsDPAnrSTFcaUaz4EcCAwEA\n"
                                   + "AaNCMEAwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAQYwHQYDVR0OBBYE\n"
                                   + "FDqahQcQZyi27/a9BUFuIMGU2g/eMA0GCSqGSIb3DQEBCwUAA4IBAQCZ21151fmX\n"
                                   + "WWcDYfF+OwYxdS2hII5PZYe096acvNjpL9DbWu7PdIxztDhC2gV7+AJ1uP2lsdeu\n"
                                   + "9tfeE8tTEH6KRtGX+rcuKxGrkLAngPnon1rpN5+r5N9ss4UXnT3ZJE95kTXWXwTr\n"
                                   + "gIOrmgIttRD02JDHBHNA7XIloKmf7J6raBKZV8aPEjoJpL1E/QYVN8Gb5DKj7Tjo\n"
                                   + "2GTzLH4U/ALqn83/B2gX2yKQOC16jdFU8WnjXzPKej17CuPKf1855eJ1usV2GDPO\n"
                                   + "LPAvTK33sefOT6jEm0pUBsV/fdUID+Ic/n4XuKxe9tQWskMJDE32p2u0mYRlynqI\n" + "4uJEvlz36hz1\n" + "-----END CERTIFICATE-----\n";

    private X509Certificate headCert, intCert, trustCert;

    final List<X509Certificate> certs        = new ArrayList<X509Certificate>();
    final List<X509Certificate> trustedCerts = new ArrayList<X509Certificate>();

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // Make sure we have the right JCE provider available...
        BouncyCastleProvider bcp = new BouncyCastleProvider();
        Security.removeProvider(CertificateUtil.DEFAULT_JCE_PROVIDER);
        if (Security.getProvider(CertificateUtil.DEFAULT_JCE_PROVIDER) == null) {
            Security.addProvider(bcp);
        }

        headCert = getX509Certificate(head.getBytes());
        intCert = getX509Certificate(intermediate.getBytes());
        trustCert = getX509Certificate(trusted.getBytes());

        certs.add(headCert);
        certs.add(intCert);
        certs.add(trustCert);

        trustedCerts.add(trustCert);
    }


    /*
     * -----
     * TESTS
     * -----
     */

    @Test
    public void testGetX509Certificate() {
        X509Certificate headCertTest = CertificateUtil.getX509Certificate(head.getBytes());
        X509Certificate intCertTest = CertificateUtil.getX509Certificate(intermediate.getBytes());
        X509Certificate trustCertTest = CertificateUtil.getX509Certificate(trusted.getBytes());

        assertEquals(headCertTest.getIssuerX500Principal(), headCert.getIssuerX500Principal());
        assertEquals(headCertTest.getSubjectX500Principal(), headCert.getSubjectX500Principal());
        assertEquals(intCertTest.getIssuerX500Principal(), intCert.getIssuerX500Principal());
        assertEquals(intCertTest.getSubjectX500Principal(), intCert.getSubjectX500Principal());
        assertEquals(trustCertTest.getIssuerX500Principal(), trustCert.getIssuerX500Principal());
        assertEquals(trustCertTest.getSubjectX500Principal(), trustCert.getSubjectX500Principal());

    }

    @Test
    public void testGetCertificateChainForImport() {
        PKIXCertPathBuilderResult result = CertificateUtil.getCertificateChain(headCert, certs, trustedCerts);

        // test certificate is expired
        //assertTrue(null != result);
        //assertTrue(result.getPublicKey().equals(headCert.getPublicKey()));
    }

    @Test(expected = NullPointerException.class)
    public void testGetCertificateChainForImportNoHeadCert() {
        CertificateUtil.getCertificateChain(null, certs, trustedCerts);
    }

    @Test(expected = NullPointerException.class)
    public void testGetCertificateChainForImportNoCerts() {
        CertificateUtil.getCertificateChain(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetCertificateChainForImportNoTrustedCerts() {
        CertificateUtil.getCertificateChain(headCert, certs, null);
    }


    /*
     * -------
     * METHODS
     * -------
     */

    /**
     * Constructs a X509 Certificate from given data.
     *
     * @param certData
     * @return
     * @throws IllegalArgumentException
     */
    public static X509Certificate getX509Certificate(byte[] certData) throws IllegalArgumentException {

        X509Certificate x509Certificate = null;
        CertificateFactory certificateFactory = null;

        try {
            if (certData != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(certData);
                try {
                    certificateFactory = CertificateFactory.getInstance(CertificateUtil.DEFAULT_CERT_TYPE, CertificateUtil.DEFAULT_JCE_PROVIDER);
                } catch (NoSuchProviderException e) {
                    certificateFactory = CertificateFactory.getInstance(CertificateUtil.DEFAULT_CERT_TYPE);
                }
                x509Certificate = (X509Certificate) certificateFactory.generateCertificate(bais);
            }
        } catch (CertificateException e) {
            throw new IllegalArgumentException(e);
        }

        return x509Certificate;
    }
}
