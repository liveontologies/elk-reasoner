package org.semanticweb.elk.owl.inferences;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;

public class ElkInferencePriner implements ElkInference.Visitor<String> {

	private static final String CONCLUSION_DELIM_ = " -| ";

	private static ElkInferencePriner INSTANCE_ = new ElkInferencePriner();
	private static final String PREMISE_DELIM_ = "; ";

	static ElkInference.Visitor<String> getVisitor() {
		return INSTANCE_;
	}

	public static String toString(ElkInference conclusion) {
		return conclusion.accept(INSTANCE_);
	}

	ElkObjectFactory factory_ = new ElkObjectFactoryImpl();

	private ElkInferencePriner() {

	}

	@Override
	public String visit(ElkClassInclusionExistentialFillerUnfolding inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getFirstPremise(factory_) + PREMISE_DELIM_
				+ inference.getSecondPremise(factory_);
	}

	@Override
	public String visit(ElkClassInclusionExistentialOfObjectHasSelf inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(
			ElkClassInclusionExistentialPropertyUnfolding inference) {
		String result = inference.getConclusion(factory_) + CONCLUSION_DELIM_;
		for (int i = 1; i <= inference.getExistentialPremiseCount(); i++) {
			result += inference.getExistentialPremise(i, factory_)
					+ PREMISE_DELIM_;
		}
		result += inference.getLastPremise(factory_);
		return result;
	}

	@Override
	public String visit(ElkClassInclusionHierarchy inference) {
		String result = inference.getConclusion(factory_) + CONCLUSION_DELIM_;
		int premiseCount = inference.getPremiseCount();
		for (int i = 1; i < premiseCount; i++) {
			result += inference.getPremise(i, factory_) + PREMISE_DELIM_;
		}
		result += inference.getPremise(premiseCount, factory_);
		return result;
	}

	@Override
	public String visit(
			ElkClassInclusionObjectIntersectionOfComposition inference) {
		String result = inference.getConclusion(factory_) + CONCLUSION_DELIM_;
		int premiseCount = inference.getPremiseCount();
		for (int i = 1; i < premiseCount; i++) {
			result += inference.getPremise(i, factory_) + PREMISE_DELIM_;
		}
		result += inference.getPremise(premiseCount, factory_);
		return result;
	}

	@Override
	public String visit(
			ElkClassInclusionObjectIntersectionOfDecomposition inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(ElkClassInclusionObjectUnionOfComposition inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(ElkClassInclusionOfEquivalence inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(ElkClassInclusionOfObjectPropertyDomain inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(ElkClassInclusionOfReflexiveObjectProperty inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(ElkClassInclusionOwlThing inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_;
	}

	@Override
	public String visit(ElkClassInclusionReflexivePropertyRange inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getFirstPremise(factory_) + PREMISE_DELIM_
				+ inference.getSecondPremise(factory_);
	}

	@Override
	public String visit(ElkClassInclusionTautology inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_;
	}

	@Override
	public String visit(ElkPropertyInclusionHierarchy inference) {
		String result = inference.getConclusion(factory_) + CONCLUSION_DELIM_;
		int premiseCount = inference.getPremiseCount();
		for (int i = 1; i < premiseCount; i++) {
			result += inference.getPremise(i, factory_) + PREMISE_DELIM_;
		}
		result += inference.getPremise(premiseCount, factory_);
		return result;
	}

	@Override
	public String visit(
			ElkPropertyInclusionOfTransitiveObjectProperty inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getPremise(factory_) + PREMISE_DELIM_;
	}

	@Override
	public String visit(ElkPropertyInclusionTautology inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_;
	}

	@Override
	public String visit(ElkPropertyRangePropertyUnfolding inference) {
		return inference.getConclusion(factory_) + CONCLUSION_DELIM_
				+ inference.getFirstPremise(factory_) + PREMISE_DELIM_
				+ inference.getSecondPremise(factory_);
	}

}
