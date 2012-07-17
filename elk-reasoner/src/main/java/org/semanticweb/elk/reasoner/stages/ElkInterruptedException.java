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
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.owl.exceptions.ElkException;

/**
 * Thrown when the reasoner process has been interrupted
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkInterruptedException extends ElkException {

	private static final long serialVersionUID = 7774912476379495291L;

	public ElkInterruptedException() {
		super();
	}

	public ElkInterruptedException(String message) {
		super(message);
	}

	public ElkInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElkInterruptedException(Throwable cause) {
		super(cause);
	}

}
