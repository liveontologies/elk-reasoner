package org.semanticweb.elk.matching.conclusions;

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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

public class IndexedObjectPropertyRangeAxiomMatch2
		extends
			AbstractIndexedAxiomMatch<IndexedObjectPropertyRangeAxiomMatch1> {

	private final ElkObjectProperty propertyMatch_;

	private final ElkClassExpression rangeMatch_;

	IndexedObjectPropertyRangeAxiomMatch2(
			IndexedObjectPropertyRangeAxiomMatch1 parent,
			ElkObjectProperty propertyMatch, ElkClassExpression rangeMatch) {
		super(parent);
		this.propertyMatch_ = propertyMatch;
		this.rangeMatch_ = rangeMatch;
	}

	public ElkObjectProperty getPropertyMatch() {
		return propertyMatch_;
	}

	public ElkClassExpression getRangeMatch() {
		return rangeMatch_;
	}

	@Override
	public <O> O accept(IndexedAxiomMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		IndexedObjectPropertyRangeAxiomMatch2 getIndexedObjectPropertyRangeAxiomMatch2(
				IndexedObjectPropertyRangeAxiomMatch1 parent,
				ElkObjectProperty propertyMatch, ElkClassExpression rangeMatch);

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

		O visit(IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch);

	}

}
