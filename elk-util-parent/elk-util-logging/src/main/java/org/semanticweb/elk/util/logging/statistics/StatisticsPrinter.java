package org.semanticweb.elk.util.logging.statistics;
/*
 * #%L
 * ELK Utilities for Logging
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

public class StatisticsPrinter {
	/**
	 * the total width of the line to be printed
	 */
	private static final int FORMAT_WIDTH_ = 80;
	/**
	 * We are interested in substrings of the form "%***{***}*" the internal
	 * part in {***} will be the corresponding column heading parameter. The
	 * rest should be the formatter specifying how the values for this column
	 * are processed. Example: "%,{time}d"
	 */
	private static final Pattern REGEXP = Pattern
			.compile("(%[^\\{]*)\\{([^\\.\\}]*)\\}(.)");

	/**
	 * the logger used to print the statistics messages
	 */
	private final Logger logger_;

	/**
	 * specifies how the header will be formatted
	 */
	private final String headerFormat_;

	/**
	 * the header parameters used for formatting
	 */
	private final Object[] headerParams_;

	/**
	 * specifies how the values are formatted
	 */
	private final String valuesFormat_;

	/**
	 * how many symbols should be added to obtain the full width
	 * {@value #FORMAT_WIDTH_} assuming the value in the first column is empty;
	 * if it is not empty, the number of symbols added should be reduced by the
	 * value's length
	 */
	private final int maxPaddingWidth_;

	public StatisticsPrinter(Logger logger, String pattern,
			Object... sampleValues) {
		this.logger_ = logger;
		// used to find matches for regular expressions
		Matcher matcher = REGEXP.matcher(pattern);
		// used to use the found matches for the second time
		Matcher m = REGEXP.matcher(pattern);
		// header values to be extracted from the pattern
		List<String> headers = new LinkedList<String>();
		// used to build the formatting string for the headers
		StringBuffer headerFormatBuilder = new StringBuffer();
		// used to build the formatting string for the values
		StringBuffer lineFormatBuilder = new StringBuffer();
		// the formatted first sample value to be used in
		// the calculation of the padding
		String firstSampleValue = "";
		for (int i = 0; matcher.find(); i++) {
			m.find();
			// find the matching part in {}
			String header = matcher.group(2);
			headers.add(header);
			// formatting the corresponding sample value
			String formattedSample = String.format(
					matcher.group(1) + matcher.group(3), sampleValues[i]);
			if (i == 0) {
				firstSampleValue = formattedSample;
				// we do not need to set the width; it will be padded
				matcher.appendReplacement(lineFormatBuilder, matcher.group(1)
						+ matcher.group(3));
				m.appendReplacement(headerFormatBuilder, "%s");

			} else {
				// we set the width to the maximum of the width of the
				// column heading and the width of the formatted sample
				// so that all fits nicely
				int width = Math.max(header.length(), formattedSample.length());
				matcher.appendReplacement(lineFormatBuilder, matcher.group(1)
						+ width + matcher.group(3));
				m.appendReplacement(headerFormatBuilder, "%" + width + "s");
			}
		}
		matcher.appendTail(lineFormatBuilder);
		m.appendTail(headerFormatBuilder);

		this.headerFormat_ = headerFormatBuilder.toString();
		this.headerParams_ = headers.toArray();
		this.valuesFormat_ = lineFormatBuilder.toString();
		this.maxPaddingWidth_ = FORMAT_WIDTH_
				- String.format(valuesFormat_, sampleValues).length()
				+ firstSampleValue.length();
	}

	/**
	 * Prints a line separator
	 */
	public void printSeparator() {
		String separator = getString('-', FORMAT_WIDTH_);
		logger_.debug(separator);
	}

	/**
	 * Prints the heading together with the separators
	 */
	public void printHeader() {
		printSeparator();
		addPadding(' ', headerParams_);
		logger_.debug(String.format(headerFormat_, headerParams_));
		printSeparator();
	}

	/**
	 * Appends a string consisting of the given character to the first value so
	 * that when the values when formatted have in total {@value #FORMAT_WIDTH_}
	 * characters
	 * 
	 * @param c
	 * @param values
	 * @return
	 */
	Object[] addPadding(char c, Object... values) {
		String firstValue = values[0].toString();
		int paddingLength = maxPaddingWidth_ - firstValue.length();
		if (paddingLength > 0) {
			String padding = getString(c, paddingLength);
			values[0] = firstValue + padding;
		}
		return values;
	}

	/**
	 * Formats and the given values, adding padding symbols if necessary. The
	 * given array may be modified, but the values themselves are not modified.
	 * 
	 * @param values
	 *            the values to be printed
	 */
	public void print(Object... values) {
		addPadding('.', values);
		logger_.debug(String.format(valuesFormat_, values));
	}

	/**
	 * Creates a string of the given length consisting of the given character
	 * 
	 * @param c
	 * @param n
	 * @return
	 */
	static String getString(char c, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
}
