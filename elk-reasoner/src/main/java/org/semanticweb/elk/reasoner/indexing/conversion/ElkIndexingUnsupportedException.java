package org.semanticweb.elk.reasoner.indexing.conversion;
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

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.util.OwlObjectNameVisitor;

/**
 * This exception should be used to indicate that some {@link ElkObject} cannot
 * be represented within the index datastructure, that is, it is not supported
 * by the reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ElkIndexingUnsupportedException extends ElkIndexingException {

	private static final long serialVersionUID = -4575658482490999720L;

	protected ElkIndexingUnsupportedException() {
	}

	protected ElkIndexingUnsupportedException(String message) {
		super(message);
	}

	protected ElkIndexingUnsupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	protected ElkIndexingUnsupportedException(Throwable cause) {
		super(cause);
	}

	public ElkIndexingUnsupportedException(ElkObject elkObject) {
		this("ELK does not support " + OwlObjectNameVisitor.getName(elkObject)
				+ ".");
	}

	public ElkIndexingUnsupportedException(ElkObject elkObject, Throwable cause) {
		this("ELK does not support " + OwlObjectNameVisitor.getName(elkObject)
				+ ".", cause);
	}

}
