/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.protege;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Thread safety is ensured by accessing all the state variables from the AWT
 * event dispatching thread.
 * 
 * @author Peter Skocovsky
 */
public class LogController {

	private final JEditorPane editorPane_;

	private final JScrollPane scrollPane_;

	private Runnable onAppendWhenLogNotVisible_ = null;

	private Logger logger_ = null;

	private AppenderBase<ILoggingEvent> appender_ = null;

	private int characterLimit_ = 80000;

	private boolean logComponentShown_ = false;

	public LogController() {
		// @formatter:off
		editorPane_ = new JEditorPane("text/html", "<html>"
				+ "\n  <head>"
				+ "\n    <style type=\"text/css\">"
				+ "\n      body { font-family: Monospace; }"
				+ "\n      p { margin: 0; }"
				+ "\n      .WARN { color: red; }"
				+ "\n      .ERROR { color: red; }"
				+ "\n    </style>"
				+ "\n  </head>"
				+ "\n  <body>"
				+ "\n    <div id=\"records\">"
				+ "\n    </div>"
				+ "\n  </body>"
				+ "\n</html>");
		// @formatter:on
		editorPane_.setEditable(false);
		editorPane_.addComponentListener(editorPaneListener_);
		scrollPane_ = new JScrollPane(editorPane_);
	}

	private final ComponentListener editorPaneListener_ = new ComponentAdapter() {

		@Override
		public void componentHidden(final ComponentEvent e) {
			logComponentShown_ = false;
		}

		@Override
		public void componentShown(final ComponentEvent e) {
			logComponentShown_ = true;
		}

		public void componentResized(final ComponentEvent e) {
			scrollToBottom();
		};

	};

	private class PrivateAppender extends AppenderBase<ILoggingEvent> {

		@Override
		protected void append(final ILoggingEvent event) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final String message = formatLogRecord(event);

					final HTMLDocument htmlDocument = (HTMLDocument) editorPane_
							.getDocument();
					final Element recordsElement = htmlDocument
							.getElement("records");
					try {
						htmlDocument.insertBeforeEnd(recordsElement, message);
						ensureCharacterLimit();
					} catch (final BadLocationException e) {
						/*
						 * The exceptions should be printed, but if they are
						 * logged using the logger to which this appender is
						 * currently attached, it may result in an infinite
						 * loop. So we just print to stderr.
						 */
						e.printStackTrace();
					} catch (final IOException e) {
						e.printStackTrace();
					}

					if (!logComponentShown_
							&& onAppendWhenLogNotVisible_ != null) {
						onAppendWhenLogNotVisible_.run();
					}

					scrollToBottom();

				}
			});
		}

	}

	protected String formatLogRecord(final ILoggingEvent event) {
		// TODO: maybe it would be better to use
		// org.apache.commons.lang.StringEscapeUtils.escapeHtml
		return "<p class=\"" + event.getLevel().toString() + "\">"
				+ event.getFormattedMessage().replaceAll("&", "&amp;")
						.replaceAll("\"", "&quot;").replaceAll("<", "&lt;")
						.replaceAll(">", "&gt;").replaceAll("\n", "<br/>")
						.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
				+ "</p>";
	}

	/**
	 * Should be called from the AWT event dispatching thread!
	 */
	private void ensureCharacterLimit() {
		final HTMLDocument htmlDocument = (HTMLDocument) editorPane_
				.getDocument();
		final Element recordsElement = htmlDocument.getElement("records");
		while (htmlDocument.getLength() > characterLimit_
				&& recordsElement.getElementCount() > 1) {
			// Leave at least one element.
			htmlDocument.removeElement(recordsElement.getElement(0));
		}
	}

	public JComponent getLogComponent() {
		return scrollPane_;
	}

	public Logger getLogger() {
		return logger_;
	}

	public void setLogger(final Logger logger) {
		detachCurrentLogger();
		this.logger_ = logger;
		attachCurrentLogger();
	}

	private void detachCurrentLogger() {
		if (appender_ != null) {
			appender_.stop();
			if (logger_ != null) {
				logger_.detachAppender(appender_);
			}
			appender_ = null;
		}
	}

	private void attachCurrentLogger() {
		if (logger_ != null) {
			this.appender_ = new PrivateAppender();
			appender_.setContext(logger_.getLoggerContext());
			logger_.addAppender(appender_);
			appender_.start();
		}
	}

	/**
	 * Should be called from the AWT event dispatching thread!
	 * 
	 * @return
	 */
	public int getCharacterLimit() {
		return characterLimit_;
	}

	/**
	 * Should be called from the AWT event dispatching thread!
	 * 
	 * @param characterLimit
	 */
	public void setCharacterLimit(final int characterLimit) {
		if (characterLimit < 0) {
			throw new IllegalArgumentException(
					"characterLimit must be positive!");
		}
		this.characterLimit_ = characterLimit;
		ensureCharacterLimit();
	}

	public void setOnAppendWhenLogNotVisible(
			final Runnable onAppendWhenLogNotVisible) {
		this.onAppendWhenLogNotVisible_ = onAppendWhenLogNotVisible;
	}

	/**
	 * Should be called from the AWT event dispatching thread!
	 */
	private void scrollToBottom() {
		final JScrollBar vertical = scrollPane_.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

}
