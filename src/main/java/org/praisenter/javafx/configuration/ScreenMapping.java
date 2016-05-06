package org.praisenter.javafx.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "screen")
@XmlAccessorType(XmlAccessType.NONE)
public final class ScreenMapping {
	@XmlAttribute(name = "id", required = false)
	private final String id;
	
	@XmlAttribute(name = "role", required = false)
	private final ScreenRole role;
	
	private ScreenMapping() {
		// for jaxb
		this.id = null;
		this.role = null;
	}
	
	public ScreenMapping(String id, ScreenRole role) {
		this.id = id;
		this.role = role;
	}

	public String getId() {
		return this.id;
	}

	public ScreenRole getRole() {
		return this.role;
	}
}
