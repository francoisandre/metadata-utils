package fr.sedoo.commons.metadata.utils.domain;

public class DescribedURL {
	
	private String link;
	private String label;
	
	public DescribedURL(String link, String label) {
		this.setLink(link);
		this.setLabel(label);
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
