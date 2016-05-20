package org.praisenter.javafx.slide;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.praisenter.Tag;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextPlaceholderComponent;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class ObservableSlide<T extends Slide> extends ObservableSlideRegion<T> implements Slide {
	
	final StringProperty name = new SimpleStringProperty();
	final ObjectProperty<Path> path = new SimpleObjectProperty<Path>();
	final LongProperty time = new SimpleLongProperty();
	
	final ObservableList<ObservableSlideComponent<?>> components = FXCollections.observableArrayList();
	final ObservableList<SlideAnimation> animations = FXCollections.observableArrayList();
	final ObservableSet<Tag> tags = FXCollections.observableSet();
	
	public ObservableSlide(T slide, PraisenterContext context, SlideMode mode) {
		super(slide, context, mode);
		
		// set initial values
		this.name.set(slide.getName());
		this.path.set(slide.getPath());
		this.time.set(slide.getTime());
		for (SlideComponent component : slide.getComponents(SlideComponent.class)) {
			this.components.add(this.createObservableFor(component));
		}
		for (SlideAnimation animation : slide.getAnimations()) {
			this.animations.add(animation);
		}
		for (Tag tag : slide.getTags()) {
			this.tags.add(tag);
		}
		
		// setup listeners
		this.name.addListener((obs, ov, nv) -> { slide.setName(nv); });
		this.path.addListener((obs, ov, nv) -> { slide.setPath(nv); });
		this.time.addListener((obs, ov, nv) -> { slide.setTime(nv.longValue()); });
		// FIXME need listeners for list properties
	}

	@Override
	public void fit(int width, int height) {
		this.region.fit(width, height);
		this.x.set(this.region.getX());
		this.y.set(this.region.getY());
		this.width.set(this.region.getWidth());
		this.height.set(this.region.getHeight());
	}
	
	private ObservableSlideComponent<?> createObservableFor(SlideComponent component) {
		// now create its respective observable one
		if (component instanceof MediaComponent) {
			return new ObservableMediaComponent((MediaComponent)component, this.context, this.mode);
		} else if (component instanceof DateTimeComponent) {
			return new ObservableDateTimeComponent((DateTimeComponent)component, this.context, this.mode);
		} else if (component instanceof TextPlaceholderComponent) {
			// FIXME implement
			throw new ClassCastException("Component type not implemented yet");
		} else if (component instanceof BasicTextComponent) {
			return new ObservableBasicTextComponent((BasicTextComponent)component, this.context, this.mode);
		} else {
			// TODO fix
			throw new ClassCastException("Component type not handled");
		}
	}
	
	// animations
	
	@Override
	public List<SlideAnimation> getAnimations(UUID id) {
		throw new UnsupportedOperationException();
	}
	
	public ObservableList<SlideAnimation> getAnimations() {
		return this.animations;
	}
	
	// components
	
	@Override
	public void addComponent(SlideComponent component) {
		// this sets the order, so must be done first
		this.region.addComponent(component);
		this.components.add(createObservableFor(component));
	}

	@Override
	public boolean removeComponent(SlideComponent component) {
		// remove the component
		if (this.region.removeComponent(component)) {
			this.components.removeIf(c -> c.getId().equals(component.getId()));
			// this operation may also remove animations
			this.animations.removeIf(a -> a.getId().equals(component.getId()));
			return true;
		}
		return false;
	}
	
	@Override
	public Iterator<SlideComponent> getComponentIterator() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <E extends SlideComponent> List<E> getComponents(Class<E> clazz) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void moveComponentDown(SlideComponent component) {
		// this will set the order of the components and sort them
		this.region.moveComponentDown(component);
		
		// now we need to reflect those changes in the observable objects
		
		// reset the orders
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.setOrder(comp.region.getOrder());
		}
		// resort the components list
		FXCollections.sort(this.components);
	}
	
	@Override
	public void moveComponentUp(SlideComponent component) {
		// this will set the order of the components and sort them
		this.region.moveComponentUp(component);
		
		// now we need to reflect those changes in the observable objects
		
		// reset the orders
		for (ObservableSlideComponent<?> comp : this.components) {
			comp.setOrder(comp.region.getOrder());
		}
		// resort the components list
		FXCollections.sort(this.components);
	}
	
	public ObservableList<ObservableSlideComponent<?>> getComponents() {
		return this.components;
	}

	// name
	
	@Override
	public String getName() {
		return this.name.get();
	}

	@Override
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}

	// path
	
	@Override
	public Path getPath() {
		return this.path.get();
	}

	@Override
	public void setPath(Path path) {
		this.path.set(path);
	}

	public ObjectProperty<Path> pathProperty() {
		return this.path;
	}

	// time
	
	@Override
	public long getTime() {
		return this.time.get();
	}

	@Override
	public void setTime(long time) {
		this.time.set(time);
	}
	
	public LongProperty timeProperty() {
		return this.time;
	}

	// tags
	
	@Override
	public ObservableSet<Tag> getTags() {
		return this.tags;
	}
	
	// others
	
	@Override
	public String getVersion() {
		return this.region.getVersion();
	}

	@Override
	public boolean hasPlaceholders() {
		return this.region.hasPlaceholders();
	}

	@Override
	public Slide copy() {
		throw new UnsupportedOperationException();
	}
}
