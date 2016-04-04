package org.semanticweb.elk.owl.inferences;

/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.owl.implementation.ElkObjectImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Represents the conclusion of the given {@link ElkInference}
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkInferenceConclusion extends ElkObjectImpl implements ElkAxiom {

	private final ElkInference inference_;

	private final ElkObject.Factory elkFactory_;

	ElkInferenceConclusion(ElkObject.Factory elkFactory,
			ElkInference inference) {
		this.inference_ = inference;
		this.elkFactory_ = elkFactory;
	}

	@Override
	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return inference_.getConclusion(elkFactory_).accept(visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkAxiomVisitor<O>) visitor);
	}

}
