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

import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;

/**
 * {@link ElkSameIndividualAxiom} was entailed because an
 * {@link org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom
 * ElkEquivalentClassesAxiom} asserting that nominals of
 * {@link ElkSameIndividualAxiom#getIndividuals()} are equivalent was entailed.
 * 
 * @author Peter Skocovsky
 */
public interface EntailedEquivalentClassesEntailsSameIndividualAxiom
		extends AxiomEntailmentInference<ElkSameIndividualAxiom> {

	@Override
	SameIndividualAxiomEntailment getConclusion();

	@Override
	List<? extends EquivalentClassesAxiomEntailment> getPremises();

	public static interface Visitor<O> {
		O visit(EntailedEquivalentClassesEntailsSameIndividualAxiom entailedEquivalentClassesEntailsSameIndividualAxiom);
	}

}
