package org.nexuse2e.patches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.patch.Patch;
import org.nexuse2e.patch.PatchException;
import org.nexuse2e.patch.PatchReporter;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.PartnerPojo;

/**
 * Renames equally named certificates for same partner to have a unique name.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public class RenameEquallyNamedCertificatesPatch implements Patch {

    private PatchReporter patchReporter;
    
    public void executePatch() throws PatchException {
        try {
            patchReporter.info( "Starting patch..." );
            EngineConfiguration config = Engine.getInstance().getCurrentConfiguration();
            List<PartnerPojo> partners = config.getPartners(org.nexuse2e.configuration.Constants.PARTNER_TYPE_ALL, null);
            int count = 0;
            for (PartnerPojo partner : partners) {
                patchReporter.info("Checking " + partner.getCertificates().size() + " certificates for partner " + partner.getName());
                Map<String, CertificatePojo> map = new HashMap<String, CertificatePojo>();
                for (CertificatePojo cert : partner.getCertificates()) {
                    if (cert.getName() != null) {
                        CertificatePojo existingCert = map.get(cert.getName());
                        if (existingCert != null) {
                            existingCert.setName(existingCert.getName() + "(" + existingCert.getNxId() + ")");
                            config.updateCertificate(existingCert);
                            count++;
                        }
                        map.put(cert.getName(), cert);
                    }
                }
            }

            patchReporter.info("Restarting engine...");
            Engine.getInstance().setCurrentConfiguration(config);
            patchReporter.info("Done");

            patchReporter.info("Patch executed successfully. " + count + " certificates updated.");
        } catch (NexusException nex) {
            throw new PatchException(nex);
        }
    }

    public String getVersionInformation() {
        return "1.1";
    }

    public String getPatchName() {
        return "Rename equally named certificates";
    }

    public String getPatchDescription() {
        return "Renames equally named certificates for same partner to have a unique name.";
    }

    public void setPatchReporter(PatchReporter patchReporter) {
        this.patchReporter = patchReporter;
    }

    public boolean isExecutedSuccessfully() {
        return false;
    }

}
