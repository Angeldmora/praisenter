package org.praisenter.javafx.slide;

import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.Slide;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Pane;

final class SlideListCell extends ListCell<Slide> {
	private final ImageView graphic;
	private final Pane pane;
	
	public SlideListCell() {
		this.pane = new Pane();
		this.pane.setBackground(new Background(new BackgroundImage(Fx.TRANSPARENT_PATTERN, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
		
		this.graphic = new ImageView();
		this.graphic.setFitWidth(100);
		
		this.pane.getChildren().add(this.graphic);
		setGraphic(this.pane);
		
		this.setWrapText(true);
	}
	
	@Override
	protected void updateItem(Slide item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item == null || empty) {
			this.graphic.setImage(null);
			this.pane.setVisible(false);
			setText(null);
		} else {
			this.graphic.setImage(SwingFXUtils.toFXImage(item.getThumbnail(), null));
			this.pane.setVisible(true);
			setText(item.getName());
		}
	}
}