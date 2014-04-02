/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

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

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.AbstractLocalDeterministicConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;

/**
 * An implementation of {@link DisjointSubsumer}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DisjointSubsumerImpl extends AbstractLocalDeterministicConclusion implements
		DisjointSubsumer {

	private final IndexedClassExpression member_;

	private final IndexedDisjointnessAxiom axiom_;

	public DisjointSubsumerImpl(IndexedDisjointnessAxiom axiom,
			IndexedClassExpression member) {
		this.axiom_ = axiom;
		this.member_ = member;
	}

	@Override
	public IndexedClassExpression getMember() {
		return member_;
	}

	@Override
	public IndexedDisjointnessAxiom getAxiom() {
		return axiom_;
	}

	@Override
	public String toString() {
		return axiom_.toString() + ":" + member_.toString();
	}

	@Override
	public <I, O> O accept(LocalDeterministicConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}