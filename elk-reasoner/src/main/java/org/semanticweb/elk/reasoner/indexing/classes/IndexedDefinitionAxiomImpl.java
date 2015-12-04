package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;

/**
 * Implements {@link IndexedDefinitionAxiom}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <A>
 *            the type of the defined class originates
 * @param <E>
 *            the type of the definition
 */
class IndexedDefinitionAxiomImpl<A extends ElkAxiom, C extends IndexedClass, D extends IndexedClassExpression>
		extends
			IndexedAxiomImpl<A>
		implements IndexedDefinitionAxiom {

	private final C definedClass_;
	private final D definition_;

	protected IndexedDefinitionAxiomImpl(A originalAxiom, C definedClass,
			D definition) {
		super(originalAxiom);
		this.definedClass_ = definedClass;
		this.definition_ = definition;
	}

	@Override
	public C getDefinedClass() {
		return this.definedClass_;
	}

	@Override
	public D getDefinition() {
		return this.definition_;
	}

	@Override
	public String toStringStructural() {
		return "EquivalentClasses(" + this.definedClass_ + ' '
				+ this.definition_ + ')';
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
