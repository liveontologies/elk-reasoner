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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;

/**
 * Implements {@link IndexedSubObjectPropertyOfAxiom}
 * 
 * @author Yevgeny Kazakov
 *
 * @param <A>
 *            the type of the defined class originates
 * @param <C>
 *            the type of sub-property chain
 * @param <P>
 *            the type of super-property
 */
class IndexedSubObjectPropertyOfAxiomImpl<A extends ElkAxiom, C extends IndexedPropertyChain, P extends IndexedObjectProperty>
		extends
			IndexedAxiomImpl<A>
		implements IndexedSubObjectPropertyOfAxiom {

	private final C subPropertyChain_;

	private final P superProperty_;

	IndexedSubObjectPropertyOfAxiomImpl(A originalAxiom, C subPropertyChain,
			P superProperty) {
		super(originalAxiom);
		this.subPropertyChain_ = subPropertyChain;
		this.superProperty_ = superProperty;
	}

	@Override
	public final C getSubPropertyChain() {
		return this.subPropertyChain_;
	}

	@Override
	public final P getSuperProperty() {
		return this.superProperty_;
	}

	@Override
	public final <O> O accept(IndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
