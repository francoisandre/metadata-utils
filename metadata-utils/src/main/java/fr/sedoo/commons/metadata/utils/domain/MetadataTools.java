package fr.sedoo.commons.metadata.utils.domain;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.geotoolkit.metadata.fra.FRA_DataIdentification;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultAddress;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.xml.XML;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Identification;

public final class MetadataTools {

	private static Map<String, Locale> localeMap = new HashMap<String, Locale>();
	private static DateFormat englishDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	static
	{
		String[] languages = Locale.getISOLanguages();
		for (String language : languages) {
			Locale locale = new Locale(language);
			localeMap.put(locale.getISO3Language(), locale);
		}
		//ISOLanguage renvoie FRA pour FR, alors que FRE est la vraie valeur...
		//TODO: Correct this patch
		Locale locale = new Locale("fr");
		localeMap.put("fre", locale);
	}
	
	private MetadataTools()
	{
		
	}

	public static DefaultCitation getCitation(Metadata metadata) 
	{
		DefaultCitation citation = null;
		DefaultDataIdentification identification = getFisrtIdentificationInfo(metadata);

		if (metadata.getIdentificationInfo().isEmpty())
		{
			citation = new DefaultCitation();
			identification.setCitation(citation);
			return citation;

		}
		else
		{
			Citation aux = identification.getCitation();
			if (aux instanceof DefaultCitation)
			{
				return (DefaultCitation) aux;
			}
			else
			{
				citation = new DefaultCitation();
				identification.setCitation(citation);
				return citation;
			}
		}
	}

	public static FRA_DataIdentification getFisrtIdentificationInfo(Metadata metadata) 
	{
		FRA_DataIdentification identification = null;

		if (metadata.getIdentificationInfo().isEmpty())
		{
			identification = new FRA_DataIdentification();
			metadata.setIdentificationInfo(new ArrayList<Identification>(Collections.singletonList(identification)));
			return identification;
		}
		else
		{
			DataIdentification aux = (DataIdentification) metadata.getIdentificationInfo().iterator().next();
			if (identification instanceof FRA_DataIdentification)
			{
				return (FRA_DataIdentification) aux;
			}
			else
			{
				identification = new FRA_DataIdentification(aux);
				metadata.setIdentificationInfo(new ArrayList<Identification>(Collections.singletonList(identification)));
				return identification;
			}
		}
	}
	
	public static Locale getLocaleFromISO3(String code)
	{
		return localeMap.get(code);
	}
	
	public static Date parseString(String string)
	{
		
		try
		{
			Date date = (Date)englishDateFormatter.parse(string);
			return date;
		}
		catch (Exception e)
		{
			//TODO: A gérer
		}
		return null;
	}

	public static String formatDate(Date date) 
	{
		return englishDateFormatter.format(date);
	}

	
	public static ResponsibleParty metadataContactToResponsibleParty(Contact metadataContact)
	{
		DefaultResponsibleParty responsibleParty = new DefaultResponsibleParty(Role.POINT_OF_CONTACT);
		responsibleParty.setIndividualName(metadataContact.getIndividualName());
		responsibleParty.setOrganisationName(new DefaultInternationalString(metadataContact.getOrganisationName()));
		DefaultContact contactInfo = new DefaultContact();
		DefaultAddress address = new DefaultAddress();
		address.setElectronicMailAddresses(Collections.singletonList(metadataContact.getEmailAddress()));
		contactInfo.setAddress(address);
		responsibleParty.setContactInfo(contactInfo);
		return responsibleParty;
	}
	
	public static Metadata fromXML(String xml) throws Exception
	{
		DefaultMetadata tmp = (DefaultMetadata) XML.unmarshal(xml);
		Metadata aux = new Metadata(tmp);
		return aux;
	}

	public static String toXML(Metadata metadata) throws Exception {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		XML.marshal(metadata, outputStream);
		String result = outputStream.toString();
		return prettyFormat(result,2);
	}

	public static String prettyFormat(String input, Integer indent) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        //transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}

	public static Metadata getEmptyMetadata() 
	{
			return new Metadata();
	}
	
	public static Summary toSummary(Metadata metadata)
	{
		Summary summary = new Summary();
		summary.setUuid(metadata.getUuid());
		summary.setResourceAbstract(metadata.getResourceAbstract());
		summary.setResourceTitle(metadata.getResourceTitle());
		return summary;
	}
}