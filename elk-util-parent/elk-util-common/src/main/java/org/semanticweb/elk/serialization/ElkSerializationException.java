/*-
 * #%L
 * ELK Common Utilities
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.serialization;

import org.semanticweb.elk.exceptions.ElkException;

public class ElkSerializationException extends ElkException {
	private static final long serialVersionUID = -7493818210989472137L;

	public ElkSerializationException() {
		super();
	}

	public ElkSerializationException(final String message) {
		super(message);
	}

	public ElkSerializationException(final String message,
			final Throwable cause) {
		super(message, cause);
	}

	public ElkSerializationException(final Throwable cause) {
		super(cause);
	}

}
