package fr.sedoo.commons.metadata.utils.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.constraint.DefaultConstraints;
import org.geotoolkit.metadata.iso.distribution.DefaultDigitalTransferOptions;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.distribution.DigitalTransferOptions;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;

/**
 * @author F. Andre
 *
 */
public class Metadata extends DefaultMetadata
{
	private final String EMPTY_STRING ="";
	
	public final static Collection<ScopeCode> HIERARCHY_LEVEL = Collections.singletonList(ScopeCode.DATASET);
	
	public Metadata()  
	{
		super();
		initDefaultValues();
	}
	
	public Metadata(org.opengis.metadata.Metadata metadata)
	{
		super(metadata);
	}
	
	

	private void initDefaultValues() 
	{
		setHierarchyLevels(HIERARCHY_LEVEL);
		//loadMetadataContacts();
		Collection<ReferenceSystem> referenceSystemInfo = getReferenceSystemInfo();
	}
	
	public void setResourceTitle(String resourceTitle)
	{
		DefaultCitation citation = MetadataTools.getCitation(this);
		citation.setTitle(new SimpleInternationalString(StringUtils.trimToEmpty(resourceTitle)));
	}
	
	public String getResourceTitle()
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		return StringUtils.defaultString(dataIdentification.getCitation().getTitle().toString());
	}
	
	public String getResourceAbstract()
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		if (dataIdentification.getAbstract() == null)
		{
			return EMPTY_STRING;
		}
		return StringUtils.defaultString(dataIdentification.getAbstract().toString());
	}
	
	public void setResourceAbstract(String resourceTitle)
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		dataIdentification.setAbstract(new SimpleInternationalString(StringUtils.trimToEmpty(resourceTitle)));
	}

	public void setResourceDOI(String resourceDOI) 
	{
		DefaultCitation citation = MetadataTools.getCitation(this);
		citation.setIdentifiers(Collections.singletonList(new DOI(resourceDOI)));
	}
	
	public String getResourceDOI()
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<? extends Identifier> identifiers = dataIdentification.getCitation().getIdentifiers();
		if (identifiers.isEmpty())
		{
			return EMPTY_STRING;
		}
		else
		{
			return StringUtils.defaultString(identifiers.iterator().next().getCode());
		}
	}
	
	
	/**
	 * Retourne la liste des codes correspondants aux langages du <b>jeu de donnÃ©es<b/>
	 * @return
	 */
	public List<String> getResourceLanguages()
	{
		List<String> codes = new ArrayList<String>();
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Locale> languages = dataIdentification.getLanguages();
		if (languages == null)
		{
			return codes;
		}
		Iterator<Locale> iterator = languages.iterator();
		while (iterator.hasNext()) 
		{
			Locale locale = iterator.next();
			//TODO gérer proprement le cas FRE/FRA
			if (locale.getISO3Language().compareToIgnoreCase("fra")==0)
			{
				codes.add("fre");
			}
			else
			{
				codes.add(locale.getISO3Language());
			}
		}
		return codes;
	}
	
	/**
	 * Affecte la liste des codes correspondants aux langages du <b>jeu de données<b/>
	 * @return
	 */
	public void setResourceLanguages(List<String> codes)
	{
		List<Locale> languages = new ArrayList<Locale>();
		Iterator<String> iterator = codes.iterator();
		while (iterator.hasNext()) 
		{
			String code = iterator.next();
			Locale locale = MetadataTools.getLocaleFromISO3(code);
			languages.add(locale);
		}
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		dataIdentification.setLanguages(languages);
	
	}

	/**
	 * Affecte le code correspondant au langage de la fiche de <b>métadonnées<b/>
	 * @return
	 */
	
	public void setMetadataLanguage(String code)
	{
		Locale locale = MetadataTools.getLocaleFromISO3(code);
		setLanguage(locale);
	}
	
	/**
	 * Retourne le code correspondant au langage de la fiche de <b>métadonnées<b/>
	 * @return
	 */
	
	public String getMetadataLanguage()
	{
		if (getLanguage() == null)
		{
			return EMPTY_STRING;
		}
		else
		{
			return getLanguage().getISO3Language();
		}
	}
	
	public List<String> getKeywords()
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Keywords> descriptiveKeywords = dataIdentification.getDescriptiveKeywords();
		List<String> keywords = new ArrayList<String>();
		if (descriptiveKeywords != null)
		{
			Iterator<Keywords> iterator = descriptiveKeywords.iterator();
			while (iterator.hasNext()) {
				Keywords aux = iterator.next();
				String trimed = StringUtils.defaultString(aux.toString());
				if (trimed.length() != 0)
				{
					keywords.add(aux.toString());
				}
			}
		}
		return keywords;
	}
	
	public void setKeywords(List<String> keywords)
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Keywords> values = new ArrayList<Keywords>();
		
		Iterator<String> iterator = keywords.iterator();
		while (iterator.hasNext()) {
			String keyword = iterator.next();
			DefaultKeywords aux = new DefaultKeywords();
			DefaultInternationalString internationalString = new DefaultInternationalString(keyword);
			aux.setKeywords(Collections.singletonList(internationalString));
			values.add(aux);
		}
		
		dataIdentification.setDescriptiveKeywords(values);
	}
	
	public String getUseConditions()
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Constraints> resourceConstraints = dataIdentification.getResourceConstraints();
		if ((resourceConstraints == null) || (resourceConstraints.isEmpty()))
		{
			return EMPTY_STRING;
		}
		else
		{
			Constraints constraints = resourceConstraints.iterator().next();
			Collection<? extends InternationalString> useLimitations = constraints.getUseLimitations();
			if ((useLimitations == null) || (useLimitations.isEmpty()))
			{
				return EMPTY_STRING;
			}
			else
			{
				return useLimitations.iterator().next().toString();
			}
		}
	}
	
	
	public void setUseConditions(String useConditions)
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Constraints> resourceConstraints = dataIdentification.getResourceConstraints();
		if ((resourceConstraints == null))
		{
			resourceConstraints = new ArrayList<Constraints>();
			dataIdentification.setResourceConstraints(resourceConstraints);
		}
		
		DefaultConstraints constraints =  null;
		if (resourceConstraints.isEmpty())
		{
			constraints = new DefaultConstraints();
		}
		else
		{
			constraints = new DefaultConstraints(resourceConstraints.iterator().next());
			resourceConstraints.clear();
		}
		
		InternationalString value = new DefaultInternationalString(useConditions);
		constraints.setUseLimitations(Collections.singletonList(value));
		resourceConstraints.add(constraints);
	}
	
	public void setResourceURL(List<DescribedURL> urls)
	{
		DefaultDistribution distributionInfo = (DefaultDistribution) getDistributionInfo();
		if (distributionInfo == null)
		{
			distributionInfo = new  DefaultDistribution();
		}
		
		Collection<DigitalTransferOptions> transferOptions = distributionInfo.getTransferOptions();
		
		if (transferOptions == null)
		{
			transferOptions = new ArrayList<DigitalTransferOptions>();
			distributionInfo.setTransferOptions(transferOptions);
		}
		
		if (urls != null)
		{
			Iterator<DescribedURL> iterator = urls.iterator();
			while (iterator.hasNext()) 
			{
				DescribedURL url = iterator.next();
				DefaultDigitalTransferOptions currentTransferOptions = new DefaultDigitalTransferOptions();
				DefaultOnlineResource onlineResource = new DefaultOnlineResource();
				try {
					onlineResource.setLinkage(new URI(url.getLink()));
					onlineResource.setName(url.getLabel());
				} catch (URISyntaxException e) {
					// Ignored exception
				}
				currentTransferOptions.setOnLines(Collections.singletonList(onlineResource));
				transferOptions.add(currentTransferOptions);
			}
		}
		
		setDistributionInfo(distributionInfo);
	}
	
	public List<DescribedURL> getResourceURL()
	{
		List<DescribedURL> urls = new ArrayList<DescribedURL>();
		
		DefaultDistribution distributionInfo = (DefaultDistribution) getDistributionInfo();
		if (distributionInfo != null)
		{
			Collection<DigitalTransferOptions> transferOptions = distributionInfo.getTransferOptions();
			if (transferOptions != null)
			{
				Iterator<DigitalTransferOptions> iterator = transferOptions.iterator();
				while (iterator.hasNext()) {
					DigitalTransferOptions currentTransferOptions = iterator.next();
					Collection<? extends OnlineResource> onLines = currentTransferOptions.getOnLines();
					Iterator<? extends OnlineResource> onLinesterator = onLines.iterator();
					while (onLinesterator.hasNext()) 
					{
						OnlineResource onlineResource = (OnlineResource) onLinesterator.next();
						DescribedURL url = new DescribedURL(onlineResource.getLinkage().toString(), onlineResource.getName()); 
						urls.add(url);
					}
				}
			}
		}
		
		return urls;
	}

	
	/**
	 *  On considÃ¨re qu'une telle date est unique
	 *  Toute instance existante de type CI_DateTypeCode codeListValue="PUBLICATION" sera donc supprimÃ©e
	 *  @param publicationDate
	 */
	public void setPublicationDate(String publicationDateString) 
	{
		setTypedDate(publicationDateString, DateType.PUBLICATION);
	}
	
	
	public void setCreationDate(String creationDateString)
	{
		setTypedDate(creationDateString, DateType.CREATION);
	}
	
	public void setLastRevisionDate(String revisionDateString)
	{
		setTypedDate(revisionDateString, DateType.REVISION);
	}

	
	/**
	 *  On considÃ¨re qu'une telle date est unique (restriction par rapport Ã  la norme Inspire
	 *  La premiÃ¨re instance existante de type CI_DateTypeCode codeListValue="publication" sera donc renvoyÃ©e
	 *  @param publicationDate
	 */
	public String getPublicationDate()
	{
		return getTypedDate(DateType.PUBLICATION);
	}
	
	
	public void setTemporalExtent(String startDate, String endDate)
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Extent> extents = dataIdentification.getExtents();
		if (extents == null)
		{
			extents = new ArrayList<Extent>();
			dataIdentification.setExtents(extents);
		}
		DefaultExtent uniqueExtent = null;
		if (extents.isEmpty())
		{
			uniqueExtent = new DefaultExtent();
			extents.add(uniqueExtent);
		}
		else
		{
			uniqueExtent = new DefaultExtent(extents.iterator().next());
		}
		
//		TimePositionType position = new TimePositionType(Calendar.getInstance().getTime());
//		TimePeriodType period = new TimePeriodType(position);
		
		
        DefaultTemporalExtent uniqueTemporalExtent = new DefaultTemporalExtent(Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
//		TimePositionType begin = new TimePositionType();
//		begin.setFrame(startDate);
//		TimePeriodType period = new TimePeriodType();
//		period.setBeginPosition(begin);
//		period.setEndPosition(TimeIndeterminateValueType.NOW);
		
        
       // TimePeriod 
        
        DefaultTemporalExtent defExtent = new DefaultTemporalExtent();
		List<DefaultTemporalExtent> defExtList = new ArrayList<DefaultTemporalExtent>();
        
        DefaultPeriod timePeriod = new DefaultPeriod();
		timePeriod.setBegining(Calendar.getInstance().getTime());
		timePeriod.setEnding(Calendar.getInstance().getTime());
		
		defExtent.setExtent(timePeriod); //set TemporalExtent
		defExtList.add(defExtent);
        
		uniqueExtent.setTemporalElements(defExtList);
		dataIdentification.setExtents(Collections.singletonList(uniqueExtent));
		System.out.println(this.toString());
//		System.out.println("cocuuo");
		
		
	}
	
	
		
	public String getCreationDate()
	{
		return getTypedDate(DateType.CREATION);
	}
	
	public String getLastRevisionDate()
	{
		return getTypedDate(DateType.REVISION);
	}

	private String getTypedDate(DateType type) 
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		DefaultCitation citation = new DefaultCitation(dataIdentification.getCitation());

		Collection<CitationDate> dates = citation.getDates();
		Iterator<CitationDate> iterator = dates.iterator();
		while (iterator.hasNext()) {
			CitationDate citationDate = iterator.next();
			if (citationDate.getDateType().compareTo(type) == 0)
			{
				return MetadataTools.formatDate(citationDate.getDate());
			}
		}

		return EMPTY_STRING;
	}
	
	private void setTypedDate(String dateString, DateType type) 
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		DefaultCitation citation = (DefaultCitation) dataIdentification.getCitation();
		
		Collection<CitationDate> oldDates = citation.getDates();
		Collection<CitationDate> newDates = new ArrayList<CitationDate>();
		
		//On rajoute les dates n'Ã©tant pas de type PUBLICATION 
		Iterator<CitationDate> iterator = oldDates.iterator();
		while (iterator.hasNext()) {
			CitationDate citationDate = iterator.next();
			if (citationDate.getDateType().compareTo(type) != 0)
			{
				newDates.add(citationDate);
			}
		}
		
		DefaultCitationDate publicationDate = new DefaultCitationDate();
		publicationDate.setDate(MetadataTools.parseString(dateString));
		publicationDate.setDateType(type);
		newDates.add(publicationDate);
		citation.setDates(newDates);
		
	}

	public String getMetadataDate() 
	{
		return MetadataTools.formatDate(getDateStamp());
	}
	
	public void setMetadataDate(String dateString)
	{
		setDateStamp(MetadataTools.parseString(dateString));
	}

	
	
	public void setGeographicBoundingBox(DefaultGeographicBoundingBox box)
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Extent> extents = dataIdentification.getExtents();
		
		if (extents.isEmpty())
		{
				DefaultExtent uniqueExtent = new DefaultExtent();
				uniqueExtent.setGeographicElements(Collections.singletonList(box));
				dataIdentification.setExtents(Collections.singletonList(uniqueExtent));
		}
		
		else
		{
			Extent uniqueExtent = extents.iterator().next();
			if (uniqueExtent instanceof DefaultExtent)
			{
				((DefaultExtent) uniqueExtent).setGeographicElements(Collections.singletonList(box));
			}
			else
			{
				DefaultExtent aux = new DefaultExtent(uniqueExtent);
				aux.setGeographicElements(Collections.singletonList(box));
				dataIdentification.setExtents(Collections.singletonList(aux));
			}
		}
	}

	public DefaultGeographicBoundingBox getGeographicBoundingBox() 
	{
		DefaultDataIdentification dataIdentification =  MetadataTools.getFisrtIdentificationInfo(this);
		Collection<Extent> extents = dataIdentification.getExtents();
		
		if (extents.isEmpty())
		{
			return null;
		}
		else
		{
			Extent uniqueExtent = extents.iterator().next();
			Collection<? extends GeographicExtent> geographicElements = uniqueExtent.getGeographicElements();
			if (geographicElements.isEmpty())
			{
				return null;
			}
			else
			{
				GeographicExtent aux = geographicElements.iterator().next();
				if (aux instanceof DefaultGeographicBoundingBox)
				{
					return (DefaultGeographicBoundingBox) aux;
				}
				else
				{
					return null;
				}
			}
		}
	}
	
	public static String toXML(Metadata metadata)
	{
		return "coucou";
	}
	
	public String getUuid()
	{
		return getFileIdentifier();
	}
	
	public void setUuid(String value)
	{
		setFileIdentifier(value);
	}
	
	
	
	
}
