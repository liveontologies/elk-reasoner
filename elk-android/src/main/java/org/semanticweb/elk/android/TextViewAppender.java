package org.semanticweb.elk.android;

/*
 * #%L
 * ELK Android App
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.apache.log4j.Level;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

public class TextViewAppender extends WriterAppender {

	public static int DARK_ORANGE = Color.parseColor("#F88017");
	public static int GREEN = Color.parseColor("#008000");

	private final TextView textView_;

	public TextViewAppender(TextView textView) {
		super.setLayout(new SimpleLayout());
		this.textView_ = textView;
	}

	@Override
	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);
		final Spanned spannedMessage;
		switch (loggingEvent.getLevel().toInt()) {
		case Level.FATAL_INT:
			spannedMessage = wrap(message, Color.RED);
			break;
		case Level.ERROR_INT:
			spannedMessage = wrap(message, Color.RED);
			break;
		case Level.WARN_INT:
			spannedMessage = wrap(message, DARK_ORANGE);
			break;
		case Level.INFO_INT:
			spannedMessage = wrap(message, Color.BLUE);
			break;

		case Level.DEBUG_INT:
			spannedMessage = wrap(message, GREEN);
			break;

		case Level.TRACE_INT:
			spannedMessage = wrap(message, Color.GRAY);
			break;

		default:
			spannedMessage = wrap(message, Color.BLACK);
		}

		textView_.post(new Runnable() {
			@Override
			public void run() {
				textView_.append(spannedMessage);
			}
		});
	}

	private Spanned wrap(String message, int color) {
		SpannableString spanned = new SpannableString(message);
		ForegroundColorSpan fcs = new ForegroundColorSpan(color);
		spanned.setSpan(fcs, 0, message.length() - 1,
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return spanned;
	}

}
