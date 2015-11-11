/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;

/**
 * An implementation of {@link DisjointSubsumer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DisjointSubsumerImpl extends AbstractClassConclusion implements
		DisjointSubsumer {

	private final IndexedClassExpressionList disjointExpressions_;
	
	private final int position_;

	private final ElkAxiom reason_;

	protected DisjointSubsumerImpl(IndexedContextRoot root,
			IndexedClassExpressionList disjointExpressions, int position, ElkAxiom reason) {
		super(root);
		this.disjointExpressions_ = disjointExpressions;
		this.position_ = position;
		this.reason_ = reason;
	}

	@Override
	public IndexedClassExpressionList getDisjointExpressions() {
		return disjointExpressions_;
	}
	
	@Override
	public int getPosition() {
		return position_;
	}

	@Override
	public ElkAxiom getReason() {
		return reason_;
	}

	@Override
	public <I, O> O accept(ClassConclusion.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public <I, O> O accept(DisjointSubsumer.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}