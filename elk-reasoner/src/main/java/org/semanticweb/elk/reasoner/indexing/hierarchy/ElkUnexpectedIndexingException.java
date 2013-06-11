package org.semanticweb.elk.reasoner.indexing.hierarchy;
/*
 * #%L
 * ELK Reasoner
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

/**
 * An exception to signal incorrect indexing behavior.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkUnexpectedIndexingException extends ElkIndexingException {

	private static final long serialVersionUID = -6297215279078361253L;

	protected ElkUnexpectedIndexingException() {
	}

	public ElkUnexpectedIndexingException(String message) {
		super(message);
	}

	public ElkUnexpectedIndexingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ElkUnexpectedIndexingException(Throwable cause) {
		super(cause);
	}

}
