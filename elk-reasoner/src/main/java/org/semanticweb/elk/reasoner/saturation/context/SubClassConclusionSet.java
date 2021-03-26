package org.semanticweb.elk.reasoner.saturation.context;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2021 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;

public interface SubClassConclusionSet {

	/**
	 * Adds the given {@link SubClassConclusion} to this {@link SubClassConclusionSet} if
	 * it does not already contained there
	 * 
	 * @param conclusion
	 *            the {@link SubClassConclusion} to be added
	 * @return {@code true} if this {@link SubClassConclusion} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	boolean addSubConclusion(SubClassConclusion conclusion);

	/**
	 * Removes the given {@link SubClassConclusion} from this
	 * {@link SubClassConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link SubClassConclusion} to be removed
	 * @return {@code true} if this {@link SubClassConclusion} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	boolean removeSubConclusion(SubClassConclusion conclusion);

	/**
	 * Checks if the given {@link SubClassConclusion} is contained in this
	 * {@link SubClassConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link SubClassConclusion} to be checked
	 * @return {@code true} if {@link SubClassConclusion} is contained in this
	 *         {@link SubClassConclusionSet} and {@code false} otherwise
	 */
	boolean containsSubConclusion(SubClassConclusion conclusion);

	/**
	 * @return {@code true} if this {@link SubClassConclusionSet} does not
	 *         contain any {@link SubClassConclusion}. In this case,
	 *         {@link #containsSubConclusion(SubClassConclusion)} returns
	 *         {@code false} for every input.
	 */
	boolean isEmpty();

}
