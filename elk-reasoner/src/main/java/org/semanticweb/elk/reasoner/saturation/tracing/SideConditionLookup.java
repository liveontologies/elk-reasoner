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
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

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

	private ClassInferenceVisitor<Void, ElkAxiom> classAxiomGetter = new AbstractClassInferenceVisitor<Void, ElkAxiom>() {

		@Override
		protected ElkAxiom defaultTracedVisit(ClassInference conclusion,
				Void ignored) {
			// by default rules aren't bound to axioms, only some are
			return null;
		}

		@Override
		public ElkAxiom visit(SubClassOfSubsumer inference, Void ignored) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(DisjointSubsumerFromSubsumer inference, Void input) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(ContradictionFromDisjointSubsumers inference,
				Void input) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(
				ContradictionFromInconsistentDisjointnessAxiom inference,
				Void input) {
			return inference.getReason();
		}

		@Override
		public ElkAxiom visit(ComposedBackwardLink inference, Void input) {
			return inference.getReason();
		}

	};

	private ObjectPropertyInferenceVisitor<Void, ElkAxiom> propertyAxiomGetter = new AbstractObjectPropertyInferenceVisitor<Void, ElkAxiom>() {

		@Override
		protected ElkObjectPropertyAxiom defaultTracedVisit(
				ObjectPropertyInference inference, Void input) {
			// no side conditions by default
			return null;
		}

		@Override
		public ElkAxiom visit(SubPropertyChainExpanded inference, Void input) {
			return inference.getReason();
		}

	};

	public ElkAxiom lookup(ClassInference inference) {
		return inference.accept(classAxiomGetter, null);
	}

	public ElkAxiom lookup(ObjectPropertyInference inference) {
		return inference.accept(propertyAxiomGetter, null);
	}

}
