/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.printers;

import java.io.IOException;

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Printing ELK Objects in OWL 2 functional style syntax.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class OwlFunctionalStylePrinter {

	/**
	 * Converting an ELK Object to string.
	 * 
	 * @param elkObject
	 *            the input ELK object
	 * @return the string representation of the ELK object
	 */
	public static String toString(ElkObject elkObject) {
		StringBuilder writer = new StringBuilder();
		OwlFunctionalStylePrinterVisitor printer = new OwlFunctionalStylePrinterVisitor(
				writer);
		elkObject.accept(printer);
		return writer.toString();
	}

	/**
	 * Printing an ELK Object through an appender.
	 * 
	 * @param appender
	 *            the appender used for printing
	 * @param elkObject
	 *            the ELK Object to print
	 * @throws IOException
	 *             if an I/O Error occurs
	 */
	public static void append(Appendable appender, ElkObject elkObject)
			throws IOException {
		OwlFunctionalStylePrinterVisitor printer = new OwlFunctionalStylePrinterVisitor(
				appender);
		try {
			elkObject.accept(printer);
		} catch (PrintingException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}

	}

}
