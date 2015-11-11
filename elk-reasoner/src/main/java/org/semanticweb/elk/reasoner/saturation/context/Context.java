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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * An object representing an elementary unit of computation for
 * {@link ClassConclusion}s that can be used as premises of inferences associated
 * with the {@link IndexedClassExpression}, stored as a <em>root</em> of this
 * {@link Context} ({@link Context#getRoot()}). The computation is organized in
 * a saturation process where all {@link ClassConclusion}s to which inferences should
 * be applied are added to the "todo" queue using
 * {@link #addConclusion(ClassConclusion)} and when the rules are applied, they are
 * repeatedly taken from this queue using {@link #takeToDo()}. The object
 * provides some methods in addition to {@link ClassConclusionSet} to store, test and
 * remove information for {@link ClassConclusion}s in this {@link Context}, and some
 * bookkeeping methods for the saturation process.
 * 
 * @author "Yevgeny Kazakov"
 * @see RuleApplicationAdditionFactory
 * 
 */
public interface Context extends ClassConclusionSet, ContextPremises {

	/**
	 * @return the {@link Chain} view of all backward link rules assigned to
	 *         this {@link ContextPremises}; this is always not {@code null}.
	 *         This method can be used for convenient search and modification
	 *         (addition and deletion) of the rules using the methods of the
	 *         {@link Chain} interface without without worrying about
	 *         {@code null} values.
	 */
	Chain<LinkableBackwardLinkRule> getBackwardLinkRuleChain();

	/**
	 * Adds the given {@link ClassConclusion} to be processed within this
	 * {@link Context}. The method returns {@code true} when this is the first
	 * unprocessed conclusion added to the context after it is being created or
	 * cleared (that is, {@link #takeToDo()} has returned {@code null}). If
	 * several threads call this method at the same time for the same
	 * {@link Context} then at most one of these method returns {@code true},
	 * unless {@link #takeToDo()} is called as well.
	 * 
	 * @param conclusion
	 *            the {@link ClassConclusion} added to be processed within this
	 *            {@link Context}
	 * @return {@code true} when the added conclusion is the first unprocessed
	 *         conclusion for this context
	 * @see #takeToDo()
	 */
	boolean addToDo(ClassConclusion conclusion);

	/**
	 * Removes and returns one of the unprocessed {@link ClassConclusion}s of this
	 * context. This method is thread safe and can be used concurrently with the
	 * method {@link #addToDo(ClassConclusion)}.
	 * 
	 * @return some unprocessed {@link ClassConclusion} of this context, if there is
	 *         one, or {@code null} if there is no such {@link ClassConclusion}
	 * @see #addToDo(ClassConclusion)
	 */
	ClassConclusion takeToDo();

	/**
	 * @return {@code true} if all {@link ClassConclusion}s for this {@link Context},
	 *         as determined by the function
	 *         {@link ClassConclusion#getSourceRoot(IndexedClassExpression)}, except
	 *         for {@link SubClassConclusion}s are already computed.
	 */
	boolean isSaturated();

	boolean isInitialized();

}
