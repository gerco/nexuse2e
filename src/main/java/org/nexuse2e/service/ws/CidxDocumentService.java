package org.nexuse2e.service.ws;

import javax.jws.WebParam;

/**
 * Interface for CIDX document receiver web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
public interface CidxDocumentService {

    /**
     * Process a CIDX document. The document string passed here contains all required
     * information (including routing information) that needs to be extracted by
     * subsequent processing steps.
     * @param document The CIDX document.
     */
    public void processCidxDocument(
            @WebParam(name = "document", targetNamespace = "")
            String document );
}
