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
import org.semanticweb.elk.reasoner.saturation.conclusions.SuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
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
	 *         {@link SuperClassExpression}s of the root
	 *         {@link IndexedClassExpression}
	 */
	public Set<IndexedClassExpression> getSuperClassExpressions();

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
	 * @return the {@link BackwardLinkRules} registered with this
	 *         {@link Context} or {@code null} if no such a rule is registered.
	 *         These rules are applied to every {@link BackwardLink} inserted to
	 *         this context. This method should be used to access the rules
	 *         without modifying them.
	 */
	public BackwardLinkRules getBackwardLinkRules();

	/**
	 * @return the {@link Chain} view of all {@link BackwardLinkRules}
	 *         registered with this {@link Context}; this is always not
	 *         {@code null}. This method can be used for convenient search and
	 *         modification (addition and deletion) of the rules using the
	 *         methods of the {@link Chain} interface without without worrying
	 *         about {@code null} values.
	 */
	public Chain<BackwardLinkRules> getBackwardLinkRulesChain();

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

	/**
	 * Adds the given {@link SuperClassExpression} to this {@link Context}.
	 * 
	 * @param expression
	 *            the {@link SuperClassExpression} being added to this
	 *            {@link Context}
	 * @return {@code true} if this {@link Context} has changed as the result of
	 *         calling this method, i.e., the {@code SuperClassExpression} has
	 *         not been added before for this {@link Context}. This method is
	 *         not thread safe.
	 */
	public boolean addSuperClassExpression(SuperClassExpression expression);

	public boolean addDisjointnessAxiom(IndexedDisjointnessAxiom axiom);

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
	 * methods {@link #addToDo(Conclusion)} and {@link #deactivate()}.
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
	 * @return {@code true} if all implied {@link SuperClassExpression}s have
	 *         computed for the root of this {@link Context}.
	 */
	public boolean isSaturated();

	/**
	 * Marks this {@code Context} as inconsistent. After this the call of
	 * {@link #isInconsistent()} should return {@code true}
	 */
	public void setInconsistent();

	/**
	 * Marks this {@code Context} as saturated. After this call there should not
	 * be further {@link SuperClassExpression}s added to this {@link Context}.
	 */
	public void setSaturated();

}
