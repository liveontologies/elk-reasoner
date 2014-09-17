/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexedSubObjectPropertyOfAxiom<I extends IndexedObjectProperty> extends IndexedAxiom {

	protected final IndexedPropertyChain subProperty_;
	
	protected final I superProperty_;
	
	IndexedSubObjectPropertyOfAxiom(IndexedPropertyChain sub, I sup) {
		subProperty_ = sub;
		superProperty_ = sup;
	}
	
	public IndexedPropertyChain getSubProperty() {
		return subProperty_;
	}
	
	public IndexedObjectProperty getSuperProperty() {
		return superProperty_;
	}
	
	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment) {
		if (increment > 0) {
			subProperty_.addToldSuperObjectProperty(superProperty_);
			superProperty_.addToldSubPropertyChain(subProperty_);
		} else {
			subProperty_.removeToldSuperObjectProperty(superProperty_);
			superProperty_.removeToldSubObjectProperty(subProperty_);
		}
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean occurs() {
		// we do not cache property axioms
		return false;
	}

	@Override
	String toStringStructural() {
		return "SubObjectPropertyChain(" + subProperty_ + " " + superProperty_ + ")";
	}

}
