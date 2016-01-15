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
package org.praisenter.javafx;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.praisenter.resources.translations.Translations;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Helper class for generating alerts.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Alerts {
	/** Hidden constructor */
	private Alerts() {}
	
	/**
	 * Creates an alert for the given exception(s) with the given title, header and content.
	 * <p>
	 * The exceptions will have their stacktraces placed in an expandable area.
	 * <p>
	 * Ideally the header and content are descriptive enough for a user to make a decision on
	 * what to do, with the exception stacktraces there for sending to support.
	 * @param title the alert window title; if null, a generic message will be used
	 * @param header the alert's header section; if null, a generic message will be used
	 * @param content the alert's content section; if null, a generic message will be used
	 * @param exceptions the exception(s)
	 * @return Alert
	 */
	public static final Alert exception(
			String title,
			String header,
			String content,
			Exception... exceptions) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title == null ? Translations.getTranslation("error.alert.title") : title);
		alert.setHeaderText(header == null ? Translations.getTranslation("error.alert.message") : header);
		alert.setContentText(content == null ? Translations.getTranslation("error.alert.message") : content);

		// create expandable section with the exceptions in it
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		for (Exception ex : exceptions) {
			ex.printStackTrace(pw);
			pw.println();
			pw.println();
		}
		String exceptionText = sw.toString();

		Label label = new Label(Translations.getTranslation("error.alert.stacktrace"));

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(false);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.getDialogPane().setPrefWidth(550);
		
		return alert;
	}
}
