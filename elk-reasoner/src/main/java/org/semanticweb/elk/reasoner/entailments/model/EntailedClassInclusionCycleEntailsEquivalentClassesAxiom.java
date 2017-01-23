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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;

/**
 * {@link ElkEquivalentClassesAxiom} was entailed because a cyclic inclusion of
 * classes from {@link ElkEquivalentClassesAxiom#getClassExpressions()} was
 * derived.
 * <p>
 * {@link #getPremises()} returns {@link SubClassOfAxiomEntailment}-s over all
 * the classes from {@link ElkEquivalentClassesAxiom#getClassExpressions()},
 * such that superclass of one is the subclass of the next one and superclass of
 * the last one is the subclass of the first one.
 * 
 * @author Peter Skocovsky
 */
public interface EntailedClassInclusionCycleEntailsEquivalentClassesAxiom
		extends AxiomEntailmentInference<ElkEquivalentClassesAxiom> {

	@Override
	EquivalentClassesAxiomEntailment getConclusion();

	@Override
	List<? extends SubClassOfAxiomEntailment> getPremises();

	public static interface Visitor<O> {
		O visit(EntailedClassInclusionCycleEntailsEquivalentClassesAxiom derivedClassInclusionCycleEntailsEquivalentClassesAxiom);
	}

}
