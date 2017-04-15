/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.slide;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.InvalidFormatException;
import org.praisenter.LockMap;
import org.praisenter.Tag;
import org.praisenter.UnknownFormatException;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;
import org.praisenter.xml.XmlIO;

/**
 * A collection of slides that has been created in Praisenter.
 * <p>
 * Obtain a {@link SlideLibrary} instance by calling the {@link #open(Path)}
 * static method. Only one instance should be created for each path. Multiple instances
 * modifying the same path can have unexpected results and can show different sets of slides.
 * <p>
 * This class is intended to be thread safe within this application but can still contend
 * with other programs during disk operations.
 * <p>
 * Since the slides are not bound to any graphics framework, thumbnails must be produced by the
 * caller and given to the {@link SlideLibrary} for saving.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideLibrary {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	// static

	/** The extension to use for the slide files */
	private static final String EXTENSION = ".xml";
	
	/** The directory used for slides when combined with other data */
	static final String ZIP_DIR = "slides";
	
	// instance
	
	/** The root path to the slide library */
	private final Path path;
	
	/** The slides */
	private final Map<UUID, Slide> slides;

	// locks
	
	/** The mutex locks */
	private final LockMap<String> locks;
	
	/**
	 * Sets up a new {@link SlideLibrary} at the given path.
	 * @param path the root path to the slide library
	 * @return {@link SlideLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final SlideLibrary open(Path path) throws IOException {
		SlideLibrary sl = new SlideLibrary(path);
		sl.initialize();
		return sl;
	}
	
	/**
	 * Full constructor.
	 * @param path the path to maintain the slide library
	 * @param thumbnailGenerator the class used to generate thumbnails for slides
	 */
	private SlideLibrary(Path path) {
		this.path = path;
		this.slides = new ConcurrentHashMap<UUID, Slide>();
		this.locks = new LockMap<String>();
	}
	
	/**
	 * Performs the initialization required by the slide library.
	 * @throws IOException if an IO error occurs
	 */
	private void initialize() throws IOException {
		LOGGER.debug("Initializing slide library at '{}'.", this.path);
		
		// verify paths exist
		Files.createDirectories(this.path);
		
		// index existing documents
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path)) {
			for (Path file : stream) {
				// only open files
				if (Files.isRegularFile(file)) {
					// only open xml files
					if (MimeType.XML.check(file)) {
						try (InputStream is = Files.newInputStream(file)) {
							try {
								// read in the xml
								BasicSlide slide = XmlIO.read(is, BasicSlide.class);
								slide.path = file;
								
								// we can't attempt generating thumbnails at this time
								// since it would rely on the caller's systems to be in place already
								// for example, JavaFX
								
								this.slides.put(slide.getId(), slide);
							} catch (Exception e) {
								LOGGER.warn("Failed to load slide '" + file.toAbsolutePath().toString() + "'", e);
							}
						} catch (Exception ex) {
							LOGGER.warn("Failed to load slide '" + file.toAbsolutePath().toString() + "'", ex);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns a lock for the given slide.
	 * @param slide the slide
	 * @return Object
	 */
	private Object getSlideLock(Slide slide) {
		return this.locks.get(slide.getId().toString());
	}

	/**
	 * Returns a lock for the given path file name.
	 * @param path the path
	 * @return Object
	 */
	private Object getPathLock(Path path) {
		return this.locks.get(path.getFileName().toString());
	}

	/**
	 * Returns the slide for the given id.
	 * <p>
	 * Returns null if not found or id is null.
	 * @param id the slide id
	 * @return {@link Slide}
	 */
	public Slide get(UUID id) {
		if (id == null) return null;
		return this.slides.get(id);
	}
	
	/**
	 * Returns all the slides in the library.
	 * @return List&lt;{@link Slide}&gt;
	 */
	public List<Slide> all() {
		return new ArrayList<Slide>(this.slides.values());
	}
	
	/**
	 * Returns the number of slides in the library.
	 * @return int
	 */
	public int size() {
		return this.slides.size();
	}
	
	/**
	 * Saves the given slide to the slide library.
	 * @param slide the slide to save
	 * @throws JAXBException if an error occurs writing the XML
	 * @throws IOException if an IO error occurs
	 */
	public void save(Slide slide) throws JAXBException, IOException {
		// calling this method could indicate one of the following:
		// 1. New
		// 2. Save Existing
		// 3. Save Existing + Rename
		
		// obtain the lock on the slide
		synchronized (this.getSlideLock(slide)) {
			LOGGER.debug("Saving slide '{}'.", slide.getName());
			
			// update the last modified date
			slide.setLastModifiedDate(Instant.now());
			
			// generate the file name and path
			String name = SlideLibrary.createFileName(slide);
			Path path = this.path.resolve(name + EXTENSION);
			Path uuid = this.path.resolve(StringManipulator.toFileName(slide.getId()) + EXTENSION);
			
			// check for operation
			if (!this.slides.containsKey(slide.getId())) {
				LOGGER.debug("Adding slide '{}'.", slide.getName());
				// then its a new
				synchronized (this.getPathLock(path)) {
					// check if the path exists once we obtain the lock
					if (Files.exists(path)) {
						// just use the UUID (which shouldn't need a lock since it's unique)
						path = uuid;
					}
					slide.setPath(path);
					XmlIO.save(path, slide);
					LOGGER.debug("Slide '{}' saved to '{}'.", slide.getName(), path);
				}
			} else {
				LOGGER.debug("Updating slide '{}'.", slide.getName());
				// it's an existing one
				Path original = slide.getPath();
				if (!original.equals(path)) {
					// obtain the desired path lock
					synchronized (this.getPathLock(path)) {
						// check if the path exists once we obtain the lock
						if (Files.exists(path)) {
							// is the original path the UUID path (which indicates that when it was imported
							// it had a file name conflict)
							if (original.equals(uuid)) {
								// if so, this isn't really a rename, just save it
								XmlIO.save(slide.getPath(), slide);
							} else {
								// if the path already exists and the current path isn't the uuid path
								// then we know that this was a rename to a different name that already exists
								LOGGER.warn("Unable to rename slide '{}' to '{}' because a file with that name already exists.", slide.getName(), path.getFileName());
								throw new FileAlreadyExistsException(path.getFileName().toString());
							}
						} else {
							LOGGER.debug("Renaming slide '{}' to '{}'.", slide.getName(), path.getFileName());
							// otherwise rename the file
							Files.move(original, path);
							slide.setPath(path);
							XmlIO.save(path, slide);
						}
					}
				} else {
					// it's a normal save
					XmlIO.save(slide.getPath(), slide);
				}
			}
			
			// update the slide map
			this.slides.put(slide.getId(), slide);
		}
	}
	
	/**
	 * Removes the slide from the library.
	 * @param slide the slide to remove
	 * @throws IOException if and IO error occurs
	 */
	public void remove(Slide slide) throws IOException {
		if (slide == null) return;
		
		UUID id = slide.getId();
		if (id == null) return;
		
		synchronized (this.getSlideLock(slide)) {
			LOGGER.debug("Removing slide '{}'.", slide.getName());
			// delete the file
			if (slide.getPath() != null) {
				Files.deleteIfExists(slide.getPath());
			}
			// remove it from the map
			this.slides.remove(id);
		}
	}

	/**
	 * Adds the given tag to the given slide and saves it.
	 * @param slide the slide
	 * @param tag the new tag
	 * @return boolean true if the tag was added successfully
	 * @throws JAXBException if the slide failed to save
	 * @throws IOException if an IO error occurs
	 */
	public boolean addTag(Slide slide, Tag tag) throws JAXBException, IOException {
		// obtain the lock for this slide item
		synchronized(this.getSlideLock(slide)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this slide was deleted
			// or renamed. the slide map will contain the latest 
			// object for us to update
			Slide latest = this.slides.get(slide.getId());
			// make sure the slide wasn't removed
			if (latest != null) {
				LOGGER.debug("Adding tag '{}' to slide '{}'.", tag, slide.getName());
				// see if adding the tag really does add it...
				boolean added = latest.getTags().add(tag);
				if (added) {
					try {
						XmlIO.save(slide.getPath(), slide);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save slide after adding tag '{}' to slide '{}'.", tag, slide.getName());
						// remove the tag due to not being able to save
						latest.getTags().remove(tag);
						// rethrow the exception
						throw ex;
					}
				}
				return added;
			}
			return false;			
		}
	}

	/**
	 * Adds the given tags to the given slide and saves it.
	 * @param slide the slide
	 * @param tags the new tags
	 * @return boolean true if the tags were added successfully
	 * @throws JAXBException if the slide failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean addTags(Slide slide, Collection<Tag> tags) throws JAXBException, IOException {
		// obtain the lock for this slide item
		synchronized(this.getSlideLock(slide)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this slide was deleted
			// or renamed. the slide map will contain the latest 
			// slide for us to update
			Slide latest = this.slides.get(slide.getId());
			// make sure the slide wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Adding tags '{}' to slide '{}'.", ts, slide.getName());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.getTags());
				// attempt to add all of the tags
				boolean added = latest.getTags().addAll(tags);
				if (added) {
					try {
						XmlIO.save(slide.getPath(), slide);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save slide after adding tags '{}' to slide '{}'.", ts, slide.getName());
						// reset to initial state
						latest.getTags().retainAll(old);
						// rethrow the exception
						throw ex;
					}
				}
				return added;
			}
			return false;
		}
	}
	
	/**
	 * Sets the given tags on the given slide and saves it.
	 * @param slide the slide
	 * @param tags the new tags
	 * @return boolean true if the tags were set successfully
	 * @throws JAXBException if the slide failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean setTags(Slide slide, Collection<Tag> tags) throws JAXBException, IOException {
		// obtain the lock for this slide item
		synchronized(this.getSlideLock(slide)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this slide was deleted
			// or renamed. the slide map will contain the latest 
			// slide for us to update
			Slide latest = this.slides.get(slide.getId());
			// make sure the slide wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Setting tags '{}' on slide '{}'.", ts, slide.getName());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.getTags());
				// attempt to set the tags
				boolean changed = latest.getTags().addAll(tags);
				changed |= latest.getTags().retainAll(tags);
				if (changed) {
					try {
						// attempt to save
						XmlIO.save(slide.getPath(), slide);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save slide after setting tags '{}' on slide '{}'.", ts, slide.getName());
						// reset to initial state
						latest.getTags().clear();
						latest.getTags().addAll(old);
						// rethrow the exception
						throw ex;
					}
				}
				return changed;
			}
			return false;
		}
	}
	
	/**
	 * Removes the given tag from the given slide and saves it.
	 * @param slide the slide
	 * @param tag the tag to remove
	 * @return boolean true if the tag was removed successfully
	 * @throws JAXBException if the slide failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTag(Slide slide, Tag tag) throws JAXBException, IOException {
		// obtain the lock for this slide item
		synchronized(this.getSlideLock(slide)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this slide was deleted
			// or renamed. the slide map will contain the latest 
			// slide for us to update
			Slide latest = this.slides.get(slide.getId());
			// make sure the slide wasn't removed
			if (latest != null) {
				LOGGER.debug("Removing tag '{}' from slide '{}'.", tag, slide.getName());
				boolean removed = latest.getTags().remove(tag);
				if (removed) {
					try {
						XmlIO.save(slide.getPath(), slide);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save slide after removing tag '{}' from slide '{}'.", tag, slide.getName());
						// reset to initial state
						latest.getTags().add(tag);
						// rethrow the exception
						throw ex;
					}
				}
				return removed;
			}
			return false;
		}
	}

	/**
	 * Removes the given tags from the given slide and saves it.
	 * @param slide the slide
	 * @param tags the tags to remove
	 * @return boolean true if the tags were removed successfully
	 * @throws JAXBException if the slide failed to save
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTags(Slide slide, Collection<Tag> tags) throws JAXBException, IOException {
		// obtain the lock for this slide item
		synchronized(this.getSlideLock(slide)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this slide was deleted
			// or renamed. the slide map will contain the latest 
			// slide for us to update
			Slide latest = this.slides.get(slide.getId());
			// make sure the slide wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Removing tags '{}' from slide '{}'.", ts, slide.getName());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.getTags());
				// attempt to set the tags
				boolean removed = latest.getTags().removeAll(tags);
				if (removed) {
					try {
						XmlIO.save(slide.getPath(), slide);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save slide after removing tags '{}' from slide '{}'.", ts, slide.getName());
						// reset to initial state
						latest.getTags().clear();
						latest.getTags().addAll(old);
						// rethrow the exception
						throw ex;
					}
				}
				return removed;
			}
			return false;
		}
	}

	/**
	 * Exports the given slides to the given file.
	 * @param path the file
	 * @param slides the slides to export
	 * @throws IOException if an IO error occurs
	 * @throws JAXBException if a slide cannot be written to XML
	 */
	public void exportSlides(Path path, List<Slide> slides) throws IOException, JAXBException {
		SlideExporter exporter = new PraisenterSlideExporter();
		exporter.execute(path, slides);
	}

	/**
	 * Exports the given slides to the given stream.
	 * @param stream the stream to write to
	 * @param slides the slides to export
	 * @throws IOException if an IO error occurs
	 * @throws JAXBException if a slide cannot be written to XML
	 */
	public void exportSlides(ZipOutputStream stream, List<Slide> slides) throws IOException, JAXBException {
		SlideExporter exporter = new PraisenterSlideExporter();
		exporter.execute(stream, ZIP_DIR, slides);
	}
	
	/**
	 * Imports the given slides into the library.
	 * @param path the path to a zip file
	 * @return List&lt;{@link Slide}&gt;
	 * @throws FileNotFoundException if the given path is not found
	 * @throws InvalidFormatException if the file wasn't in the format expected
	 * @throws UnknownFormatException if the format of the file couldn't be determined
	 * @throws JAXBException if an error occurs while reading XML
	 * @throws IOException if an IO error occurs
	 */
	public List<Slide> importSlides(Path path) throws FileNotFoundException, IOException, JAXBException, InvalidFormatException, UnknownFormatException {
		FormatIdentifingSlideImporter importer = new FormatIdentifingSlideImporter();
		List<Slide> slides = importer.execute(path);
		
		LOGGER.debug("'{}' slides found in '{}'.", slides.size(), path);
		Iterator<Slide> it = slides.iterator();
		while (it.hasNext()) {
			Slide slide = it.next();
			// update the import date
			if (slide instanceof BasicSlide) {
				BasicSlide s = (BasicSlide)slide;
				s.createdDate = Instant.now();
			}
			try {
				this.save(slide);
			} catch (Exception ex) {
				LOGGER.error("Failed to save the slide '" + slide.getName() + "'", ex);
				it.remove();
			}
		}
		return slides;
	}
	
	/**
	 * Returns true if any of the given media is in use on a slide.
	 * @param ids the media ids
	 * @return boolean
	 */
	public boolean isMediaReferenced(UUID... ids) {
		for (Slide slide : this.slides.values()) {
			if (slide.isMediaReferenced(ids)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Creates a file name for the given slide name.
	 * @param slide the slide
	 * @return String
	 */
	public static final String createFileName(Slide slide) {
		String name = slide.getName();
		if (name == null) {
			// just use the id
			name = slide.getId().toString().replaceAll("-", "");
		}
		
		// truncate the name to certain length
		int max = Constants.MAX_FILE_NAME_CODEPOINTS - EXTENSION.length();
		if (name.length() > max) {
			LOGGER.warn("File name too long '{}', truncating.", name);
			name = name.substring(0, Math.min(name.length() - 1, max));
		}
		
		return name;
	}
}
