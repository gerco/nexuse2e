
package org.nexuse2e.service.ws.aggateway.wsdl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.nexuse2e.service.ws.aggateway.wsdl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _OutboundData_QNAME = new QName("urn:aggateway:names:ws:docexchange", "outboundData");
    private final static QName _InboundData_QNAME = new QName("urn:aggateway:names:ws:docexchange", "inboundData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.nexuse2e.service.ws.aggateway.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XmlPayload }
     * 
     */
    public XmlPayload createXmlPayload() {
        return new XmlPayload();
    }

    /**
     * Create an instance of {@link OutboundData }
     * 
     */
    public OutboundData createOutboundData() {
        return new OutboundData();
    }

    /**
     * Create an instance of {@link InboundData }
     * 
     */
    public InboundData createInboundData() {
        return new InboundData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OutboundData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:aggateway:names:ws:docexchange", name = "outboundData")
    public JAXBElement<OutboundData> createOutboundData(OutboundData value) {
        return new JAXBElement<OutboundData>(_OutboundData_QNAME, OutboundData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InboundData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:aggateway:names:ws:docexchange", name = "inboundData")
    public JAXBElement<InboundData> createInboundData(InboundData value) {
        return new JAXBElement<InboundData>(_InboundData_QNAME, InboundData.class, null, value);
    }

}
