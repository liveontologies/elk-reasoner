package org.semanticweb.elk.reasoner.saturation.context;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * An object representing an elementary unit of computation for saturation of an
 * {@link IndexedClassExpression}, stored as a <em>root</em> of the
 * {@link Context}. This interface specifies method that can be used to access
 * the result of the computation in addition to the methods used by a
 * {@link RuleApplicationFactory} to perform the computation concurrently.
 * 
 * @author "Yevgeny Kazakov"
 * @see RuleApplicationFactory
 * 
 */
public interface Context extends ConclusionSet, ContextPremises {

	/**
	 * @return the {@link Chain} view of all backward link rules assigned to
	 *         this {@link ContextPremises}; this is always not {@code null}.
	 *         This method can be used for convenient search and modification
	 *         (addition and deletion) of the rules using the methods of the
	 *         {@link Chain} interface without without worrying about
	 *         {@code null} values.
	 */
	public Chain<LinkableBackwardLinkRule> getBackwardLinkRuleChain();

	/**
	 * Adds the given {@link Conclusion} to be processed within this
	 * {@link Context}. The method returns {@code true} when this is the first
	 * unprocessed conclusion added to the context after it is being created or
	 * cleared (that is, {@link #takeToDo()} has returned {@code null}). If
	 * several threads call this method at the same time for the same
	 * {@link Context} then at most one of these method returns {@code true},
	 * unless {@link #takeToDo()} is called as well.
	 * 
	 * @param conclusion
	 *            the {@link Conclusion} added to be processed within this
	 *            {@link Context}
	 * @return {@code true} when the added conclusion is the first unprocessed
	 *         conclusion for this context
	 * @see #takeToDo()
	 */
	public boolean addToDo(Conclusion conclusion);

	/**
	 * Removes and returns one of the unprocessed {@link Conclusion}s of this
	 * context. This method is thread safe and can be used concurrently with the
	 * method {@link #addToDo(Conclusion)}.
	 * 
	 * @return some unprocessed {@link Conclusion} of this context, if there is
	 *         one, or {@code null} if there is no such {@link Conclusion}
	 * @see #addToDo(Conclusion)
	 */
	public Conclusion takeToDo();

	/**
	 * Call the given {@link ConclusionProcessor} exactly once for every
	 * unprocessed {@link Conclusion} that was added using the method
	 * {@link #addToDo(Conclusion)} and removes such {@link Conclusion}s. This
	 * method is thread safe and can be used concurrently with the method
	 * {@link #addToDo(Conclusion)}.
	 * 
	 * @param processor
	 *            the {@link ConclusionProcessor} to be called on unprocessed
	 *            {@link Conclusion}s of this {@link Context}
	 */
	// public void processPendingConclusions(ConclusionProcessor processor);

	/**
	 * @return {@code true} if all {@link Conclusion}s for this {@link Context},
	 *         as determined by the function
	 *         {@link Conclusion#getSourceRoot(IndexedClassExpression)}, are
	 *         already computed.
	 */
	public boolean isSaturated();

	/**
	 * Marks this {@code Context} as saturated. This means that all
	 * {@link Conclusion}s for this {@link Context} are already computed.
	 * 
	 * @return the previous value of the saturation state for this
	 *         {@link Context}
	 */
	public boolean setSaturated(boolean saturated);

	/**
	 * 
	 * @return {@code true} if the context is empty
	 */
	public boolean isEmpty();

	/**
	 * removes links to the next and previous contexts, effectively removing
	 * this {@link Context} from the chain of contexts
	 */
	// public void removeLinks(); // TODO: find a way to hide this

}
