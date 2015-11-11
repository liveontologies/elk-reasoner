package org.semanticweb.elk.reasoner.saturation.context;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubClassConclusion;

/**
 * An object containing {@link ClassConclusion}s. Every {@link ClassConclusion} is stored
 * in this {@link ClassConclusionSet} at most once.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ClassConclusionSet {

	/**
	 * Adds the given {@link ClassConclusion} to this {@link ClassConclusionSet} if it
	 * does not already contained there
	 * 
	 * @param conclusion
	 *            the {@link ClassConclusion} to be added
	 * @return {@code true} if this {@link ClassConclusionSet} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	boolean addConclusion(ClassConclusion conclusion);

	/**
	 * Removes the given {@link ClassConclusion} from this {@link ClassConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link ClassConclusion} to be removed
	 * @return {@code true} if this {@link ClassConclusionSet} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	boolean removeConclusion(ClassConclusion conclusion);

	/**
	 * Checks if the given {@link ClassConclusion} is contained in this
	 * {@link ClassConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link ClassConclusion} to be checked
	 * @return {@code true} if {@link ClassConclusion} is contained in this
	 *         {@link ClassConclusionSet} and {@code false} otherwise
	 */
	boolean containsConclusion(ClassConclusion conclusion);

	/**
	 * @return {@code true} if this {@link ClassConclusionSet} does not contain any
	 *         {@link ClassConclusion}. In this case,
	 *         {@link #containsConclusion(ClassConclusion)} returns {@code false} for
	 *         every input.
	 */
	boolean isEmpty();

	/**
	 * @return {@code true} if the {@link SubClassConclusionSet} corresponding to the
	 *         given subRoot {@link IndexedObjectProperty} does not contain any
	 *         {@link SubClassConclusion}.
	 * 
	 * @see SubClassConclusionSet#isEmpty()
	 */
	boolean isEmpty(IndexedObjectProperty subRoot);

}
