package org.semanticweb.elk.reasoner.saturation.conclusions.model;

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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;

/**
 * A {@link ClassConclusion} representing a derived subclass axiom between the
 * class expression represented by {@link #getDestination()} and the existential
 * restriction on property expression represented by {@link #getRelation()} and
 * filler represented by {@link #getTarget()}. For example, a
 * {@link ForwardLink} with {@link #getDestination()} = {@code :A},
 * {@link #getRelation()} = {@code :r} and {@link #getTarget()} = {@code :B}
 * represents {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B))}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ForwardLink extends ClassConclusion {

	public static final String NAME = "Forward Link";

	/**
	 * @return the {@link IndexedPropertyChain} in the existential restriction
	 *         corresponding to this {@link ForwardLink}
	 */
	public IndexedPropertyChain getRelation();

	/**
	 * @return the {@link IndexedContextRoot} corresponding to the filler of the
	 *         existential restriction corresponding to this {@link ForwardLink}
	 */
	public IndexedContextRoot getTarget();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		ForwardLink getForwardLink(IndexedContextRoot destination,
				IndexedPropertyChain forwardChain, IndexedContextRoot target);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		public O visit(ForwardLink conclusion);

	}

}
