/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferencePrinter implements TracedConclusionVisitor<String, Void> {

	public static String print(TracedConclusion conclusion) {
		return conclusion.acceptTraced(new InferencePrinter(), null);
	}
	
	@Override
	public String visit(InitializationSubsumer conclusion, Void parameter) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassOfSubsumer conclusion, Void parameter) {
		return "SubClassOf( " + conclusion.getPremise() + " " + conclusion.getExpression() + " )";
	}

	@Override
	public String visit(ComposedConjunction conclusion, Void parameter) {
		return "Conjuncting " + conclusion.getFirstConjunct() + " and " + conclusion.getSecondConjunct();

	}

	@Override
	public String visit(DecomposedConjunction conclusion, Void parameter) {
		return "Decomposing " + conclusion.getConjunction();

	}

	@Override
	public String visit(PropagatedSubsumer conclusion, Void parameter) {
		return "Existential inference from "
				+ conclusion.getPropagation() + " and "
				+ conclusion.getBackwardLink();
	}

	@Override
	public String visit(ReflexiveSubsumer conclusion, Void parameter) {
		return "Reflexive inference: owl:Thing => " + conclusion.getRelation() + " some owl:Thing";
	}

	@Override
	public String visit(ComposedBackwardLink conclusion, Void parameter) {
		BackwardLink bwLink = conclusion.getBackwardLink();
		ForwardLink fwLink = conclusion.getForwardLink();

		return "Property chain inference from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ReversedBackwardLink conclusion, Void parameter) {
		return "Reversing backward link " + conclusion.getSourceLink();
	}

	@Override
	public String visit(DecomposedExistential conclusion, Void parameter) {
		return "Creating backward link from " + conclusion.getExistential();
	}

	@Override
	public String visit(TracedPropagation conclusion, Void parameter) {
		return "Creating propagation from " + conclusion.getPremise();
	}

}
