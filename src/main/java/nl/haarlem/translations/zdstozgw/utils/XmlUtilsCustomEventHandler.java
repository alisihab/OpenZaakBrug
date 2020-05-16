package nl.haarlem.translations.zdstozgw.utils;

import java.lang.invoke.MethodHandles;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtilsCustomEventHandler implements ValidationEventHandler{
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private Class c;
	private String  body;
	
      public XmlUtilsCustomEventHandler(String body, Class c) {
    	  this.body = body;
    	  this.c = c;
      }



	@Override
      public boolean handleEvent(ValidationEvent event) {
    	  log.info("Class:" + c.getCanonicalName() + " message:" + event.getMessage());  
    	  //if (event.getSeverity() == event.ERROR || event.getSeverity() == event.FATAL_ERROR)
    	  if (event.getSeverity() == event.FATAL_ERROR)
    	  {
    		  ValidationEventLocator locator = event.getLocator();
    		  throw new RuntimeException("Class:" + c.getCanonicalName() + " message:" + event.getMessage(), event.getLinkedException());
          }
          return true;
      }

}	