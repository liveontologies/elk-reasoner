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
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;

/**
 * Implements {@link IndexedObjectPropertyRangeAxiom}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <A>
 *            the type of the defined class originates
 * @param <P>
 *            the type of the property
 * @param <C>
 *            the type of the range class
 */
class IndexedObjectPropertyRangeAxiomImpl<A extends ElkAxiom, P extends IndexedObjectProperty, C extends IndexedClassExpression>
		extends
			IndexedAxiomImpl<A>
		implements IndexedObjectPropertyRangeAxiom {

	private final P property_;

	private final C range_;

	IndexedObjectPropertyRangeAxiomImpl(A originalAxiom, P property, C range) {
		super(originalAxiom);
		this.property_ = property;
		this.range_ = range;
	}

	@Override
	public final P getProperty() {
		return this.property_;
	}

	@Override
	public final C getRange() {
		return this.range_;
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
