package nl.haarlem.translations.zdstozgw.utils;

import nl.haarlem.translations.zdstozgw.converter.ConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.invoke.MethodHandles;

public class XmlUtils {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static SOAPMessage stringToSoapMessage(String stringMessage) {
        SOAPMessage soapMessage = null;
        InputStream is = new ByteArrayInputStream(stringMessage.getBytes());
        try {
            soapMessage = MessageFactory.newInstance().createMessage(null, is);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return soapMessage;
    }

//    public static String soapMessageToSTring(SOAPMessage soapMessage) {
//        String stringMessage = "";
//        try {
//            ByteArrayOutputStream stream = getStringFromSOAP(soapMessage);
//            stringMessage = new String(stream.toByteArray(), StandardCharsets.UTF_8);
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//
//        return stringMessage;
//    }

    public static String xmlToString(Document xml) {
        String result = "";
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(xml), new StreamResult(writer));

            result = writer.getBuffer().toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    public static String getSoapMessageAsString(Document document) {
        return XmlUtils.xmlToString(document);
    }


    public static Document xmlNodesToDocument(NodeList nodes, String rootName) {
        Document result = null;
        try {
            result = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Element root = result.createElement(rootName);
        result.appendChild(root);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node copyNode = result.importNode(node, true);
            root.appendChild(copyNode);
        }
        return result;
    }

    public static String getSOAPMessageFromObject(Object object) throws ConverterException {

        try {
            Document document = marshalJAXBToXMLDocument(object);
            SOAPMessage message = getSoapMessage(document);
            return getStringFromSOAP(message);
		}
		catch(JAXBException jaxbe) {
			throw new ConverterException("SoapXml from object (JAXBException):" + jaxbe.getMessage(),  jaxbe);
		}
		catch(ParserConfigurationException pce) {
			throw new ConverterException("SoapXml from object (ParserConfigurationException):" + pce.getMessage(),  pce);
		}
		catch(TransformerException te) {
			throw new ConverterException("SoapXml from object (TransformerException):" + te.getMessage(),  te);
		} 
        catch (SOAPException se) {
        	throw new ConverterException("SoapXml from object (SOAPException):" + se.getMessage(),  se);
		} 
        catch (IOException ioe) {
        	throw new ConverterException("SoapXml from object (IOException):" + ioe.getMessage(),  ioe);
		}        
    }
    
    public static String getSOAPFaultMessageFromObject(QName faultcode, String faultstring, Object detail) {
		try {
			Document detailDocument = marshalJAXBToXMLDocument(detail);
			SOAPMessage message = getSoapFaultMessage(faultcode, faultstring, detailDocument);
			return getStringFromSOAP(message);
		}
		catch(Exception e) {
			log.error("kon niet het soapfault resulaat maken", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "kon niet het soapfault resulaat maken", e);
		}
    }

    private static String getStringFromSOAP(SOAPMessage message) throws SOAPException, IOException {
    	try {    	
	    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    	message.writeTo(out);    	
	    	var unformattedxml = new String(out.toByteArray());
	    	
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new InputSource(new StringReader(unformattedxml)));
	        
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	    	//initialize StreamResult with File object to save to file
	    	StreamResult result = new StreamResult(new StringWriter());
	    	DOMSource source = new DOMSource(doc);
	    	transformer.transform(source, result);
	    	String formattedxml = result.getWriter().toString();    	
	    	return formattedxml;
    	}
    	catch (ParserConfigurationException e) {
	    	throw new ConverterException("Kon de xml niet formetteren, ParserConfigurationException:" + e.toString(), e);
    	} catch (TransformerConfigurationException e) {
	    	throw new ConverterException("Kon de xml niet formetteren, TransformerConfigurationException:" + e.toString(), e);
    	} catch (TransformerFactoryConfigurationError e) {
	    	throw new ConverterException("Kon de xml niet formetteren, TransformerFactoryConfigurationError:" + e.toString(), e);
		} catch (SAXException e) {
	    	throw new ConverterException("Kon de xml niet formetteren, SAXException:" + e.toString(), e);
		} catch (TransformerException e) {
	    	throw new ConverterException("Kon de xml niet formetteren, TransformerException:" + e.toString(), e);
		}
    }

    private static Document marshalJAXBToXMLDocument(Object object) throws JAXBException, ParserConfigurationException, TransformerException {
        Document document = getNewDocument();
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        // pretty print
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(object, document);

//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//        StringWriter writer = new StringWriter();
//        transformer.transform(new DOMSource(document), new StreamResult(writer));
//        // Waarom volgende regel: marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
//        // String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
//        String output = writer.getBuffer().toString();
//        log.debug(output);

        return document;
    }

    private static Document getNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        db = dbf.newDocumentBuilder();
        return db.newDocument();
    }

    private static SOAPMessage getSoapMessage(Document document) throws SOAPException {
        //SOAP
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage message = mf.createMessage();
        SOAPPart part = message.getSOAPPart();
        SOAPEnvelope env = part.getEnvelope();
        SOAPBody body = env.getBody();

        body.addDocument(document);
        
        return message;
    }

    private static SOAPMessage getSoapFaultMessage(QName faultcode, String faultstring, Document detailDocument) throws SOAPException {
        //SOAP
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage message = mf.createMessage();
        SOAPPart part = message.getSOAPPart();
        SOAPEnvelope env = part.getEnvelope();
        SOAPBody body = env.getBody();

        SOAPFault fault = body.addFault();        
        fault.setFaultCode(faultcode);
        fault.setFaultString(faultstring);
        Detail detail = fault.addDetail();
        
        Node imported = detail.getOwnerDocument().importNode(detailDocument.getFirstChild(), true);
        detail.appendChild(imported);

        return message;
    }
    
    
    public static Object getStUFObject(String body, Class c) {
        Object object = null;
        try {
            object = JAXBContext.newInstance(c)
                    .createUnmarshaller()
                    .unmarshal(MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes())).getSOAPBody().extractContentAsDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
//
//    public static String getApplicicatieFromZender(String request) {
//        Document document = convertStringToDocument(request);
//        try {
//            return XPathFactory.newInstance().newXPath().evaluate("//*[local-name()='stuurgegevens']/*[local-name()='zender']/*[local-name()='applicatie']/text()", document);
//        } catch (XPathExpressionException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
