package org.praisenter.javafx.slide.editor;

import java.io.Serializable;

import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.media.MediaType;

import javafx.event.EventTarget;

final class MediaComponentAddEvent extends SlideComponentAddEvent implements Serializable {
	final MediaType mediaType;
	
	public MediaComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, MediaType mediaType) {
		this(source, target, component, mediaType, true, true);
	}
	
	public MediaComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, MediaType mediaType, boolean centered, boolean selected) {
		super(source, target, component, centered, selected);
		this.mediaType = mediaType;
	}
	
	public MediaType getMediaType() {
		return this.mediaType;
	}
}
