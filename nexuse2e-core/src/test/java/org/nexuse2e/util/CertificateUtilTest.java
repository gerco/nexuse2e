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

    // Certificate for test-intranet.direkt-gruppe.de, valid until 31.05.2015
    private final String        head         = "-----BEGIN CERTIFICATE-----\r\n" + "MIIF1jCCBL6gAwIBAgIHS38t90ZtkDANBgkqhkiG9w0BAQUFADCByjELMAkGA1UE\r\n"
                                                     + "BhMCVVMxEDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAY\r\n"
                                                     + "BgNVBAoTEUdvRGFkZHkuY29tLCBJbmMuMTMwMQYDVQQLEypodHRwOi8vY2VydGlm\r\n"
                                                     + "aWNhdGVzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkxMDAuBgNVBAMTJ0dvIERhZGR5\r\n"
                                                     + "IFNlY3VyZSBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTERMA8GA1UEBRMIMDc5Njky\r\n"
                                                     + "ODcwHhcNMTMwNjAzMTMzNTUwWhcNMTUwNTMxMTE1MDU0WjBMMSEwHwYDVQQLExhE\r\n"
                                                     + "b21haW4gQ29udHJvbCBWYWxpZGF0ZWQxJzAlBgNVBAMTHnRlc3QtaW50cmFuZXQu\r\n"
                                                     + "ZGlyZWt0LWdydXBwZS5kZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\r\n"
                                                     + "AOQnkAKDQZt3UyboDB3qm55bdnOYgyB7QbjRKYgwE6dfV8tyvQl622tnh+6Jw00n\r\n"
                                                     + "0zEwRCEeqU2HLDUJNEIcu0YbP9i4KQy0+oQC6VP7+MDptLnZwECVbK4daHFwSkbS\r\n"
                                                     + "AJ4PfrrTBxwBE7bw4J30M8ZFSdtsSJUlmHzYyYYK09nynhnKRAGZ6ZrsH9FKRYQS\r\n"
                                                     + "Bb6DB0XmhWa11NP6e546Eu/dz6wxZ8pnjz1jLgSZLljIQYdCSewcudlztP+90q2/\r\n"
                                                     + "xsdkjg0/ktrh9a0WZt18gEQpFUeKtdHz0qbI1A0OWidqnaVN3XDYW5LbROgMvAxM\r\n"
                                                     + "Cwt6HEBbM8hH67Zdt6eO2lMCAwEAAaOCAjwwggI4MA8GA1UdEwEB/wQFMAMBAQAw\r\n"
                                                     + "HQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMA4GA1UdDwEB/wQEAwIFoDAz\r\n"
                                                     + "BgNVHR8ELDAqMCigJqAkhiJodHRwOi8vY3JsLmdvZGFkZHkuY29tL2dkczEtOTIu\r\n"
                                                     + "Y3JsMFMGA1UdIARMMEowSAYLYIZIAYb9bQEHFwEwOTA3BggrBgEFBQcCARYraHR0\r\n"
                                                     + "cDovL2NlcnRpZmljYXRlcy5nb2RhZGR5LmNvbS9yZXBvc2l0b3J5LzCBgAYIKwYB\r\n"
                                                     + "BQUHAQEEdDByMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5nb2RhZGR5LmNvbS8w\r\n"
                                                     + "SgYIKwYBBQUHMAKGPmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuZ29kYWRkeS5jb20vcmVw\r\n"
                                                     + "b3NpdG9yeS9nZF9pbnRlcm1lZGlhdGUuY3J0MB8GA1UdIwQYMBaAFP2sYTKTbEXW\r\n"
                                                     + "4u6FX5q653aZaMznMIGoBgNVHREEgaAwgZ2CHnRlc3QtaW50cmFuZXQuZGlyZWt0\r\n"
                                                     + "LWdydXBwZS5kZYIid3d3LnRlc3QtaW50cmFuZXQuZGlyZWt0LWdydXBwZS5kZYIc\r\n"
                                                     + "dGVzdC1wZW9wbGUuZGlyZWt0LWdydXBwZS5kZYIddGVzdC1zZXJ2aWNlLmRpcmVr\r\n"
                                                     + "dC1ncnVwcGUuZGWCGnRlc3QtdGVhbS5kaXJla3QtZ3J1cHBlLmRlMB0GA1UdDgQW\r\n"
                                                     + "BBQg90vobb//fYRHV0mmvJ1Bel/StDANBgkqhkiG9w0BAQUFAAOCAQEAPmPc/Tl9\r\n"
                                                     + "4X2xiSWqY7xVhYrbk7fi9Ff30opprW038pyXeInVH87lzPhVjxGbZ8G5ADV3s7Vs\r\n"
                                                     + "1JYQlU+FyFxIl5cgEfccsVa3eDB2RxUGCeByxcv8IcuXDrEKZIf5qp8T+fUSX/69\r\n"
                                                     + "6NA1wUvPk0W1X5kzRtQfTe3rordjaBE7s6go1nwvfIoLOHQvw3u+722Wxrwsk1aP\r\n"
                                                     + "F3vHjx7ucZq1uW2G0RzPMjifUnGXVy/UUoSnRI+IkYmbZqAlK8Mj5JNrzsP4ArZL\r\n"
                                                     + "FUkCOwkkSPwkrRcy0PyY+3SosTqv9Jr8Fd3xcBBWIG/gP7Pw0RYAjl87mCLKDPxM\r\n" + "TL/41pBKMS6QnA==\r\n"
                                                     + "-----END CERTIFICATE-----";

    // Intermediate CA certificate for Go Daddy Secure Certification Authority, valid until 16.11.2026
    private final String        intermediate           = "-----BEGIN CERTIFICATE-----\r\n" + "MIIE3jCCA8agAwIBAgICAwEwDQYJKoZIhvcNAQEFBQAwYzELMAkGA1UEBhMCVVMx\r\n"
                                                     + "ITAfBgNVBAoTGFRoZSBHbyBEYWRkeSBHcm91cCwgSW5jLjExMC8GA1UECxMoR28g\r\n"
                                                     + "RGFkZHkgQ2xhc3MgMiBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTAeFw0wNjExMTYw\r\n"
                                                     + "MTU0MzdaFw0yNjExMTYwMTU0MzdaMIHKMQswCQYDVQQGEwJVUzEQMA4GA1UECBMH\r\n"
                                                     + "QXJpem9uYTETMBEGA1UEBxMKU2NvdHRzZGFsZTEaMBgGA1UEChMRR29EYWRkeS5j\r\n"
                                                     + "b20sIEluYy4xMzAxBgNVBAsTKmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuZ29kYWRkeS5j\r\n"
                                                     + "b20vcmVwb3NpdG9yeTEwMC4GA1UEAxMnR28gRGFkZHkgU2VjdXJlIENlcnRpZmlj\r\n"
                                                     + "YXRpb24gQXV0aG9yaXR5MREwDwYDVQQFEwgwNzk2OTI4NzCCASIwDQYJKoZIhvcN\r\n"
                                                     + "AQEBBQADggEPADCCAQoCggEBAMQt1RWMnCZM7DI161+4WQFapmGBWTtwY6vj3D3H\r\n"
                                                     + "KrjJM9N55DrtPDAjhI6zMBS2sofDPZVUBJ7fmd0LJR4h3mUpfjWoqVTr9vcyOdQm\r\n"
                                                     + "VZWt7/v+WIbXnvQAjYwqDL1CBM6nPwT27oDyqu9SoWlm2r4arV3aLGbqGmu75RpR\r\n"
                                                     + "SgAvSMeYddi5Kcju+GZtCpyz8/x4fKL4o/K1w/O5epHBp+YlLpyo7RJlbmr2EkRT\r\n"
                                                     + "cDCVw5wrWCs9CHRK8r5RsL+H0EwnWGu1NcWdrxcx+AuP7q2BNgWJCJjPOq8lh8BJ\r\n"
                                                     + "6qf9Z/dFjpfMFDniNoW1fho3/Rb2cRGadDAW/hOUoz+EDU8CAwEAAaOCATIwggEu\r\n"
                                                     + "MB0GA1UdDgQWBBT9rGEyk2xF1uLuhV+auud2mWjM5zAfBgNVHSMEGDAWgBTSxLDS\r\n"
                                                     + "kdRMEXGzYcs9of7dqGrU4zASBgNVHRMBAf8ECDAGAQH/AgEAMDMGCCsGAQUFBwEB\r\n"
                                                     + "BCcwJTAjBggrBgEFBQcwAYYXaHR0cDovL29jc3AuZ29kYWRkeS5jb20wRgYDVR0f\r\n"
                                                     + "BD8wPTA7oDmgN4Y1aHR0cDovL2NlcnRpZmljYXRlcy5nb2RhZGR5LmNvbS9yZXBv\r\n"
                                                     + "c2l0b3J5L2dkcm9vdC5jcmwwSwYDVR0gBEQwQjBABgRVHSAAMDgwNgYIKwYBBQUH\r\n"
                                                     + "AgEWKmh0dHA6Ly9jZXJ0aWZpY2F0ZXMuZ29kYWRkeS5jb20vcmVwb3NpdG9yeTAO\r\n"
                                                     + "BgNVHQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQEFBQADggEBANKGwOy9+aG2Z+5mC6IG\r\n"
                                                     + "OgRQjhVyrEp0lVPLN8tESe8HkGsz2ZbwlFalEzAFPIUyIXvJxwqoJKSQ3kbTJSMU\r\n"
                                                     + "A2fCENZvD117esyfxVgqwcSeIaha86ykRvOe5GPLL5CkKSkB2XIsKd83ASe8T+5o\r\n"
                                                     + "0yGPwLPk9Qnt0hCqU7S+8MxZC9Y7lhyVJEnfzuz9p0iRFEUOOjZv2kWzRaJBydTX\r\n"
                                                     + "RE4+uXR21aITVSzGh6O1mawGhId/dQb8vxRMDsxuxN89txJx9OjxUUAiKEngHUuH\r\n"
                                                     + "qDTMBqLdElrRhjZkAzVvb3du6/KFUJheqwNTrZEjYx8WnM25sgVjOuH0aBsXBTWV\r\n" + "U+4=\r\n"
                                                     + "-----END CERTIFICATE-----";

    // Self-signed CA certificate from Go Daddy Class 2 Certification Authority, valid until29.07.2034
    private final String        trusted      = "-----BEGIN CERTIFICATE-----\r\n" + "MIIEADCCAuigAwIBAgIBADANBgkqhkiG9w0BAQUFADBjMQswCQYDVQQGEwJVUzEh\r\n"
                                                     + "MB8GA1UEChMYVGhlIEdvIERhZGR5IEdyb3VwLCBJbmMuMTEwLwYDVQQLEyhHbyBE\r\n"
                                                     + "YWRkeSBDbGFzcyAyIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MB4XDTA0MDYyOTE3\r\n"
                                                     + "MDYyMFoXDTM0MDYyOTE3MDYyMFowYzELMAkGA1UEBhMCVVMxITAfBgNVBAoTGFRo\r\n"
                                                     + "ZSBHbyBEYWRkeSBHcm91cCwgSW5jLjExMC8GA1UECxMoR28gRGFkZHkgQ2xhc3Mg\r\n"
                                                     + "MiBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTCCASAwDQYJKoZIhvcNAQEBBQADggEN\r\n"
                                                     + "ADCCAQgCggEBAN6d1+pXGEmhW+vXX0iG6r7d/+TvZxz0ZWizV3GgXne77ZtJ6XCA\r\n"
                                                     + "PVYYYwhv2vLM0D9/AlQiVBDYsoHUwHU9S3/Hd8M+eKsaA7Ugay9qK7HFiH7Eux6w\r\n"
                                                     + "wdhFJ2+qN1j3hybX2C32qRe3H3I2TqYXP2WYktsqbl2i/ojgC95/5Y0V4evLOtXi\r\n"
                                                     + "EqITLdiOr18SPaAIBQi2XKVlOARFmR6jYGB0xUGlcmIbYsUfb18aQr4CUWWoriMY\r\n"
                                                     + "avx4A6lNf4DD+qta/KFApMoZFv6yyO9ecw3ud72a9nmYvLEHZ6IVDd2gWMZEewo+\r\n"
                                                     + "YihfukEHU1jPEX44dMX4/7VpkI+EdOqXG68CAQOjgcAwgb0wHQYDVR0OBBYEFNLE\r\n"
                                                     + "sNKR1EwRcbNhyz2h/t2oatTjMIGNBgNVHSMEgYUwgYKAFNLEsNKR1EwRcbNhyz2h\r\n"
                                                     + "/t2oatTjoWekZTBjMQswCQYDVQQGEwJVUzEhMB8GA1UEChMYVGhlIEdvIERhZGR5\r\n"
                                                     + "IEdyb3VwLCBJbmMuMTEwLwYDVQQLEyhHbyBEYWRkeSBDbGFzcyAyIENlcnRpZmlj\r\n"
                                                     + "YXRpb24gQXV0aG9yaXR5ggEAMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQAD\r\n"
                                                     + "ggEBADJL87LKPpH8EsahB4yOd6AzBhRckB4Y9wimPQoZ+YeAEW5p5JYXMP80kWNy\r\n"
                                                     + "OO7MHAGjHZQopDH2esRU1/blMVgDoszOYtuURXO1v0XJJLXVggKtI3lpjbi2Tc7P\r\n"
                                                     + "TMozI+gciKqdi0FuFskg5YmezTvacPd+mSYgFFQlq25zheabIZ0KbIIOqPjCDPoQ\r\n"
                                                     + "HmyW74cNxA9hi63ugyuV+I6ShHI56yDqg+2DzZduCLzrTia2cyvk0/ZM/iZx4mER\r\n"
                                                     + "dEr/VxqHD3VILs9RaRegAhJhldXRQLIQTO7ErBBDpqWeCtWVYpoNz4iCxTIM5Cuf\r\n"
                                                     + "ReYNnyicsbkqWletNw+vHX/bvZ8=\r\n" + "-----END CERTIFICATE-----";

    private X509Certificate     headCert, intCert, trustCert;

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

        assertTrue(null != result);
        assertTrue(result.getPublicKey().equals(headCert.getPublicKey()));
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
