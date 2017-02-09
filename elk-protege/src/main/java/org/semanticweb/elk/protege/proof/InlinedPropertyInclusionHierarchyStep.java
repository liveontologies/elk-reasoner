package org.semanticweb.elk.protege.proof;

/*-
 * #%L
 * ELK OWL API Binding
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

import java.util.AbstractList;

import org.liveontologies.puli.Inference;
import org.liveontologies.puli.ProofNode;
import org.liveontologies.puli.ProofStep;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionHierarchy;
import org.semanticweb.elk.owl.inferences.ElkPropertyInclusionHierarchy;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owlapi.proofs.ElkOwlInference;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An {@link ProofStep} resulting from rewriting nested
 * {@link ElkClassInclusionHierarchy} inferences into one
 * {@link ElkClassInclusionHierarchy} inference.
 * 
 * For example the sequence of inferences
 * 
 * <pre>
 *       B ⊑ C    C ⊑ D
 *       ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 * A ⊑ B     B ⊑ D
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *       A ⊑ D
 * </pre>
 *
 * is replaced with one inference
 * 
 * <pre>
 * A ⊑ B   B ⊑ C   C ⊑ D
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         A ⊑ D
 * </pre>
 * 
 * The replacement is done provided there is only one inference deriving each
 * premise of the inference and it is {@link ElkClassInclusionHierarchy}.
 * 
 * @author Yevgeny Kazakov
 */
class InlinedPropertyInclusionHierarchyStep extends
		AbstractFlattenedInclusionHierarchyStep implements ProofStep<OWLAxiom> {

	public final static String NAME = ElkPropertyInclusionHierarchy.NAME;

	private InlinedPropertyInclusionHierarchyStep(ProofStep<OWLAxiom> step) {
		super(step);
	}

	static ProofStep<OWLAxiom> convert(ProofStep<OWLAxiom> step) {
		if (!canConvertStep(step)) {
			return null;
		}
		// else
		ProofStep<OWLAxiom> result = new InlinedPropertyInclusionHierarchyStep(
				step);
		if (result.getPremises().size() == step.getPremises().size()) {
			result = step;
		}
		return result;
	}

	static boolean canConvertStep(ProofStep<OWLAxiom> step) {
		return step.getName() == NAME;
	}

	@Override
	boolean canConvert(ProofStep<OWLAxiom> step) {
		return canConvertStep(step);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Inference<OWLAxiom> getInference() {
		return new ElkOwlInference(FACTORY.getElkPropertyInclusionHierarchy(
				getElkSubClassOfAxiom(getPremises().get(0))
						.getSubObjectPropertyExpression(),
				new AbstractList<ElkObjectPropertyExpression>() {

					@Override
					public ElkObjectPropertyExpression get(int index) {
						return getElkSubClassOfAxiom(
								getPremises().get(index - 1))
										.getSuperObjectPropertyExpression();
					}

					@Override
					public int size() {
						return getPremises().size();
					}
				}));
	}

	private ElkSubObjectPropertyOfAxiom getElkSubClassOfAxiom(
			ProofNode<OWLAxiom> node) {
		return (ElkSubObjectPropertyOfAxiom) convertNodeMember(node);
	}

}
