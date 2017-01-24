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
package org.semanticweb.elk.reasoner.entailments.impl;

import java.util.Collections;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.DerivedClassInclusionEntailsObjectPropertyAssertionAxiom;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.ObjectPropertyAssertionAxiomEntailment;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public class DerivedClassInclusionEntailsObjectPropertyAssertionAxiomImpl
		extends
		AbstractAxiomEntailmentInference<ElkObjectPropertyAssertionAxiom, ObjectPropertyAssertionAxiomEntailment>
		implements DerivedClassInclusionEntailsObjectPropertyAssertionAxiom {

	private final SubClassInclusionComposed reason_;

	public DerivedClassInclusionEntailsObjectPropertyAssertionAxiomImpl(
			final ObjectPropertyAssertionAxiomEntailment conclusion,
			final SubClassInclusionComposed reason) {
		super(conclusion);
		this.reason_ = reason;
	}

	@Override
	public List<? extends Entailment> getPremises() {
		return Collections.emptyList();
	}

	@Override
	public SubClassInclusionComposed getReason() {
		return reason_;
	}

	@Override
	public <O> O accept(final EntailmentInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
