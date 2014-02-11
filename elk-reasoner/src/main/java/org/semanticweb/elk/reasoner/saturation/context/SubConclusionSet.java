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

import org.semanticweb.elk.reasoner.saturation.conclusions.SubConclusion;

public interface SubConclusionSet {

	/**
	 * Adds the given {@link SubConclusion} to this {@link SubConclusionSet} if
	 * it does not already contained there
	 * 
	 * @param conclusion
	 *            the {@link SubConclusion} to be added
	 * @return {@code true} if this {@link SubConclusion} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	public boolean addSubConclusion(SubConclusion conclusion);

	/**
	 * Removes the given {@link SubConclusion} from this
	 * {@link SubConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link SubConclusion} to be removed
	 * @return {@code true} if this {@link SubConclusion} has changed as a
	 *         result of this operation and {@link false} otherwise
	 */
	public boolean removeSubConclusion(SubConclusion conclusion);

	/**
	 * Checks if the given {@link SubConclusion} is contained in this
	 * {@link SubConclusionSet}
	 * 
	 * @param conclusion
	 *            the {@link SubConclusion} to be checked
	 * @return {@code true} if {@link SubConclusion} is contained in this
	 *         {@link SubConclusionSet} and {@code false} otherwise
	 */
	public boolean containsSubConclusion(SubConclusion conclusion);

}
