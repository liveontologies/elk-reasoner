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
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.saturation.inferences.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;

/**
 * Given a {@link ClassInference} or a {@link ObjectPropertyInference} tries to
 * look up its side condition, if one exists, using the axiom binding.
 * 
 * TODO make it a visitor over class and object property inferences,
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionLookup {

	private ClassInference.Visitor<ElkAxiom> classAxiomGetter = new AbstractClassInferenceVisitor<ElkAxiom>() {

		@Override
		protected ElkAxiom defaultTracedVisit(ClassInference conclusion) {
			// by default rules aren't bound to axioms, only some are
			return null;
		}

		@Override
		public ElkAxiom visit(SubClassInclusionExpandedSubClassOf inference) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(DisjointSubsumerFromSubsumer inference) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(ContradictionOfDisjointSubsumers inference) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(BackwardLinkComposition inference) {
			return inference.getReason();
		}

	};

	private ObjectPropertyInference.Visitor<ElkAxiom> propertyAxiomGetter = new AbstractObjectPropertyInferenceVisitor<ElkAxiom>() {

		@Override
		protected ElkObjectPropertyAxiom defaultTracedVisit(
				ObjectPropertyInference inference) {
			// no side conditions by default
			return null;
		}

		@Override
		public ElkAxiom visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
			return inference.getReason();
		}

	};

	public ElkAxiom lookup(ClassInference inference) {
		return inference.accept(classAxiomGetter);
	}

	public ElkAxiom lookup(ObjectPropertyInference inference) {
		return inference.accept(propertyAxiomGetter);
	}

}
