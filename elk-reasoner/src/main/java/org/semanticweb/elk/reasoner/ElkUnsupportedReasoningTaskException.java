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


/**
 * Exception that is thrown when the reasoner is asked to perform a reasoning
 * task that is not supported.
 * 
 * 
 * @author Frantisek Simancik
 * 
 */
public class ElkUnsupportedReasoningTaskException extends UnsupportedOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2071488004689150749L;

	/**
	 * 
	 */
	public ElkUnsupportedReasoningTaskException() {
		super();
	}

	/**
	 */
	public ElkUnsupportedReasoningTaskException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	public ElkUnsupportedReasoningTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 */
	public ElkUnsupportedReasoningTaskException(Throwable cause) {
		super(cause);
	}

}
