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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * {@link ElkObjectPropertyAssertionAxiom} was entailed because inclusion of
 * <code>ObjectOneOf( {@link ElkObjectPropertyAssertionAxiom#getSubject()} )</code>
 * in
 * <code>ObjectSomeValuesFrom( {@link ElkObjectPropertyAssertionAxiom#getProperty()} ObjectOneOf( {@link ElkObjectPropertyAssertionAxiom#getObject()} ) )</code>
 * was derived.
 * <p>
 * {@link #getReason()} returns a {@link SubClassInclusionComposed} with
 * {@link SubClassInclusionComposed#getDestination()} corresponding to
 * <code>ObjectOneOf( {@link ElkObjectPropertyAssertionAxiom#getSubject()} )</code>
 * and {@link SubClassInclusionComposed#getSubsumer()} corresponding to
 * <code>ObjectSomeValuesFrom( {@link ElkObjectPropertyAssertionAxiom#getProperty()} ObjectOneOf( {@link ElkObjectPropertyAssertionAxiom#getObject()} ) )</code>.
 * 
 * @author Peter Skocovsky
 */
public interface DerivedClassInclusionEntailsObjectPropertyAssertionAxiom
		extends AxiomEntailmentInference<ElkObjectPropertyAssertionAxiom>,
		HasReason<SubClassInclusionComposed> {

	@Override
	ObjectPropertyAssertionAxiomEntailment getConclusion();

	public static interface Visitor<O> {
		O visit(DerivedClassInclusionEntailsObjectPropertyAssertionAxiom derivedClassInclusionEntailsObjectPropertyAssertionAxiom);
	}

}
