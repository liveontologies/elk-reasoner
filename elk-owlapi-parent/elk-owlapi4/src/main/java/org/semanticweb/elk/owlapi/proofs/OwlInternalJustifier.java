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

import java.util.Set;

import org.liveontologies.puli.Inference;
import org.liveontologies.puli.InferenceJustifier;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.ElkConverter;
import org.semanticweb.elk.proofs.InternalJustifier;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class OwlInternalJustifier
		implements InferenceJustifier<Inference<?>, Set<OWLAxiom>>,
		Function<ElkAxiom, OWLAxiom> {

	private final InferenceJustifier<Inference<?>, Set<? extends ElkAxiom>> internalJustifier_ = new InternalJustifier();

	private final ElkConverter elkConverter_ = ElkConverter.getInstance();

	@Override
	public Set<OWLAxiom> getJustification(Inference<?> inference) {
		return ImmutableSet.copyOf(Iterables.transform(
				internalJustifier_.getJustification(inference), this));
	}

	@Override
	public OWLAxiom apply(final ElkAxiom input) {
		return elkConverter_.convert(input);
	}

}
