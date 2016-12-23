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

import org.liveontologies.proof.util.Inference;
import org.liveontologies.proof.util.ProofNode;
import org.liveontologies.proof.util.ProofStep;
import org.semanticweb.elk.owl.inferences.ElkClassInclusionHierarchy;
import org.semanticweb.elk.owl.inferences.ElkPropertyInclusionHierarchy;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owlapi.proofs.ElkOwlInference;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * An {@link ProofStep} resulting from rewriting nested
 * {@link ElkPropertyInclusionHierarchy} inferences into one
 * {@link ElkPropertyInclusionHierarchy} inference.
 * 
 * For example the sequence of inferences
 * 
 * <pre>
 *       S ⊑ H    H ⊑ T
 *       ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 * R ⊑ S     S ⊑ T
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *       R ⊑ T
 * </pre>
 *
 * is replaced with one inference
 * 
 * <pre>
 * R ⊑ S   S ⊑ H   H ⊑ T
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         R ⊑ T
 * </pre>
 * 
 * The replacement is done provided there is only one inference deriving each
 * premise of the inference and it is {@link ElkPropertyInclusionHierarchy}.
 * 
 * @author Yevgeny Kazakov
 */
class InlinedClassInclusionHierarchyStep extends
		AbstractFlattenedInclusionHierarchyStep implements ProofStep<OWLAxiom> {

	public final static String NAME = ElkClassInclusionHierarchy.NAME;

	private InlinedClassInclusionHierarchyStep(ProofStep<OWLAxiom> step) {
		super(step);
	}

	static ProofStep<OWLAxiom> convert(ProofStep<OWLAxiom> step) {
		if (!canConvertStep(step)) {
			return null;
		}
		// else
		ProofStep<OWLAxiom> result = new InlinedClassInclusionHierarchyStep(
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
		return new ElkOwlInference(FACTORY.getElkClassInclusionHierarchy(
				new AbstractList<ElkClassExpression>() {

					@Override
					public ElkClassExpression get(int index) {
						switch (index) {
						case 0:
							return getElkSubClassOfAxiom(getPremises().get(0))
									.getSubClassExpression();
						default:
							return getElkSubClassOfAxiom(
									getPremises().get(index - 1))
											.getSuperClassExpression();
						}
					}

					@Override
					public int size() {
						return getPremises().size() + 1;
					}
				}));
	}

	private ElkSubClassOfAxiom getElkSubClassOfAxiom(ProofNode<OWLAxiom> node) {
		return (ElkSubClassOfAxiom) convertNodeMember(node);

	}

}
