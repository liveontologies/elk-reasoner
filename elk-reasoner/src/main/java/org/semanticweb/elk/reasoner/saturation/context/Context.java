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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.ModifiableLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.util.collections.Multimap;
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
public interface Context {

	/**
	 * @return the {@link IndexedClassExpression} for which this {@link Context}
	 *         is assigned. This may never been {@code null}.
	 */
	public IndexedClassExpression getRoot();

	/**
	 * @return the object representing all derived (implied)
	 *         {@link IndexedClassExpression}s that subsume the root
	 *         {@link IndexedClassExpression}
	 */
	public Set<IndexedClassExpression> getSubsumers();

	/**
	 * @return the {@link Context}s from which there exists an (implied)
	 *         "existential relation" with this {@link Context} indexed by the
	 *         {@link IndexedPropertyChain} of this relation. For example, if
	 *         the input ontology contains an axiom
	 *         {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B)} then an
	 *         existential link between the context with root {@code :A} and the
	 *         context with root {@code :B} with property {@code :r} will be
	 *         created. For technical reasons, this link is stored in the
	 *         context for {@code :B}, as a "backward link" {@code <:r, :A>}
	 *         indexed by {@code :r} in the {@link Multimap} returned by this
	 *         method. The returned {@link Multimap} is not thread safe and
	 *         should be accessed from at most one thread at a time. This is
	 *         never {@code null}.
	 */
	public Multimap<IndexedPropertyChain, Context> getBackwardLinksByObjectProperty();

	/**
	 * @return the first backward link rule assigned to this {@link Context}, or
	 *         {@code null} if there no such rules; all other rules can be
	 *         obtained by traversing over {@link LinkRule#next()}; this method
	 *         should be used to access the rules without modifying them.
	 */
	public LinkRule<BackwardLink, Context> getBackwardLinkRuleHead();

	/**
	 * @return the {@link Chain} view of all backward link rules assigned to
	 *         this {@link Context}; this is always not {@code null}. This
	 *         method can be used for convenient search and modification
	 *         (addition and deletion) of the rules using the methods of the
	 *         {@link Chain} interface without without worrying about
	 *         {@code null} values.
	 */
	public Chain<ModifiableLinkRule<BackwardLink, Context>> getBackwardLinkRuleChain();

	/**
	 * Adds the given {@code BackwardLink} to this {@link Context}.
	 * 
	 * @param link
	 *            the {@code BackwardLink} being added to this {@link Context}
	 * @return {@code true} if this {@link Context} has changed as the result
	 *         this method, i.e., the given {@code BackwardLink} has not been
	 *         added before to this {@link Context}. This method is not thread
	 *         safe.
	 */
	public boolean addBackwardLink(BackwardLink link);

	/*
	 * TODO
	 */
	public boolean removeBackwardLink(BackwardLink link);

	public boolean containsBackwardLink(BackwardLink link);

	/**
	 * Adds the given {@link IndexedClassExpression} to the subsumers of the
	 * root {@link IndexedClassExpression} of this {@link Context}.
	 * 
	 * @param expression
	 *            the {@link IndexedClassExpression} to be added as a susbumer
	 *            of the root {@link IndexedClassExpression} of this
	 *            {@link Context}.
	 * @return {@code true} if the set of subsumers of this {@link Context} has
	 *         changed as the result of calling this method, i.e., the input
	 *         {@code IndexedClassExpression} was not a subsumer before. This
	 *         method is not thread safe.
	 */
	public boolean addSubsumer(IndexedClassExpression expression);

	/**
	 * Removes the given {@link IndexedClassExpression} from the subsumers of
	 * the root {@link IndexedClassExpression} of this {@link Context}.
	 * 
	 * @param expression
	 *            the {@link IndexedClassExpression} to be removed from the
	 *            subsumers of the root in this {@link Context}
	 * @return {@code true} if the set of subsumers of this {@link Context} has
	 *         changed as the result of calling this method, i.e., the input
	 *         {@code IndexedClassExpression} was a subsumer before. This method
	 *         is not thread safe.
	 */
	public boolean removeSubsumer(IndexedClassExpression expression);

	/**
	 * Tests whether the given {@link IndexedClassExpression} is a subsumer of
	 * the root {@link IndexedClassExpression} of this {@link Context}.
	 * 
	 * @param expression
	 *            the {@link IndexedClassExpression} to be tested for this
	 *            {@link Context}
	 * @return {@code true} if the given {@link IndexedClassExpression} is a
	 *         subsumer of the root in this {@link Context}. This method is not
	 *         thread safe.
	 */
	public boolean containsSubsumer(IndexedClassExpression expression);

	/**
	 * Adds one instance of {@link IndexedDisjointnessAxiom} to this
	 * {@link Context}.
	 * 
	 * @param axiom
	 *            the {@link IndexedDisjointnessAxiom} to be added to this
	 *            {@link Context}
	 * @return {@code true} if adding the axiom changes the state of this
	 *         {@link Context}, i.e., some rules need to be applied
	 */
	public boolean addDisjointnessAxiom(IndexedDisjointnessAxiom axiom);

	/**
	 * Removes one instance of the given {@link IndexedDisjointnessAxiom} from
	 * this {@link Context}.
	 * 
	 * @param axiom
	 *            the {@link IndexedDisjointnessAxiom} to be removed from this
	 *            {@link Context}
	 * @return {@code true} if the state of this {@link Context} has changed as
	 *         the result of calling this method, i.e., the context has
	 *         contained this {@link IndexedDisjointnessAxiom}
	 */
	public boolean removeDisjointnessAxiom(IndexedDisjointnessAxiom axiom);

	/**
	 * @param axiom
	 *            the {@link IndexedDisjointnessAxiom} to be checked for
	 *            occurrences in this {@link Context}
	 * 
	 * @return {@code true} if the given {@link IndexedDisjointnessAxiom} occurs
	 *         in this {@link Context}
	 */
	public boolean containsDisjointnessAxiom(IndexedDisjointnessAxiom axiom);

	/**
	 * @param axiom
	 *            the {@link IndexedDisjointnessAxiom} to be checked for causing
	 *            inconsistency in this {@link Context}
	 * 
	 * @return {@code true} if the given {@link IndexedDisjointnessAxiom} causes
	 *         inconsistency of this {@link Context}
	 */
	public boolean inconsistencyDisjointnessAxiom(IndexedDisjointnessAxiom axiom);

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
	 * @return {@code true} if a contradiction has not been derived for the root
	 *         {@link IndexedClassExpression}
	 */
	public boolean isInconsistent();

	/**
	 * @return {@code true} if all {@link Conclusion}s for this {@link Context},
	 *         as determined by the function
	 *         {@link Conclusion#getSourceContext(Context)}, are already
	 *         computed.
	 */
	public boolean isSaturated();

	/**
	 * Sets the inconsistency of this {@code Context} to the given value.
	 * 
	 * @return the previous inconsistency value
	 */
	public boolean setInconsistent(boolean consistent);

	/**
	 * Marks this {@code Context} as saturated. This means that all all
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
	public void removeLinks(); // TODO: find a way to hide this

}
