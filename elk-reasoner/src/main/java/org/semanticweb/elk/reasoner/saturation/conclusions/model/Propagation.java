package org.semanticweb.elk.reasoner.saturation.conclusions.model;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;

/**
 * A {@link ClassConclusion} representing a derived subclass axiom between the
 * existential restriction on property expression represented by
 * {@link #getRelation()} and filler represented by {@link #getDestination()}
 * and (existential) class expression represented by {@link #getCarry()}. For
 * example, a {@link Propagation} with {@link #getRelation()} = {@code :R},
 * {@link #getDestination()} = {@code :A} and {@link #getCarry()} =
 * {@code ObjectSomeValuesFrom(:s :B)} represents
 * {@code SubClassOf(ObjectSomeValuesFrom(:r :A) ObjectSomeValuesFrom(:s :B))}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */
public interface Propagation extends SubClassConclusion {

	public static final String NAME = "Propagation";

	/**
	 * @return the {@link IndexedObjectProperty} that is the relation over which
	 *         this {@link Propagation} is applied
	 */
	public IndexedObjectProperty getRelation();

	/**
	 * @return the {@link IndexedObjectSomeValuesFrom} that is propagated by
	 *         this {@link Propagation}
	 */
	public IndexedObjectSomeValuesFrom getCarry();

	public <O> O accept(Visitor<O> visitor);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		Propagation getPropagation(IndexedContextRoot destination,
				IndexedObjectProperty relation,
				IndexedObjectSomeValuesFrom carry);

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

		public O visit(Propagation conclusion);

	}

}
