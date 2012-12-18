package fr.sedoo.commons.metadata.utils.tools;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.apache.commons.digester3.Digester;

import fr.sedoo.commons.metadata.utils.domain.Summary;

public class SummaryUnmarshaller 
{
	 
	

	public static List<Summary> fromXML(String xml)
	{
		Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("csw:GetRecordsResponse",SummaryList.class);
        digester.addObjectCreate("*/csw:SummaryRecord",Summary.class);
        digester.addBeanPropertySetter("*/csw:SummaryRecord/dc:title","resourceTitle");
        digester.addBeanPropertySetter("*/csw:SummaryRecord/dct:abstract","resourceAbstract");
        digester.addBeanPropertySetter("*/csw:SummaryRecord/dc:identifier","identifier");
        digester.addSetNext("*/csw:SummaryRecord","add");
        try {
			SummaryList result = digester.parse(new ByteArrayInputStream(xml.getBytes()));
			return result;
		} catch (Exception e) 
		{
			return new SummaryList();// TODO Auto-generated catch block
		}
	}
	
}
