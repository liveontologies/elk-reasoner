/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.reduction;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * The result of the transitive reduction for satisfiable indexed class
 * expression; it contains information about equivalent classes.
 * 
 * @param <I>
 *            the type of the indexed class expression representing this class
 */
public class TransitiveReductionOutputEquivalent<I extends IndexedClassExpression>
		extends TransitiveReductionOutput<I> {

	final Set<ElkClass> equivalent;

	TransitiveReductionOutputEquivalent(I root) {
		super(root);
		this.equivalent = new ArrayHashSet<ElkClass>(1);
	}

	public Set<ElkClass> getEquivalent() {
		return equivalent;
	}

	@Override
	public void accept(TransitiveReductionOutputVisitor<I> visitor) {
		visitor.visit(this);
	}
}
