package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * A {@link BackwardLink} that is obtained by reversing a given
 * {@link ForwardLink} and taking its super-property.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SuperReversedForwardLink extends ReversedForwardLink {

	/**
	 * The {@link ElkAxiom} responsible for relation to be a super-property of
	 * the role chain from the forward link
	 */
	private final ElkAxiom reason_;

	public SuperReversedForwardLink(IndexedContextRoot source,
			IndexedObjectProperty relation, ForwardLink forwardLink,
			ElkAxiom reason) {
		super(forwardLink, relation);
		this.reason_ = reason;
	}

	public ElkAxiom getReason() {
		return this.reason_;
	}

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
