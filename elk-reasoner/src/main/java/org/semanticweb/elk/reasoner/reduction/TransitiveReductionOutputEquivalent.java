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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * The result of the transitive reduction for satisfiable indexed class
 * expression; it contains information about equivalent classes.
 * 
 * @param <R>
 *            the type of the root {@link IndexedClassExpression}s of the
 *            {@link TransitiveReductionJob}s for which this output is computed
 * 
 * @see TransitiveReductionJob
 */
public class TransitiveReductionOutputEquivalent<R extends IndexedClassExpression>
		extends TransitiveReductionOutput<R> {

	private final List<ElkClass> equivalent_;

	TransitiveReductionOutputEquivalent(R root, List<ElkClass> equivalent) {
		super(root);
		this.equivalent_ = equivalent;
	}

	public List<ElkClass> getEquivalent() {
		return equivalent_;
	}

	@Override
	public void accept(TransitiveReductionOutputVisitor<R> visitor) {
		visitor.visit(this);
	}
}
