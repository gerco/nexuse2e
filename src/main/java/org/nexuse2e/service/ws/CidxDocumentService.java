package org.nexuse2e.service.ws;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Interface for CIDX document receiver web service.
 *
 * @author Jonas Reese
 * @version $LastChangedRevision:  $ - $LastChangedDate:  $ by $LastChangedBy:  $
 */
@WebService(name="CidxDocumentService", targetNamespace = "http://integration.nexuse2e.org")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CidxDocumentService {

    /**
     * Process a CIDX document. The document string passed here contains all required
     * information (including routing information) that needs to be extracted by
     * subsequent processing steps.
     * @param document The CIDX document.
     */
    @WebMethod(operationName = "processCidxDocument", action = "http://integration.nexuse2e.org/CidxDocumentService/processCidxDocument")
    @Oneway
    public void processCidxDocument(
            @WebParam(name = "document", targetNamespace = "")
            String document );
}
