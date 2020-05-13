package nl.haarlem.translations.zdstozgw.utils;

import java.lang.invoke.MethodHandles;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtilsCustomEventHandler implements ValidationEventHandler{
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

      @Override
      public boolean handleEvent(ValidationEvent event) {
    	  log.info(event.getMessage());  
    	  //if (event.getSeverity() == event.ERROR || event.getSeverity() == event.FATAL_ERROR)
    	  if (event.getSeverity() == event.FATAL_ERROR)
    	  {
    		  ValidationEventLocator locator = event.getLocator();
    		  throw new RuntimeException(event.getMessage(), event.getLinkedException());
          }
          return true;
      }

}	