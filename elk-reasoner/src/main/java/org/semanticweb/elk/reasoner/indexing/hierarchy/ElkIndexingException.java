/*
 * #%L
 * ELK Reasoner
 * *
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;

/**
 * An exception to signal indexing problems
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkIndexingException extends ElkRuntimeException {

	private static final long serialVersionUID = -8678875783274619601L;

	protected ElkIndexingException() {
	}

	public ElkIndexingException(String message) {
		super(message);
	}

	public ElkIndexingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElkIndexingException(Throwable cause) {
		super(cause);
	}

}
