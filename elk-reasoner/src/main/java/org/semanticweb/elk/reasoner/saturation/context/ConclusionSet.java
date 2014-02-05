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

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * An object containing {@link Conclusion}s. Every {@link Conclusion} is stored
 * in this {@link ConclusionSet} at most once.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ConclusionSet {

	/**
	 * Adds the given {@link Conclusion} to this {@link Context} if it does not
	 * already contained there
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} to be added
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation and {@link false} otherwise
	 */
	public boolean addConclusion(Conclusion conclusion);

	/**
	 * Removes the given {@link Conclusion} from this {@link Context}
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} to be removed
	 * @return {@code true} if this {@link Context} has changed as a result of
	 *         this operation and {@link false} otherwise
	 */
	public boolean removeConclusion(Conclusion conclusion);

	/**
	 * Checks if the given {@link Conclusion} is contained in this
	 * {@link Context}
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} to be checked
	 * @return {@code true} if {@link Conclusion} is contained in this
	 *         {@link Context} and {@code false} otherwise
	 */
	public boolean containsConclusion(Conclusion conclusion);

}
