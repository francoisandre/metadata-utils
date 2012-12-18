package fr.sedoo.commons.metadata.utils;


import junit.framework.Assert;

import org.junit.Test;

import fr.sedoo.commons.metadata.utils.domain.MetadataTools;

public class TestMetadataTools 
{
	
	@Test
	public void testDateFormatter() throws Exception 
	{
		String stringDate="2012-09-25";
		Assert.assertEquals("La date doit être reformatée à l'identique",stringDate, MetadataTools.formatDate(MetadataTools.parseString(stringDate)));
	}

		
}
