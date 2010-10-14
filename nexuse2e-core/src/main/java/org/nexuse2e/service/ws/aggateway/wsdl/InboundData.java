
package org.nexuse2e.service.ws.aggateway.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for inboundData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="inboundData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="businessProcess" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="processStep" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="partnerId" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="partnerType" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="conversationId" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="messageId" type="{urn:aggateway:names:ws:docexchange}aggatewayToken" minOccurs="0"/>
 *         &lt;element name="xmlPayload" type="{urn:aggateway:names:ws:docexchange}xmlPayload"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "inboundData", propOrder = {
    "businessProcess",
    "processStep",
    "partnerId",
    "partnerType",
    "conversationId",
    "messageId",
    "xmlPayload"
})
public class InboundData {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String businessProcess;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String processStep;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String partnerId;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String partnerType;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String conversationId;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String messageId;
    @XmlElement(required = true)
    protected XmlPayload xmlPayload;

    /**
     * Gets the value of the businessProcess property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBusinessProcess() {
        return businessProcess;
    }

    /**
     * Sets the value of the businessProcess property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBusinessProcess(String value) {
        this.businessProcess = value;
    }

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
     * Gets the value of the partnerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * Sets the value of the partnerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerId(String value) {
        this.partnerId = value;
    }

    /**
     * Gets the value of the partnerType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerType() {
        return partnerType;
    }

    /**
     * Sets the value of the partnerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerType(String value) {
        this.partnerType = value;
    }

    /**
     * Gets the value of the conversationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * Sets the value of the conversationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConversationId(String value) {
        this.conversationId = value;
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
     * @return
     *     possible object is
     *     {@link XmlPayload }
     *     
     */
    public XmlPayload getXmlPayload() {
        return xmlPayload;
    }

    /**
     * Sets the value of the xmlPayload property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlPayload }
     *     
     */
    public void setXmlPayload(XmlPayload value) {
        this.xmlPayload = value;
    }

}
