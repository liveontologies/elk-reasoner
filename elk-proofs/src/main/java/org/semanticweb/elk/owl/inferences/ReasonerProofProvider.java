package org.semanticweb.elk.owl.inferences;

import java.util.List;

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
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DerivedClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class ReasonerProofProvider
		extends DummyElkAxiomVisitor<Void> {

	private final Reasoner reasoner_;

	private final ElkObject.Factory elkFactory_;

	private final ElkInference.Factory inferenceFactory_ = new ElkInferenceBaseFactory();
	
	private final ModifiableElkInferenceSet inferenceCache_;

	public ReasonerProofProvider(Reasoner reasoner,
			ElkObject.Factory elkFactory) {
		this.reasoner_ = reasoner;
		this.elkFactory_ = elkFactory;
		this.inferenceCache_ = new ModifiableElkInferenceSetImpl(elkFactory_);
	}

	public ElkInferenceSet getInferences(ElkAxiom axiom) {
		if (inferenceCache_.get(axiom).isEmpty()) {
			// compute
			axiom.accept(this);	
		}
		return inferenceCache_;		
	}

	@Override
	public Void defaultVisit(ElkAxiom axiom) {
		throw new ElkRuntimeException("Cannot generate proof for " + axiom);
	}

	@Override
	public Void visit(ElkSubClassOfAxiom axiom) {
		addInferencesForSubsumption(inferenceCache_, axiom.getSubClassExpression(),
				axiom.getSuperClassExpression());
		return null;
	}

	@Override
	public Void visit(ElkEquivalentClassesAxiom axiom) {
		List<? extends ElkClassExpression> equivalent = axiom
				.getClassExpressions();
		ElkClassExpression first = equivalent.get(equivalent.size() - 1);
		for (ElkClassExpression second : equivalent) {
			addInferencesForSubsumption(inferenceCache_, first, second);
			first = second;
		}
		inferenceCache_.produce(
				inferenceFactory_.getElkEquivalentClassesCycle(equivalent));
		return null;
	}
	
	public void clearInferenceCache() {
		inferenceCache_.clear();		
	}

	private void addInferencesForSubsumption(
			final ModifiableElkInferenceSet inferences,
			final ElkClassExpression subClass,
			final ElkClassExpression superClass) {
		if (superClass.equals(elkFactory_.getOwlThing())) {
			inferences.produce(
					inferenceFactory_.getElkClassInclusionOwlThing(subClass));
		}
		try {
			DerivedClassConclusionVisitor conclusionVisitor = new DerivedClassConclusionVisitor() {

				@Override
				public boolean inconsistentOwlThing(
						ClassInconsistency conclusion) throws ElkException {
					Matcher matcher = new Matcher(
							reasoner_.explainConclusion(conclusion),
							elkFactory_, inferences);
					matcher.trace(conclusion, elkFactory_.getOwlThing());
					return true;
				}

				@Override
				public boolean inconsistentIndividual(
						ClassInconsistency conclusion, ElkIndividual entity)
						throws ElkException {
					inferences.produce(inferenceFactory_
							.getElkClassInclusionOfInconsistentIndividual(
									entity));
					Matcher matcher = new Matcher(
							reasoner_.explainConclusion(conclusion),
							elkFactory_, inferences);
					matcher.trace(conclusion, entity);
					return true;
				}

				@Override
				public boolean inconsistentSubClass(
						ClassInconsistency conclusion) throws ElkException {
					inferences.produce(inferenceFactory_
							.getElkClassInclusionOwlNothing(superClass));
					inferences.produce(inferenceFactory_
							.getElkClassInclusionHierarchy(subClass,
									elkFactory_.getOwlNothing(), superClass));
					Matcher matcher = new Matcher(
							reasoner_.explainConclusion(conclusion),
							elkFactory_, inferences);
					matcher.trace(conclusion, subClass);
					return true;
				}

				@Override
				public boolean derivedClassInclusion(
						SubClassInclusionComposed conclusion)
						throws ElkException {
					Matcher matcher = new Matcher(
							reasoner_.explainConclusion(conclusion),
							elkFactory_, inferences);
					matcher.trace(conclusion, subClass, superClass);
					return true;
				}

			};
			reasoner_.visitDerivedConclusionsForSubsumption(subClass, superClass,
					conclusionVisitor);
		} catch (ElkException e) {
			throw new ElkRuntimeException(e);
		}
	}

}
