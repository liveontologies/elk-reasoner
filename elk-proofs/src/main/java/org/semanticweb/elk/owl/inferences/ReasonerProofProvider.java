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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.matching.Matcher;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class ReasonerProofProvider {

	private final Reasoner reasoner_;

	private final ElkObject.Factory elkFactory_;

	private final ElkInference.Factory inferenceFactory_ = new ElkInferenceBaseFactory();

	public ReasonerProofProvider(Reasoner reasoner,
			ElkObject.Factory elkFactory) {
		this.reasoner_ = reasoner;
		this.elkFactory_ = elkFactory;
	}

	public ElkInferenceSet getInferences(ElkAxiom axiom) {
		ModifiableElkInferenceSet result = new ModifiableElkInferenceSetImpl(
				elkFactory_);
		// support only class subsumptions for the moment
		if (!(axiom instanceof ElkSubClassOfAxiom)) {
			return result;
		}
		// else
		ElkSubClassOfAxiom subsumption = (ElkSubClassOfAxiom) axiom;
		try {
			ClassConclusion conclusion = reasoner_.getConclusion(subsumption);
			if (conclusion == null) {
				return result;
			}
			Matcher matcher = new Matcher(
					reasoner_.explainConclusion(conclusion), elkFactory_,
					result);
			if (conclusion instanceof SubClassInclusionComposed) {
				matcher.trace((SubClassInclusionComposed) conclusion);
			} else if (conclusion instanceof ClassInconsistency) {
				matcher.trace((ClassInconsistency) conclusion);
				result.produce(inferenceFactory_.getElkClassInclusionOwlNothing(
						subsumption.getSuperClassExpression()));
				result.produce(inferenceFactory_.getElkClassInclusionHierarchy(
						subsumption.getSubClassExpression(),
						elkFactory_.getOwlNothing(),
						subsumption.getSuperClassExpression()));
			}
		} catch (ElkException e) {
			throw new ElkRuntimeException(e);
		}
		return result;

	}

}
