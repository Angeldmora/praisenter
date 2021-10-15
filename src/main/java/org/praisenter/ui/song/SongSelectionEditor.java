package org.praisenter.ui.song;

import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Tag;
import org.praisenter.data.song.Author;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongBook;
import org.praisenter.ui.Action;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.controls.FormField;
import org.praisenter.ui.controls.FormFieldSet;
import org.praisenter.ui.controls.TagListView;
import org.praisenter.ui.controls.TextInputFieldEventFilter;
import org.praisenter.ui.document.DocumentContext;
import org.praisenter.ui.document.DocumentSelectionEditor;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class SongSelectionEditor extends VBox implements DocumentSelectionEditor<Song> {
	private static final String SONG_SELECTION_EDITOR_CSS = "p-song-selection-editor";
	private static final String SONG_SELECTION_EDITOR_SECTIONS_CSS = "p-song-selection-editor-sections";
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObjectProperty<DocumentContext<Song>> documentContext;
	private final BooleanProperty bulkEdit;
	
	private final ObjectProperty<Song> song;
	private final ObjectProperty<Object> selectedItem;

	private final StringProperty name;
	private final StringProperty source;
	private final StringProperty copyright;
	private final StringProperty ccliNumber;
	private final StringProperty released;
	private final StringProperty transposition;
	private final StringProperty tempo;
	private final StringProperty key;
	private final StringProperty variant;
	private final StringProperty publisher;
	private final StringProperty notes;
	private final StringProperty keywords;
	private final ObservableSet<Tag> tags;

	private final ObjectProperty<Author> selectedAuthor;
	private final StringProperty authorName;
	private final StringProperty authorType;
	
	private final ObjectProperty<SongBook> selectedSongBook;
	private final StringProperty songBookName;
	private final StringProperty songBookEntry;
	
	private final ObjectProperty<Lyrics> selectedLyrics;
	private final BooleanProperty lyricsOriginal;
	private final StringProperty lyricsLanguage;
	private final StringProperty lyricsTransliteration;
	private final StringProperty lyricsTitle;
	
	private final ObjectProperty<Section> selectedSection;
	private final StringProperty sectionName;
	private final StringProperty sectionText;
	
	public SongSelectionEditor(GlobalContext context) {
		this.getStyleClass().add(SONG_SELECTION_EDITOR_CSS);
		
		this.context = context;
		this.documentContext = new SimpleObjectProperty<>();
		this.bulkEdit = new SimpleBooleanProperty();
		
		this.song = new SimpleObjectProperty<>();
		this.selectedItem = new SimpleObjectProperty<>();
		
		this.name = new SimpleStringProperty();
		this.source = new SimpleStringProperty();
		this.copyright = new SimpleStringProperty();
		this.ccliNumber = new SimpleStringProperty();
		this.released = new SimpleStringProperty();
		this.transposition = new SimpleStringProperty();
		this.tempo = new SimpleStringProperty();
		this.key = new SimpleStringProperty();
		this.variant = new SimpleStringProperty();
		this.publisher = new SimpleStringProperty();
		this.notes = new SimpleStringProperty();
		this.keywords = new SimpleStringProperty();
		this.tags = FXCollections.observableSet(new HashSet<>());

		this.authorName = new SimpleStringProperty();
		this.authorType = new SimpleStringProperty();
		
		this.songBookName = new SimpleStringProperty();
		this.songBookEntry = new SimpleStringProperty();
		
		this.lyricsOriginal = new SimpleBooleanProperty();
		this.lyricsLanguage = new SimpleStringProperty();
		this.lyricsTransliteration = new SimpleStringProperty();
		this.lyricsTitle = new SimpleStringProperty();
		
		this.sectionName = new SimpleStringProperty();
		this.sectionText = new SimpleStringProperty();
		
		this.documentContext.addListener((obs, ov, nv) -> {
			this.song.unbind();
			this.selectedItem.unbind();
			this.bulkEdit.unbind();
			if (nv != null) {
				this.song.bind(nv.documentProperty());
				this.selectedItem.bind(nv.selectedItemProperty());
				this.bulkEdit.bind(nv.bulkEditProperty());
			} else {
				this.song.set(null);
				this.selectedItem.set(null);
				this.bulkEdit.set(false);
			}
		});
		
		this.song.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.name.unbindBidirectional(ov.nameProperty());
				this.source.unbindBidirectional(ov.sourceProperty());
				this.copyright.unbindBidirectional(ov.copyrightProperty());
				this.ccliNumber.unbindBidirectional(ov.ccliNumberProperty());
				this.released.unbindBidirectional(ov.releasedProperty());
				this.transposition.unbindBidirectional(ov.transpositionProperty());
				this.tempo.unbindBidirectional(ov.tempoProperty());
				this.key.unbindBidirectional(ov.keyProperty());
				this.variant.unbindBidirectional(ov.variantProperty());
				this.publisher.unbindBidirectional(ov.publisherProperty());
				this.notes.unbindBidirectional(ov.notesProperty());
				this.keywords.unbindBidirectional(ov.keywordsProperty());
				Bindings.unbindContentBidirectional(this.tags, ov.getTags());
				
				this.name.set(null);
				this.source.set(null);
				this.copyright.set(null);
				this.ccliNumber.set(null);
				this.released.set(null);
				this.transposition.set(null);
				this.tempo.set(null);
				this.key.set(null);
				this.variant.set(null);
				this.publisher.set(null);
				this.notes.set(null);
				this.keywords.set(null);
				this.tags.clear();
			}
			
			if (nv != null) {
				this.name.bindBidirectional(nv.nameProperty());
				this.source.bindBidirectional(nv.sourceProperty());
				this.copyright.bindBidirectional(nv.copyrightProperty());
				this.ccliNumber.bindBidirectional(nv.ccliNumberProperty());
				this.released.bindBidirectional(nv.releasedProperty());
				this.transposition.bindBidirectional(nv.transpositionProperty());
				this.tempo.bindBidirectional(nv.tempoProperty());
				this.key.bindBidirectional(nv.keyProperty());
				this.variant.bindBidirectional(nv.variantProperty());
				this.publisher.bindBidirectional(nv.publisherProperty());
				this.notes.bindBidirectional(nv.notesProperty());
				this.keywords.bindBidirectional(nv.keywordsProperty());
				Bindings.bindContentBidirectional(this.tags, nv.getTags());
			}
		});
		
		this.selectedAuthor = new SimpleObjectProperty<>();
		this.selectedAuthor.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.authorName.unbindBidirectional(ov.nameProperty());
				this.authorType.unbindBidirectional(ov.typeProperty());
				
				this.authorName.set(null);
				this.authorType.set(null);
			}
			
			if (nv != null) {
				this.authorName.bindBidirectional(nv.nameProperty());
				this.authorType.bindBidirectional(nv.typeProperty());
			}
		});
		
		this.selectedSongBook = new SimpleObjectProperty<>();
		this.selectedSongBook.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.songBookName.unbindBidirectional(ov.nameProperty());
				this.songBookEntry.unbindBidirectional(ov.entryProperty());
				
				this.songBookName.set(null);
				this.songBookEntry.set(null);
			}
			if (nv != null) {
				this.songBookName.bindBidirectional(nv.nameProperty());
				this.songBookEntry.bindBidirectional(nv.entryProperty());
			}
		});
		
		this.selectedLyrics = new SimpleObjectProperty<>();
		this.selectedLyrics.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.lyricsOriginal.unbindBidirectional(ov.originalProperty());
				this.lyricsLanguage.unbindBidirectional(ov.languageProperty());
				this.lyricsTransliteration.unbindBidirectional(ov.transliterationProperty());
				this.lyricsTitle.unbindBidirectional(ov.titleProperty());

				this.lyricsOriginal.set(false);
				this.lyricsLanguage.set(null);
				this.lyricsTitle.set(null);
				this.lyricsTransliteration.set(null);
			}
			if (nv != null) {
				this.lyricsOriginal.bindBidirectional(nv.originalProperty());
				this.lyricsLanguage.bindBidirectional(nv.languageProperty());
				this.lyricsTransliteration.bindBidirectional(nv.transliterationProperty());
				this.lyricsTitle.bindBidirectional(nv.titleProperty());
			}
		});
		
		this.selectedSection = new SimpleObjectProperty<>();
		this.selectedSection.addListener((obs, ov, nv) -> {
			if (ov != null) {
				this.sectionName.unbindBidirectional(ov.nameProperty());
				this.sectionText.unbindBidirectional(ov.textProperty());
				
				this.sectionName.set(null);
				this.sectionText.set(null);
			}
			if (nv != null) {
				this.sectionName.bindBidirectional(nv.nameProperty());
				this.sectionText.bindBidirectional(nv.textProperty());
			}
		});
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			// handle selection of a tree item
			this.selectedAuthor.set(null);
			this.selectedSongBook.set(null);
			this.selectedLyrics.set(null);
			this.selectedSection.set(null);

			if (nv == null || nv instanceof Song) {
				// do nothing
			} else if (nv instanceof Author) {
				this.selectedAuthor.set((Author)nv);
			} else if (nv instanceof SongBook) {
				this.selectedSongBook.set((SongBook)nv);
			} else if (nv instanceof Lyrics) {
				this.selectedLyrics.set((Lyrics)nv);
			} else if (nv instanceof Section) {
				this.selectedSection.set((Section)nv);
			} else if (nv instanceof Container) {
				// ignore
			} else {
				LOGGER.warn("The selected item was not a recognized type {}", nv.getClass());
			}
		});
		
		// UI
		
		TextField txtSongName = new TextField();
		txtSongName.textProperty().bindBidirectional(this.name);

		TextField txtSongSource = new TextField();
		txtSongSource.textProperty().bindBidirectional(this.source);

		TextField txtSongCopyright = new TextField();
		txtSongCopyright.textProperty().bindBidirectional(this.copyright);

		TextField txtSongCCLINumber = new TextField();
		txtSongCCLINumber.textProperty().bindBidirectional(this.ccliNumber);
		
		TextField txtSongReleased = new TextField();
		txtSongReleased.textProperty().bindBidirectional(this.released);
		
		TextField txtSongTransposition = new TextField();
		txtSongTransposition.textProperty().bindBidirectional(this.transposition);
		
		TextField txtSongTempo = new TextField();
		txtSongTempo.textProperty().bindBidirectional(this.tempo);
		
		TextField txtSongKey = new TextField();
		txtSongKey.textProperty().bindBidirectional(this.key);
		
		TextField txtSongVariant = new TextField();
		txtSongVariant.textProperty().bindBidirectional(this.variant);
		
		TextField txtSongPublisher = new TextField();
		txtSongPublisher.textProperty().bindBidirectional(this.publisher);
		
		TextField txtSongKeywords = new TextField();
		txtSongKeywords.textProperty().bindBidirectional(this.keywords);
		
		TextArea txtSongNotes = new TextArea();
		txtSongNotes.textProperty().bindBidirectional(this.notes);
		txtSongNotes.setWrapText(true);

		TagListView viewTags = new TagListView(this.context.getWorkspaceManager().getTagsUmodifiable());
		Bindings.bindContentBidirectional(viewTags.getTags(), this.tags);
		
		// author
		
		TextField txtAuthorName = new TextField();
		txtAuthorName.textProperty().bindBidirectional(this.authorName);
		
		TextField txtAuthorType = new TextField();
		txtAuthorType.textProperty().bindBidirectional(this.authorType);
		
		// songbook
		
		TextField txtSongBookName = new TextField();
		txtSongBookName.textProperty().bindBidirectional(this.songBookName);
		
		TextField txtSongBookEntry = new TextField();
		txtSongBookEntry.textProperty().bindBidirectional(this.songBookEntry);
		
		// lyrics
		
		CheckBox chkLyricsOriginal = new CheckBox();
		chkLyricsOriginal.selectedProperty().bindBidirectional(this.lyricsOriginal);
		
		TextField txtLyricsLanguage = new TextField();
		txtLyricsLanguage.textProperty().bindBidirectional(this.lyricsLanguage);
		
		TextField txtLyricsTransliteration = new TextField();
		txtLyricsTransliteration.textProperty().bindBidirectional(this.lyricsTransliteration);
		
		TextField txtLyricsTitle = new TextField();
		txtLyricsTitle.textProperty().bindBidirectional(this.lyricsTitle);
		
		// section
		
		TextField txtSectionName = new TextField();
		txtSectionName.textProperty().bindBidirectional(this.sectionName);
		
		TextArea txtSectionText = new TextArea();
		txtSectionText.textProperty().bindBidirectional(this.sectionText);
		txtSectionText.setWrapText(true);
		
		Button btnLyricsQuickEdit = new Button(Translations.get("action.edit.bulk"));
		
		btnLyricsQuickEdit.setOnAction(e -> {
			this.context.executeAction(Action.BULK_EDIT_BEGIN);
		});
		
		TextInputFieldEventFilter.applyTextInputFieldEventFilter(
				txtAuthorName,
				txtAuthorType,
				txtLyricsLanguage,
				txtLyricsTitle,
				txtLyricsTransliteration,
				txtSectionName,
				txtSectionText,
				txtSongBookEntry,
				txtSongBookName,
				txtSongCCLINumber,
				txtSongCopyright,
				txtSongKey,
				txtSongKeywords,
				txtSongName,
				txtSongNotes,
				txtSongPublisher,
				txtSongReleased,
				txtSongSource,
				txtSongTempo,
				txtSongTransposition,
				txtSongVariant);
		
		VBox boxGeneral = new VBox(
				new FormField(Translations.get("song.name"), Translations.get("song.name.description"), txtSongName),
				new FormField(Translations.get("song.source"), Translations.get("song.source.description"), txtSongSource),
				new FormField(Translations.get("song.copyright"), Translations.get("song.copyright.description"), txtSongCopyright),
				new FormField(Translations.get("song.ccli"), Translations.get("song.ccli.description"), txtSongCCLINumber),
				new FormField(Translations.get("song.released"), Translations.get("song.released.description"), txtSongReleased),
				new FormField(Translations.get("song.transposition"), Translations.get("song.transposition.description"), txtSongTransposition),
				new FormField(Translations.get("song.tempo"), Translations.get("song.tempo.description"), txtSongTempo),
				new FormField(Translations.get("song.key"), Translations.get("song.key.description"), txtSongKey),
				new FormField(Translations.get("song.variant"), Translations.get("song.variant.description"), txtSongVariant),
				new FormField(Translations.get("song.publisher"), Translations.get("song.publisher.description"), txtSongPublisher),
				new FormField(Translations.get("song.keywords"), Translations.get("song.keywords.description"), txtSongKeywords),
				new FormField(Translations.get("song.notes"), Translations.get("song.notes.description"), txtSongNotes),
				new FormField(Translations.get("song.tags"), Translations.get("song.tags.description"), viewTags));
		FormFieldSet pneGeneral = new FormFieldSet(Translations.get("song"), boxGeneral);
		
		VBox boxAuthor = new VBox(
				new FormField(Translations.get("song.lyrics.author.name"), Translations.get("song.lyrics.author.name.description"), txtAuthorName),
				new FormField(Translations.get("song.lyrics.author.type"), Translations.get("song.lyrics.author.type.description"), txtAuthorType));
		FormFieldSet pneAuthor = new FormFieldSet(Translations.get("song.lyrics.author"), boxAuthor);
		
		VBox boxSongBook = new VBox(
				new FormField(Translations.get("song.lyrics.songbook.name"), Translations.get("song.lyrics.songbook.name.description"), txtSongBookName),
				new FormField(Translations.get("song.lyrics.songbook.entry"), Translations.get("song.lyrics.songbook.entry.description"), txtSongBookEntry));
		FormFieldSet pneSongBook = new FormFieldSet(Translations.get("song.lyrics.songbook"), boxSongBook);
		
		VBox boxLyrics = new VBox(
				new FormField(Translations.get("song.lyrics.original"), Translations.get("song.lyrics.original.description"), chkLyricsOriginal),
				new FormField(Translations.get("song.lyrics.language"), Translations.get("song.lyrics.language.description"), txtLyricsLanguage),
				new FormField(Translations.get("song.lyrics.transliteration"), Translations.get("song.lyrics.transliteration.description"), txtLyricsTransliteration),
				new FormField(Translations.get("song.lyrics.title"), Translations.get("song.lyrics.title.description"), txtLyricsTitle),
				new FormField(Translations.get("song.lyrics.edit.bulk"), Translations.get("song.lyrics.edit.bulk.description"), btnLyricsQuickEdit));
		FormFieldSet pneLyrics = new FormFieldSet(Translations.get("song.lyrics"), boxLyrics);

		VBox boxSection = new VBox(
				new FormField(Translations.get("song.lyrics.section.name"), Translations.get("song.lyrics.section.name.description"), txtSectionName),
				new FormField(Translations.get("song.lyrics.section.text"), Translations.get("song.lyrics.section.text.description"), txtSectionText));
		FormFieldSet pneSection = new FormFieldSet(Translations.get("song.lyrics.section"), boxSection);
		
		VBox boxLayout = new VBox(
				pneGeneral,
				pneAuthor,
				pneSongBook,
				pneLyrics,
				pneSection);
		boxLayout.getStyleClass().add(SONG_SELECTION_EDITOR_SECTIONS_CSS);
		
		ScrollPane scroller = new ScrollPane(boxLayout);
		scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroller.setFitToWidth(true);
		
		this.getChildren().addAll(scroller);
		
		VBox.setVgrow(scroller, Priority.ALWAYS);
		
		// hide/show
		
		BooleanBinding hasSong = this.documentContext.isNotNull();
		pneGeneral.visibleProperty().bind(hasSong);
		pneGeneral.managedProperty().bind(hasSong);
		
		this.selectedItem.addListener((obs, ov, nv) -> {
			if (nv == null || nv instanceof Song) {
				pneGeneral.setExpanded(true);
			} else {
				pneGeneral.setExpanded(false);
			}
			
			if (nv != null && !(nv instanceof Song)) {
				pneAuthor.setExpanded(true);
				pneSongBook.setExpanded(true);
				pneLyrics.setExpanded(true);
				pneSection.setExpanded(true);
			}
		});
		
		ObjectBinding<Class<?>> selectedType = Bindings.createObjectBinding(() -> {
			Object item = this.selectedItem.get();
			if (item == null) return null;
			return item.getClass();
		}, this.selectedItem);
		
		pneAuthor.visibleProperty().bind(selectedType.isEqualTo(Author.class));
		pneAuthor.managedProperty().bind(pneAuthor.visibleProperty());
		
		pneSongBook.visibleProperty().bind(selectedType.isEqualTo(SongBook.class));
		pneSongBook.managedProperty().bind(pneSongBook.visibleProperty());
		
		pneLyrics.visibleProperty().bind(selectedType.isEqualTo(Lyrics.class));
		pneLyrics.managedProperty().bind(pneLyrics.visibleProperty());
		pneLyrics.disableProperty().bind(this.bulkEdit);
		
		pneSection.visibleProperty().bind(selectedType.isEqualTo(Section.class));
		pneSection.managedProperty().bind(pneSection.visibleProperty());
	}
	
	public DocumentContext<Song> getDocumentContext() {
		return this.documentContext.get();
	}
	
	public void setDocumentContext(DocumentContext<Song> ctx) {
		this.documentContext.set(ctx);
	}
	
	public ObjectProperty<DocumentContext<Song>> documentContextProperty() {
		return this.documentContext;
	}
}
