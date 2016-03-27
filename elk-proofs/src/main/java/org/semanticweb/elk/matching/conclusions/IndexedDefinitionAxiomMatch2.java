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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

public class IndexedDefinitionAxiomMatch2
		extends
			AbstractIndexedAxiomMatch<IndexedDefinitionAxiomMatch1> {

	private final ElkClass definedClassMatch_;

	private final ElkClassExpression definitionMatch_;

	IndexedDefinitionAxiomMatch2(IndexedDefinitionAxiomMatch1 parent,
			ElkClass definedClassMatch, ElkClassExpression definitionMatch) {
		super(parent);
		this.definedClassMatch_ = definedClassMatch;
		this.definitionMatch_ = definitionMatch;
	}

	public ElkClass getDefinedClassMatch() {
		return definedClassMatch_;
	}

	public ElkClassExpression getDefinitionMatch() {
		return definitionMatch_;
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

		IndexedDefinitionAxiomMatch2 getIndexedDefinitionAxiomMatch2(
				IndexedDefinitionAxiomMatch1 parent, ElkClass definedClassMatch,
				ElkClassExpression definitionMatch);

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

		O visit(IndexedDefinitionAxiomMatch2 conclusionMatch);

	}

}
