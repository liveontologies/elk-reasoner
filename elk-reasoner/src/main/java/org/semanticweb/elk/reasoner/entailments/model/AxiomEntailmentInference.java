/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.reasoner.entailments.model;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

/**
 * How was some {@link AxiomEntailment} entailed.
 * 
 * @author Peter Skocovsky
 *
 * @param <A>
 *            The type of the axiom.
 */
public interface AxiomEntailmentInference<A extends ElkAxiom>
		extends EntailmentInference {

	@Override
	AxiomEntailment<? extends A> getConclusion();

	public static interface Visito<O>
			extends DerivedClassInclusionEntailsClassAssertionAxiom.Visitor<O>,
			DerivedClassInclusionEntailsObjectPropertyAssertionAxiom.Visitor<O>,
			DerivedClassInclusionEntailsObjectPropertyDomainAxiom.Visitor<O>,
			EntailedClassInclusionCycleEntailsEquivalentClassesAxiom.Visitor<O>,
			EntailedDisjointClassesEntailsDifferentIndividualsAxiom.Visitor<O>,
			EntailedEquivalentClassesEntailsSameIndividualAxiom.Visitor<O>,
			EntailedIntersectionInconsistencyEntailsDisjointClassesAxiom.Visitor<O>,
			OntologyInconsistencyEntailsAnyAxiom.Visitor<O>,
			SubClassOfAxiomEntailmentInference.Visitor<O> {
		// combined interface
	}

}
