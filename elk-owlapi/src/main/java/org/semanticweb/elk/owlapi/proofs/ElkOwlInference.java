package org.semanticweb.elk.owlapi.proofs;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.liveontologies.puli.AbstractConvertedInference;
import org.liveontologies.puli.Inference;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.ElkConverter;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ElkOwlInference
		extends AbstractConvertedInference<ElkAxiom, OWLAxiom>
		implements Inference<OWLAxiom> {

	public ElkOwlInference(Inference<ElkAxiom> elkInference) {
		super(elkInference);
	}

	@Override
	protected OWLAxiom convert(ElkAxiom axiom) {
		return ElkConverter.getInstance().convert(axiom);
	}

}
