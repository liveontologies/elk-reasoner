package org.semanticweb.elk.reasoner.tracing;

import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

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

public class DummyConclusionVisitor<O> implements Conclusion.Visitor<O> {

	protected O defaultVisit(
			@SuppressWarnings("unused") Conclusion conclusion) {
		return null;
	}

	protected O defaultVisit(ClassConclusion conclusion) {
		return defaultVisit((SaturationConclusion) conclusion);
	}

	protected O defaultVisit(ObjectPropertyConclusion conclusion) {
		return defaultVisit((SaturationConclusion) conclusion);
	}
	
	protected O defaultVisit(IndexedAxiom conclusion) {
		return defaultVisit((SaturationConclusion) conclusion);
	}

	@Override
	public O visit(ContextInitialization conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(Contradiction conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(DisjointSubsumer conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(SubContextInitialization conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(ForwardLink conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(BackwardLink conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(Propagation conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionComposed conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(SubClassInclusionDecomposed conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(SubPropertyChain conclusion) {
		return defaultVisit(conclusion);
	}

	@Override
	public O visit(IndexedDisjointClassesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedSubClassOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedDefinitionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedSubObjectPropertyOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedObjectPropertyRangeAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedDeclarationAxiom axiom) {
		return defaultVisit(axiom);
	}

}
