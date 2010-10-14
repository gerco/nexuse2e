
package org.nexuse2e.service.ws.aggateway.wsdl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for outboundData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="outboundData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processStep" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="messageId" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="xmlPayload" type="{urn:aggateway:names:ws:docexchange}xmlPayload" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "outboundData", propOrder = {
    "processStep",
    "messageId",
    "xmlPayload"
})
public class OutboundData {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String processStep;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String messageId;
    protected List<XmlPayload> xmlPayload;

    /**
     * Gets the value of the processStep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessStep() {
        return processStep;
    }

    /**
     * Sets the value of the processStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessStep(String value) {
        this.processStep = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the xmlPayload property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlPayload property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlPayload().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlPayload }
     * 
     * 
     */
    public List<XmlPayload> getXmlPayload() {
        if (xmlPayload == null) {
            xmlPayload = new ArrayList<XmlPayload>();
        }
        return this.xmlPayload;
    }

}
