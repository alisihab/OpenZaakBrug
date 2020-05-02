package nl.haarlem.translations.zdstozgw.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
			result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
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

	public static Document getDocument(String template) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			File f = new File(template);
			return builder.parse(f);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static String getSOAPMessageFromObject(Object object) {

		String result = "";
		try {
			Document document = marshalJAXBToXMLDocument(object);
			SOAPMessage message = getSoapMessage(document);
			result = getStringFromSOAP(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getStringFromSOAP(SOAPMessage message) throws SOAPException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		message.writeTo(out);
		return new String(out.toByteArray());
	}

	private static Document marshalJAXBToXMLDocument(Object object) throws JAXBException, ParserConfigurationException {
		Document document = getNewDocument();
		JAXBContext context = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(object, document);
		return document;
	}

	private static Document getNewDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		db = dbf.newDocumentBuilder();
		return db.newDocument();
	}

	private static SOAPMessage getSoapMessage(Document document) throws SOAPException {
		// SOAP
		MessageFactory mf = MessageFactory.newInstance();
		SOAPMessage message = mf.createMessage();
		SOAPPart part = message.getSOAPPart();
		SOAPEnvelope env = part.getEnvelope();
		SOAPBody body = env.getBody();
		body.addDocument(document);
		return message;
	}

	public static Object getStUFObject(String body, Class c) {
		Object object = null;
		try {
			object = JAXBContext.newInstance(c).createUnmarshaller().unmarshal(
					MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(body.getBytes()))
							.getSOAPBody().extractContentAsDocument());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}
