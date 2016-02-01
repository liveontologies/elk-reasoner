package org.semanticweb.elk.reasoner.tracing;

import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
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

public class ConclusionPrinter implements Conclusion.Visitor<String> {

	private static ConclusionPrinter INSTANCE_ = new ConclusionPrinter();

	public static String toString(Conclusion conclusion) {
		return conclusion.accept(INSTANCE_);
	}

	static Conclusion.Visitor<String> getPrinterVisitor() {
		return INSTANCE_;
	}

	private ConclusionPrinter() {

	}

	@Override
	public String visit(BackwardLink subConclusion) {
		return "BackwardLink(" + subConclusion.getDestination() + ":"
				+ subConclusion.getDestinationSubRoot() + ":"
				+ subConclusion.getTraceRoot() + ")";
	}

	@Override
	public String visit(ContextInitialization conclusion) {
		return "Init(" + conclusion.getDestination() + ")";
	}

	@Override
	public String visit(Contradiction conclusion) {
		return "Contradiction(" + conclusion.getDestination() + ")";
	}

	@Override
	public String visit(DisjointSubsumer conclusion) {
		return "DisjointSubsumer(" + conclusion.getDestination() + ":"
				+ conclusion.getDisjointExpressions() + ":"
				+ conclusion.getPosition() + "[" + conclusion.getReason()
				+ "])";
	}

	@Override
	public String visit(ForwardLink conclusion) {
		return "ForwardLink(" + conclusion.getDestination() + ":"
				+ conclusion.getForwardChain() + "->" + conclusion.getTarget()
				+ ")";
	}

	@Override
	public String visit(IndexedDeclarationAxiom conclusion) {
		return "IndexedDeclarationAxiom(" + conclusion.getEntity() + ")";
	}

	@Override
	public String visit(IndexedDefinitionAxiom conclusion) {
		return "IndexedDefinitionAxiom(" + conclusion.getDefinedClass() + " "
				+ conclusion.getDefinition() + ")";
	}

	@Override
	public String visit(IndexedDisjointClassesAxiom conclusion) {
		return "IndexedDisjointClassesAxiom(" + conclusion.getMembers() + ")";
	}

	@Override
	public String visit(IndexedObjectPropertyRangeAxiom conclusion) {
		return "IndexedObjectPropertyRangeAxiom(" + conclusion.getProperty()
				+ " " + conclusion.getRange() + ")";
	}

	@Override
	public String visit(IndexedSubClassOfAxiom conclusion) {
		return "IndexedSubClassOfAxiom(" + conclusion.getSubClass() + " "
				+ conclusion.getSuperClass() + ")";
	}

	@Override
	public String visit(IndexedSubObjectPropertyOfAxiom conclusion) {
		return "IndexedSubObjectPropertyOfAxiom("
				+ conclusion.getSubPropertyChain() + " "
				+ conclusion.getSuperProperty() + ")";
	}

	@Override
	public String visit(Propagation subConclusion) {
		return "Propagation(" + subConclusion.getDestination() + ":"
				+ subConclusion.getDestinationSubRoot() + ":"
				+ subConclusion.getCarry() + ")";
	}

	@Override
	public String visit(PropertyRange conclusion) {
		return "PropertyRange(" + conclusion.getProperty() + ": "
				+ conclusion.getRange() + ")";
	}

	@Override
	public String visit(SubClassInclusionComposed conclusion) {
		return "Subsumption+(" + conclusion.getDestination() + " "
				+ conclusion.getSuperExpression() + ")";
	}

	@Override
	public String visit(SubClassInclusionDecomposed conclusion) {
		return "Subsumption-(" + conclusion.getDestination() + " "
				+ conclusion.getSuperExpression() + ")";
	}

	@Override
	public String visit(SubContextInitialization subConclusion) {
		return "SubInit(" + subConclusion.getDestination() + ":"
				+ subConclusion.getDestinationSubRoot() + ")";
	}

	@Override
	public String visit(SubPropertyChain conclusion) {
		return "SubPropertyChain(" + conclusion.getSubChain() + " "
				+ conclusion.getSuperChain() + ")";
	}
}
