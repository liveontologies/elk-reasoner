package org.semanticweb.elk.matching;

/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.matching.conclusions.IndexedClassExpressionMatch;
import org.semanticweb.elk.matching.conclusions.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;

/**
 * Exception thrown when to signal that {@link IndexedClassExpressionMatch} is
 * invalid, that is, the {@link IndexedClassExpression} in the match cannot be
 * obtained from the corresponding {@link ElkClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class ElkMatchException extends ElkRuntimeException {

	private static final long serialVersionUID = 7910516380668620176L;

	private ElkMatchException(IndexedObject expression, Object match) {
		super("Invalid match: " + expression + "::" + match);
	}

	public ElkMatchException(IndexedClassExpression expression,
			ElkClassExpression match) {
		this((IndexedObject) expression, match);
	}

	public ElkMatchException(IndexedObjectProperty expression,
			ElkObjectPropertyExpression match) {
		this((IndexedObject) expression, match);
	}

	public ElkMatchException(IndexedPropertyChain expression,
			ElkSubObjectPropertyExpression match, int startPos) {
		this((IndexedObject) expression,
				startPos == 0 ? match : match + "[" + startPos + 1 + "-]");
	}

	public ElkMatchException(IndexedContextRoot expression,
			IndexedContextRootMatch match) {
		this((IndexedObject) expression, match);
	}

	public ElkMatchException(IndexedClassExpression expression,
			ElkObjectIntersectionOf match, int prefixLength) {
		this((IndexedObject) expression,
				prefixLength == 0 ? match : match + "[0-" + prefixLength + "]");
	}

}
