package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;

@XmlSeeAlso({
	SongTextComponent.class
})
public interface TextPlaceholderComponent extends SlideRegion, SlideComponent, TextComponent {
	public static final String NAME_PRIMARY = "PRIMARY";
	public static final String NAME_SECONDARY = "SECONDARY";
	public static final String NAME_TERTIARY = "TERTIARY";
	
	public abstract String getName();
	public abstract void setName(String name);
}
