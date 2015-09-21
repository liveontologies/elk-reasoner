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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;

/**
 * An implementation of {@link DisjointSubsumer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DisjointSubsumerImpl extends AbstractConclusion implements
		DisjointSubsumer {

	private final IndexedClassExpression member_;

	private final IndexedDisjointClassesAxiom axiom_;

	private final ElkAxiom reason_;

	public DisjointSubsumerImpl(IndexedContextRoot root,
			IndexedClassExpression member, IndexedDisjointClassesAxiom axiom,
			ElkAxiom reason) {
		super(root);
		this.member_ = member;
		this.axiom_ = axiom;
		this.reason_ = reason;
	}

	@Override
	public IndexedClassExpression getMember() {
		return member_;
	}

	@Override
	public IndexedDisjointClassesAxiom getAxiom() {
		return axiom_;
	}

	@Override
	public ElkAxiom getReason() {
		return reason_;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}