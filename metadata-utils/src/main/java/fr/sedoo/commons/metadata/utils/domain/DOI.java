package fr.sedoo.commons.metadata.utils.domain;

import org.geotoolkit.internal.simple.SimpleReferenceIdentifier;


public class DOI extends SimpleReferenceIdentifier{

	public final static String DOI_CODE_SPACE = "http://dx.doi.org"; 

	public DOI(String code)
	{
		super(DOI_CODE_SPACE, code);
	}

}
