package org.semanticweb.elk.reasoner.saturation.conclusions.model;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;

/**
 * An {@link ObjectPropertyConclusion} stating that the given
 * {@link IndexedClassExpression} returned by {@link #getRange()} is a derived
 * range of the given {@link IndexedObjectProperty} returned by
 * {@link #getProperty()}. It is logically equivalent to a
 * {@code ElkObjectPropertyRangeAxiom} for the corresponding
 * {@code ElkClassExpression} and {@code ElkObjectProperty}.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public interface PropertyRange extends ObjectPropertyConclusion {

	public IndexedObjectProperty getProperty();

	public IndexedClassExpression getRange();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		PropertyRange getPropertyRange(IndexedObjectProperty property,
				IndexedClassExpression range);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		public O visit(PropertyRange conclusion);

	}

}
