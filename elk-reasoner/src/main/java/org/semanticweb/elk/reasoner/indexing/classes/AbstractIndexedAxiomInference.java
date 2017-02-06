package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.tracing.AbstractTracingInference;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.TracingInference;

abstract class AbstractIndexedAxiomInference<A extends ElkAxiom>
		extends AbstractTracingInference implements IndexedAxiomInference {

	private final A originalAxiom_;

	AbstractIndexedAxiomInference(A originalAxiom) {
		this.originalAxiom_ = originalAxiom;
	}

	@Override
	public A getOriginalAxiom() {
		return originalAxiom_;
	}

	@Override
	public int getPremiseCount() {
		return 0;
	}

	@Override
	public Conclusion getPremise(int index, Conclusion.Factory factory) {
		return failGetPremise(index);
	}

	@Override
	public final <O> O accept(TracingInference.Visitor<O> visitor) {
		return accept((IndexedAxiomInference.Visitor<O>) visitor);
	}

}
