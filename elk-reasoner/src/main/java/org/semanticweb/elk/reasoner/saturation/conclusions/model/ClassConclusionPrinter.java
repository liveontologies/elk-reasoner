package org.semanticweb.elk.reasoner.saturation.conclusions.model;

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

public class ClassConclusionPrinter implements ClassConclusion.Visitor<String> {

	private static ClassConclusionPrinter INSTANCE_ = new ClassConclusionPrinter();

	public static String toString(ClassConclusion conclusion) {
		return conclusion.accept(INSTANCE_);
	}

	private ClassConclusionPrinter() {

	}

	@Override
	public String visit(BackwardLink subConclusion) {
		return "BackwardLink(" + subConclusion.getConclusionRoot() + ":"
				+ subConclusion.getConclusionSubRoot() + ":"
				+ subConclusion.getOriginRoot() + ")";
	}

	@Override
	public String visit(Propagation subConclusion) {
		return "Propagation(" + subConclusion.getConclusionRoot() + ":"
				+ subConclusion.getConclusionSubRoot() + ":"
				+ subConclusion.getCarry() + ")";
	}

	@Override
	public String visit(SubContextInitialization subConclusion) {
		return "SubInit(" + subConclusion.getConclusionRoot() + ":"
				+ subConclusion.getConclusionSubRoot() + ")";
	}

	@Override
	public String visit(SubClassInclusionComposed conclusion) {
		return "Subsumption+(" + conclusion.getConclusionRoot() + " "
				+ conclusion.getSuperExpression() + ")";
	}

	@Override
	public String visit(ContextInitialization conclusion) {
		return "Init(" + conclusion.getConclusionRoot() + ")";
	}

	@Override
	public String visit(Contradiction conclusion) {
		return "Contradiction(" + conclusion.getConclusionRoot() + ")";
	}

	@Override
	public String visit(SubClassInclusionDecomposed conclusion) {
		return "Subsumption-(" + conclusion.getConclusionRoot() + " "
				+ conclusion.getSuperExpression() + ")";
	}

	@Override
	public String visit(DisjointSubsumer conclusion) {
		return "DisjointSubsumer(" + conclusion.getConclusionRoot() + ":"
				+ conclusion.getDisjointExpressions() + ":"
				+ conclusion.getPosition() + "["
				+ conclusion.getReason() + "])";
	}

	@Override
	public String visit(ForwardLink conclusion) {
		return "ForwardLink(" + conclusion.getConclusionRoot() + ":"
				+ conclusion.getForwardChain() + "->" + conclusion.getTarget()
				+ ")";
	}
}
