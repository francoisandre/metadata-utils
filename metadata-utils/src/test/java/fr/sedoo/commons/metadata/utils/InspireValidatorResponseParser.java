package fr.sedoo.commons.metadata.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InspireValidatorResponseParser 
{
	static final Logger logger =LoggerFactory.getLogger(InspireValidatorResponseParser.class);
	
		private InspireValidatorResponseParser()
		{
			
		}
	
		public static String parseResponse(HttpResponse response)
		{
			List<String> errorMessages = new ArrayList<String>();
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode) {
			//OK
			case 200:   //implement the below method to extract the response
				HttpEntity resEntity = response.getEntity();
				InputStream inputStream = null;
				Document document = null;
				try
				{
				inputStream = resEntity.getContent();
				String resultString = convertStreamToString(inputStream);
				SAXBuilder sxb = new SAXBuilder();
				document = sxb.build(new StringReader(resultString));
				}
				catch (Exception e)
				{
					errorMessages.add("Une exception est survenue : "+e.getMessage());
					return messageConcatenation(errorMessages);
				}
				Element rootElement = document.getRootElement();
				Iterator<Content> iterator = rootElement.getDescendants().iterator();
				boolean found = false;
				while (iterator.hasNext()) {
					Content content = (Content) iterator.next();
					if (content instanceof Element)
					{
						Element element = (Element) content;
						if (element.getName().compareToIgnoreCase("failed-assert")==0)
						{
							found = true;
						}
						if (element.getName().compareToIgnoreCase("text")==0)
						{
							if (found == true)
							{
								errorMessages.add(element.getTextTrim());
								found = false;
							}
						}
					}
				}
				
				break;
			default:    //implement the below method to handle errors such as internal server error
				//handleServerError(response);
				System.out.println("default");
				errorMessages.add("Une erreur "+statusCode+" est survenue.");
			}
			
			if (!errorMessages.isEmpty())
			{
				logger.debug("L'analyse INSPIRE a ramené les erreurs suivantes ("+errorMessages.size()+" erreur(s))");
				Iterator<String> iterator = errorMessages.iterator();
				while (iterator.hasNext()) 
				{
					logger.debug(iterator.next());
				}
			}
			
			return messageConcatenation(errorMessages);
		}
		
		private static String messageConcatenation(List<String> errorMessages)
		{
			StringBuilder concatenation = new StringBuilder();
			Iterator<String> iterator = errorMessages.iterator();
			while (iterator.hasNext()) 
			{
				concatenation.append(iterator.next());
				if (iterator.hasNext())
				{
					concatenation.append(" | ");
				}
			}
			return concatenation.toString().trim();
		}
		
		private static String convertStreamToString(InputStream is) throws IOException{
			if (is != null) {
				StringBuilder sb = new StringBuilder();
				String line;
				
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
				} finally {
					is.close();
				}
				return sb.toString();
			} else {
				return "";
			}
		}
}
