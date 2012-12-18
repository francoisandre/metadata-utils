package fr.sedoo.commons.metadata.utils;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sedoo.commons.metadata.utils.domain.DescribedURL;
import fr.sedoo.commons.metadata.utils.domain.Metadata;
import fr.sedoo.commons.metadata.utils.domain.MetadataTools;

/**
 * 
 * @author Francois ANDRE
 *
 */
public class TestMetadata
 
{
	String resourceTitle="resourceTitle";
	String resourceAbstract="resourceAbstract";
	String resourceDOI="10.1007/s00223-003-0070-0";
	List<String> resourceLanguageCodes = new ArrayList() {{ add("fre"); add("eng"); }};
	List<String> keywords = new ArrayList() {{ add("traitement de l'eau"); add("bassin versant"); }};
	List<DescribedURL> resourceURL= new ArrayList<DescribedURL>() {{ add(new DescribedURL("http://www.google.com", "google")); add(new DescribedURL("http://www.lemonde.fr", "lemonde")); }};
	String metadataLanguageCode="eng";
	String metadataDate="2013-10-26";
	String publicationDate="2012-09-25";
	String creationDate="2011-08-24";
	String lastRevisionDate="2010-07-23";
	double northBoundLatitude = 51.58;
	double southBoundLatitude = 41.56;
	double eastBoundLongitude = 8.49;
	double westBoundLongitude = -4.60;
	String startDate = "1995-01-22";
	DefaultGeographicBoundingBox boundingbox = new DefaultGeographicBoundingBox(westBoundLongitude, eastBoundLongitude, southBoundLatitude, northBoundLatitude);
	String metadataUseConditions = "useContions";
	
	final Logger logger =LoggerFactory.getLogger(TestMetadata.class);

	@Test
	public void testSetters() throws Exception 
	{
		Metadata metadata = getTestMetadata();
		Assert.assertEquals("La donnée resourceTitle doit être conservée",resourceTitle, metadata.getResourceTitle());
		Assert.assertEquals("La donnée resourceAbstract doit être conservée",resourceAbstract, metadata.getResourceAbstract());
		Assert.assertEquals("La donnée resourceDOI doit être conservée",resourceDOI, metadata.getResourceDOI());
		Assert.assertEquals("La donnée publicationDate doit être conservée",publicationDate, metadata.getPublicationDate());
		Assert.assertEquals("La donnée creationDate doit être conservée",creationDate, metadata.getCreationDate());
		Assert.assertEquals("La donnée lastRevisionDate doit être conservée",lastRevisionDate, metadata.getLastRevisionDate());
		Assert.assertEquals("La donnée metadataDate doit être conservée",metadataDate, metadata.getMetadataDate());
		Assert.assertEquals("La donnée boundingbox doit être conservée",boundingbox, metadata.getGeographicBoundingBox());
		Assert.assertTrue("La donnée resourceLanguageCodes doit être conservée", areStringListEqual(resourceLanguageCodes, metadata.getResourceLanguages()));
		Assert.assertTrue("La donnée resourceURL doit être conservée", areDescribedURLListEqual(resourceURL, metadata.getResourceURL()));
		Assert.assertEquals("La donnée metadataLanguageCode doit être conservée", metadataLanguageCode, metadata.getMetadataLanguage());
		Assert.assertEquals("La donnée metadataUseConditions doit être conservée", metadataUseConditions, metadata.getUseConditions());
		
		
		//Données initialisés
		Assert.assertEquals("La donnée hierachyLevels doit contenir une seule entrée",1, metadata.getHierarchyLevels().size());
		
		
	}

	private boolean areDescribedURLListEqual(List<DescribedURL> first, List<DescribedURL> second) 
	{
		if ((first == null) && (second == null))
		{
			return true;
		}
		if ((first != null) && (second == null))
		{
			return false;
		}
		if ((first == null) && (second != null))
		{
			return false;
		}
		if (first.size() != second.size())
		{
			return false;
		}	
		
		Iterator<DescribedURL> firstIterator = first.iterator();
		Iterator<DescribedURL> secondIterator = second.iterator();
		
		while (firstIterator.hasNext()) 
		{
			DescribedURL aux1 = firstIterator.next();
			DescribedURL aux2 = secondIterator.next();
			if (aux1.getLabel().compareTo(aux2.getLabel()) != 0)
			{
				return false;
			}
			
			if (aux1.getLink().compareTo(aux2.getLink()) != 0)
			{
				return false;
			}
		}
		
		return true;
	}

	
	public Metadata getTestMetadata() 
	{
		Metadata metadata = new Metadata();
		metadata.setResourceTitle(resourceTitle);
		metadata.setResourceAbstract(resourceAbstract);
		metadata.setResourceDOI(resourceDOI);
		metadata.setResourceLanguages(resourceLanguageCodes);
		metadata.setMetadataLanguage(metadataLanguageCode);
		metadata.setKeywords(keywords);
		metadata.setResourceURL(resourceURL);
		metadata.setPublicationDate(publicationDate);
		metadata.setCreationDate(creationDate);
		metadata.setLastRevisionDate(lastRevisionDate);
		metadata.setMetadataDate(metadataDate);
		metadata.setGeographicBoundingBox(boundingbox);
		metadata.setUseConditions(metadataUseConditions);
		metadata.setTemporalExtent(startDate, "");
		String result;
		try {
			result = MetadataTools.toXML(metadata);
			logger.debug("\n"+result);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return metadata;
	}


	@Test
	public void testInspireValidation() throws Exception
	{
		Metadata metadata = getTestMetadata();
		
		String result = MetadataTools.toXML(metadata);
		logger.debug("\n"+result);
		
//		HttpPost httpPost = new HttpPost("http://www.inspire-geoportal.eu/INSPIREValidatorService/resources/validation/inspire");
		HttpPost httpPost = new HttpPost("http://inspire-geoportal.ec.europa.eu/GeoportalProxyWebServices/resources/INSPIREResourceTester");
		//xml response: h
		httpPost.addHeader("Accept", "application/xml");
		//html response
		//httpPost.addHeader("Accept", "text/html");
		File tempFile = File.createTempFile("fan", "fan");
		FileUtils.writeStringToFile(tempFile, result);
		
		//ContentBody body = new StringBody(outputStream.toString());
		ContentBody body = new FileBody(tempFile);
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("dataFile", body);
		httpPost.setEntity(reqEntity);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpPost);
		String globalErrorMessage = InspireValidatorResponseParser.parseResponse(response);
		Assert.assertEquals("L'analyse Inspire ne doit pas renvoyer d'erreurs", "", globalErrorMessage);
		tempFile.delete();
	}
	
	private static boolean areStringListEqual(List<String> first, List<String> second)
	{
		if ((first == null) && (second == null))
		{
			return true;
		}
		if ((first != null) && (second == null))
		{
			return false;
		}
		if ((first == null) && (second != null))
		{
			return false;
		}
		if (first.size() != second.size())
		{
			return false;
		}	
		
		Iterator<String> firstIterator = first.iterator();
		Iterator<String> secondIterator = second.iterator();
		
		while (firstIterator.hasNext()) 
		{
			String aux1 = firstIterator.next();
			String aux2 = secondIterator.next();
			if (aux1.compareTo(aux2) != 0)
			{
				return false;
			}
		}
		
		return true;
			
	}
	
	@Test
	public void testUnMarshalling() throws Exception
	{
		TimePositionType position = new TimePositionType(Calendar.getInstance().getTime());
		TimePeriodType period = new TimePeriodType(position);
		
		System.out.println(period);
		
		/*String content = FileUtils.readFileToString(new File("C:/Users/francois/git/portailrbv/portailrbv-core/src/test/resources/samples/sample3.xml"));
		Metadata aux = MetadataTools.fromXML(content);
		System.out.println(aux);*/
	}
	
}
