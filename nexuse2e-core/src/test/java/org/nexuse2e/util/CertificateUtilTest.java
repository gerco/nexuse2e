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
 */
public class CertificateUtilTest {

    private final String        head         = "-----BEGIN CERTIFICATE-----\r\n" + "MIIE6DCCA9CgAwIBAgIGVhzhMAXcMA0GCSqGSIb3DQEBBAUAMIGiMSEwHwYJKoZI\r\n"
                                                     + "hvcNAQkBFhJjYW1haWxAY2FzZXJ2ZXIuY2ExEzARBgNVBAYTCkNBIENvdW50cnkx\r\n"
                                                     + "ETAPBgNVBAgTCENBIFN0YXRlMRQwEgYDVQQHEwtDQSBMb2NhdGlvbjEPMA0GA1UE\r\n"
                                                     + "ChMGQ0EgT3JnMRAwDgYDVQQLEwdDQSBVbml0MRwwGgYDVQQDExNORVhVU2UyZSBU\r\n"
                                                     + "ZXN0IENoYWluMB4XDTE0MDIxMzIzMDAwMFoXDTI0MDIxMzIzMDAwMFowgcYxETAP\r\n"
                                                     + "BgNVBAMMCFRlc3RuYW1lMRQwEgYDVQQGEwtUZXN0Y291bnRyeTEQMA4GA1UECgwH\r\n"
                                                     + "VGVzdG9yZzERMA8GA1UECwwIVGVzdHVuaXQxEDAOBgNVBAcMB1Rlc3Rsb2MxEjAQ\r\n"
                                                     + "BgNVBAgMCVRlc3RzdGF0ZTEnMCUGCSqGSIb3DQEJARYYdGVzdG1haWxAdGVzdHNl\r\n"
                                                     + "cnZlci50ZXN0MScwJQYJKoZIhvcNAQkBFhh0ZXN0bWFpbEB0ZXN0c2VydmVyLnRl\r\n"
                                                     + "c3QwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQDvgTwKc141NAY/v1ts\r\n"
                                                     + "fZOuwDaPwdj+hU6utPO1hPBqlbZQx8hWSzptJUxfn2vQYuOyU+z64rAnz2WLNm2Z\r\n"
                                                     + "Gi9LDlBAL9vTx92pg1SjHJjLd1AWNhUKEpQgQP1rWIVKKeVLJA0jnAmTiwB0Cfih\r\n"
                                                     + "SuRfUhjwyPvuKkU9PL6Sgr2ikMCLUN+QioZC+1hM+xmThN679i734N0agybWPLrf\r\n"
                                                     + "qn2TFSKbcvjriuOoxM/UdHBP6ZIEgmMTUSaV9a1mwD8NPFxvS86nL8qFiuu/c/la\r\n"
                                                     + "EGjliTk91hC3RvbZJGj+3tdnUwaQELozrekpUHMiKJj68tr0DiJhxe9yT0qPcA/b\r\n"
                                                     + "FFdjGE4zjSG6N5A2FQRTEQHFD6+mlkwecSTm2V8LDlcnPgSqXRq5ERLjrTB4y4rE\r\n"
                                                     + "OjF2tUI8d2xt9gy0tnJHT7tjZbW1GZ//WO3Z0kKfGyBQSvdC4U0d6gxapj1ugnkT\r\n"
                                                     + "X1anYisgi0mGQiBqvyiomeXy0JW+azhA7arqyBbDxZio4M1NB3eToeWoFnfDvAkL\r\n"
                                                     + "t+J6R3nLFSgmcVjBZlK0QjuxiAsg86g1WsQACjVUITphnmyn37BXfn1dDoUFoKyj\r\n"
                                                     + "aaTsUVbtA9rFtnN/iB5iqY3q+d6ZAx6o4oyRDbk25Cpeh1iWy+EY+h14fon1E+kJ\r\n"
                                                     + "sb9NqeOzYeMo9AL4lNRsDO0K+QIDAQABMA0GCSqGSIb3DQEBBAUAA4IBAQAp48pU\r\n"
                                                     + "/Le4ie0Ou+QfsPhvmghJw2YTaaAUOV4oqWUOUJUxzcjoWEi8kLifRWLiZvk6IUz3\r\n"
                                                     + "Fp7P6aGwrTTkSzV70IzTm11DnV8vSggd/00HpG14AiTziwPInwQGojnfR4cn12BM\r\n"
                                                     + "nt3rxOGOy8BQyd1yJbBEXbVQZy5JYhzKcrX6pFmCpEBE62b/j1Q3/QG/yeBtt+Ox\r\n"
                                                     + "SOSs7Q8O7b5r1ckdfUc+8YFrTD+BzxqOW/79pzYFZjmqiKStVFvRVQGOg+ireaNQ\r\n"
                                                     + "toWTuw9LgyZIjN5p2TtZtxugF6qEgZMEUd8tFWsPEPl1CyTxiPv9tKqoDxJ+OC2T\r\n" + "d2QPtBi+ciRiQjZ9\r\n"
                                                     + "-----END CERTIFICATE-----\r\n" + "";

    private final String        ca           = "-----BEGIN CERTIFICATE-----\r\n" + "MIIDxDCCAqygAwIBAgIGQvewmTyoMA0GCSqGSIb3DQEBBAUAMIGiMSEwHwYJKoZI\r\n"
                                                     + "hvcNAQkBFhJjYW1haWxAY2FzZXJ2ZXIuY2ExEzARBgNVBAYTCkNBIENvdW50cnkx\r\n"
                                                     + "ETAPBgNVBAgTCENBIFN0YXRlMRQwEgYDVQQHEwtDQSBMb2NhdGlvbjEPMA0GA1UE\r\n"
                                                     + "ChMGQ0EgT3JnMRAwDgYDVQQLEwdDQSBVbml0MRwwGgYDVQQDExNORVhVU2UyZSBU\r\n"
                                                     + "ZXN0IENoYWluMB4XDTE0MDEwODIzMDAwMFoXDTI0MDEwODIzMDAwMFowgaIxITAf\r\n"
                                                     + "BgkqhkiG9w0BCQEWEmNhbWFpbEBjYXNlcnZlci5jYTETMBEGA1UEBhMKQ0EgQ291\r\n"
                                                     + "bnRyeTERMA8GA1UECBMIQ0EgU3RhdGUxFDASBgNVBAcTC0NBIExvY2F0aW9uMQ8w\r\n"
                                                     + "DQYDVQQKEwZDQSBPcmcxEDAOBgNVBAsTB0NBIFVuaXQxHDAaBgNVBAMTE05FWFVT\r\n"
                                                     + "ZTJlIFRlc3QgQ2hhaW4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCH\r\n"
                                                     + "/8o0s5PLmUGaDbx2mF8xvYHzQE9nUVnFrEyKUg3AJ64w6rltHYog5Ro07s3JBSBm\r\n"
                                                     + "CqZfH5NnSsg62ikP5RMiVK/bT8TEPyW4t9GGcJzzicC55gFe2di8+jCw37vYfHGT\r\n"
                                                     + "UTdWsTCYOBzrgwS6K1AHys+nUcyp1g7zS/YaIOhO+GXu1FhbKJq1PSe8NnPx2glG\r\n"
                                                     + "/+xk6YoqcndJekV3/+75lwGXHDTKr1ux70jpVxw3/jRFPpRj3Z7jgASm9FodFY1v\r\n"
                                                     + "QYB68H31g7n8v5JW4bHc1cCVdkXm4NEd0L5klBelylWBVf9k17gr5p7Ak/8k/mn5\r\n"
                                                     + "60OyHEXk2+E227tyiVm/AgMBAAEwDQYJKoZIhvcNAQEEBQADggEBAHmxRIC/PNAv\r\n"
                                                     + "cesQ/yeLkIt2DeQ7QI9AIPg3Xu0eb1xE3XzzRKJE2COsXtyo+o1qx/M0uKac6p1i\r\n"
                                                     + "HedYj556g35eZn8Pji9Xxy4VqZ/90Nr3G4vk63cDlpmd2XVOo9QeVAPKsymkfxKS\r\n"
                                                     + "uMrAFToJ48n3hD03L6aIvVuxYBNhjDZz+oZfq+htC2+J2R1sCU0wAB5lb6ke6a3T\r\n"
                                                     + "tqNc6Vey2xDmwRCwIoRcKifEyaz5Mfoj3u7ZK5Qp2PgwBfiRytVyQ4x6wySCQOn5\r\n"
                                                     + "x2ZpczpsUzHmUxReVE8SNJ4K+UouKD+XPjE5XCFge5DWu4rLsrJGn/nRF8AzAOFl\r\n" + "O8FZdi/UR4g=\r\n"
                                                     + "-----END CERTIFICATE-----\r\n" + "";

    // This certificate is unrelated to the above, but is explicitly added as trusted. In other words, CertificateUtil has no useful pre-trusted certificate.
    private final String        trusted      = "-----BEGIN CERTIFICATE-----\r\n" + "MIIDXzCCAkegAwIBAgIJAJv8cLGiRS/JMA0GCSqGSIb3DQEBBQUAMEYxCzAJBgNV\r\n"
                                                     + "BAYTAkRFMQswCQYDVQQIDAJISDELMAkGA1UECgwCREcxCzAJBgNVBAsMAlNPMRAw\r\n"
                                                     + "DgYDVQQDDAdDQSBST09UMB4XDTE0MDEzMDE0NTAyNFoXDTE0MDMwMTE0NTAyNFow\r\n"
                                                     + "RjELMAkGA1UEBhMCREUxCzAJBgNVBAgMAkhIMQswCQYDVQQKDAJERzELMAkGA1UE\r\n"
                                                     + "CwwCU08xEDAOBgNVBAMMB0NBIFJPT1QwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw\r\n"
                                                     + "ggEKAoIBAQCoc6QLARKPRhUZlBygaaCAYqmjLbbGcL0rEblef5LeulpSzmyUjY3O\r\n"
                                                     + "RkwMjzAUuOKptSNNnwfWpv+rsyMgdqE/+nEscCgTlVfMJGMFOozQNoS3pVzoY+z1\r\n"
                                                     + "wa3+ZLZoOxzPdFxGDlZnp4z8YDE+CQGg1cCgZ3CDk/CgeJQW0hqNETkU9wFVrjqE\r\n"
                                                     + "is4h3KILexIvU190ww40oy24Jg1nG/uTg0e5+VF4xvXN/mCbW3eIBlnz2fzb7Sc5\r\n"
                                                     + "Zy2TjyJSCRriMsltX4bxYslu0xc38eOceapRpK5nab4miHTUH8Hxw5UHF85wyRuG\r\n"
                                                     + "FPoy7E1fx8yYpYB+IlTpLHlCWEFnJMUdAgMBAAGjUDBOMB0GA1UdDgQWBBQM6/Ip\r\n"
                                                     + "x2R69TC1AOJvWyeKMG+WFDAfBgNVHSMEGDAWgBQM6/Ipx2R69TC1AOJvWyeKMG+W\r\n"
                                                     + "FDAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBBQUAA4IBAQA0UKVPyCSolME3nXug\r\n"
                                                     + "Wr2WuovRltJTcKTB4HftPOZWQmwvWIUYQc3v6UuCfpeN+SRn9CrxkyyM0JcRJmNs\r\n"
                                                     + "RdbcXNWiBpvycz0MIZUUlRTGx7CYdor+7PHAtG71DlGMQvhMBrYVZtEECX9S+/lJ\r\n"
                                                     + "3qawHn8t/0297ywJpPj8nQYqsUWQdCPoMmIHWcoSzqdy2v+0rAy+jsf4p6mmjKpw\r\n"
                                                     + "asg1h/BN2yHxzpWSEs0W7wFLEYvkcfJikxYQyyFe71zR26zG6bnbmLPKZNoLqw/C\r\n"
                                                     + "0mIORvRUxloQA7x0xcWmrzOFcbRn5mLOm0jR/VsyvIQ4hyIIi1Fw/mGqkOOj9KiP\r\n" + "bTf/\r\n"
                                                     + "-----END CERTIFICATE-----\r\n" + "";

    private X509Certificate     headCert, caCert, trustCert;

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
        caCert = getX509Certificate(ca.getBytes());
        trustCert = getX509Certificate(trusted.getBytes());

        certs.add(headCert);
        certs.add(caCert);
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
        X509Certificate intCertTest = CertificateUtil.getX509Certificate(ca.getBytes());
        X509Certificate trustCertTest = CertificateUtil.getX509Certificate(trusted.getBytes());

        assertEquals(headCertTest.getIssuerX500Principal(), headCert.getIssuerX500Principal());
        assertEquals(headCertTest.getSubjectX500Principal(), headCert.getSubjectX500Principal());
        assertEquals(intCertTest.getIssuerX500Principal(), caCert.getIssuerX500Principal());
        assertEquals(intCertTest.getSubjectX500Principal(), caCert.getSubjectX500Principal());
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
