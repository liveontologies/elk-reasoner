/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;

public class ElkUnsupportedOperationException extends ElkRuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2071488004689150749L;

	/**
	 * 
	 */
	public ElkUnsupportedOperationException() {
		super();
	}

	/**
	 */
	public ElkUnsupportedOperationException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	public ElkUnsupportedOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 */
	public ElkUnsupportedOperationException(Throwable cause) {
		super(cause);
	}
}
