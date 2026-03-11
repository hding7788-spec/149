
package com.javaedu;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.javaedu package. 
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

    private final static QName _LicenseDischarge_QNAME = new QName("http://www.javaedu.com", "licenseDischarge");
    private final static QName _HelloResponse_QNAME = new QName("http://www.javaedu.com", "helloResponse");
    private final static QName _LicenseRequest_QNAME = new QName("http://www.javaedu.com", "licenseRequest");
    private final static QName _LicenseDischargeResponse_QNAME = new QName("http://www.javaedu.com", "licenseDischargeResponse");
    private final static QName _Hello_QNAME = new QName("http://www.javaedu.com", "hello");
    private final static QName _LicenseRequestResponse_QNAME = new QName("http://www.javaedu.com", "licenseRequestResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.javaedu
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LicenseDischarge }
     * 
     */
    public LicenseDischarge createLicenseDischarge() {
        return new LicenseDischarge();
    }

    /**
     * Create an instance of {@link LicenseDischargeResponse }
     * 
     */
    public LicenseDischargeResponse createLicenseDischargeResponse() {
        return new LicenseDischargeResponse();
    }

    /**
     * Create an instance of {@link LicenseRequest }
     * 
     */
    public LicenseRequest createLicenseRequest() {
        return new LicenseRequest();
    }

    /**
     * Create an instance of {@link Hello }
     * 
     */
    public Hello createHello() {
        return new Hello();
    }

    /**
     * Create an instance of {@link HelloResponse }
     * 
     */
    public HelloResponse createHelloResponse() {
        return new HelloResponse();
    }

    /**
     * Create an instance of {@link LicenseRequestResponse }
     * 
     */
    public LicenseRequestResponse createLicenseRequestResponse() {
        return new LicenseRequestResponse();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link LicenseDischarge }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.javaedu.com", name = "licenseDischarge")
    public JAXBElement<LicenseDischarge> createLicenseDischarge(LicenseDischarge value) {
        return new JAXBElement<LicenseDischarge>(_LicenseDischarge_QNAME, LicenseDischarge.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link HelloResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.javaedu.com", name = "helloResponse")
    public JAXBElement<HelloResponse> createHelloResponse(HelloResponse value) {
        return new JAXBElement<HelloResponse>(_HelloResponse_QNAME, HelloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link LicenseRequest }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.javaedu.com", name = "licenseRequest")
    public JAXBElement<LicenseRequest> createLicenseRequest(LicenseRequest value) {
        return new JAXBElement<LicenseRequest>(_LicenseRequest_QNAME, LicenseRequest.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link LicenseDischargeResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.javaedu.com", name = "licenseDischargeResponse")
    public JAXBElement<LicenseDischargeResponse> createLicenseDischargeResponse(LicenseDischargeResponse value) {
        return new JAXBElement<LicenseDischargeResponse>(_LicenseDischargeResponse_QNAME, LicenseDischargeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link Hello }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.javaedu.com", name = "hello")
    public JAXBElement<Hello> createHello(Hello value) {
        return new JAXBElement<Hello>(_Hello_QNAME, Hello.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link LicenseRequestResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.javaedu.com", name = "licenseRequestResponse")
    public JAXBElement<LicenseRequestResponse> createLicenseRequestResponse(LicenseRequestResponse value) {
        return new JAXBElement<LicenseRequestResponse>(_LicenseRequestResponse_QNAME, LicenseRequestResponse.class, null, value);
    }

}
