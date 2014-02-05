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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * A representation of {@link Conclusion}s that can be used as premises for rule
 * applications. If an inference is be applicable to {@link Conclusion}s, then
 * all of them should be stored in the same {@link ContextPremises}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ContextPremises {

	/**
	 * @return the {@link IndexedClassExpression} for which this
	 *         {@link ContextPremises} is assigned. This may never been
	 *         {@code null}.
	 */
	public IndexedClassExpression getRoot();

	/**
	 * @return the object representing all derived (implied)
	 *         {@link IndexedClassExpression}s that subsume the root
	 *         {@link IndexedClassExpression}
	 */
	public Set<IndexedClassExpression> getSubsumers();

	/**
	 * @return the {@link ContextPremises}s different from this
	 *         {@link ContextPremises} from which there exists an (implied)
	 *         "existential relation" with this {@link ContextPremises} indexed
	 *         by the {@link IndexedPropertyChain} of this relation. For
	 *         example, if the input ontology contains an axiom
	 *         {@code SubClassOf(:A ObjectSomeValuesFrom(:r :B)} then an
	 *         existential link between {@code :A} and {@code :B} with property
	 *         {@code :r} will be created. For technical reasons, this link is
	 *         stored in the {@link ContextPremises} for {@code :B}, as a
	 *         "backward link" {@code <:r, :A>} indexed by {@code :r} in the
	 *         {@link Multimap} returned by this method. This link is saved only
	 *         if {@code :A} is different from {@code :B}. If they are the same,
	 *         then the property {@code :r} is saved as a local reflexive
	 *         property for the root {@code :B = :A}. The returned
	 *         {@link Multimap} is not thread safe and concurrent access should
	 *         be properly synchronized. This is never {@code null}.
	 * 
	 * @see #getLocalReflexiveObjectProperties()
	 */
	public Multimap<IndexedPropertyChain, IndexedClassExpression> getBackwardLinksByObjectProperty();

	/**
	 * @return the {@link IndexedPropertyChain}s representing all derived
	 *         "local reflexive" existential restrictions, i.e., conclusions of
	 *         the form {@code SubClassOf(:A ObjectSomeValuesFrom(:r :A)}. In
	 *         this case {@code :r} is saved as a reflexive property in the
	 *         context {@code :A}. The returned {@link Set} is not thread safe
	 *         and concurrent access should be properly synchronized. It is
	 *         never {@code null}.
	 */
	public Set<IndexedPropertyChain> getLocalReflexiveObjectProperties();

	/**
	 * @return the first backward link rule assigned to this
	 *         {@link ContextPremises}, or {@code null} if there no such rules;
	 *         all other rules can be obtained by traversing over
	 *         {@link LinkRule#next()}; this method should be used to access the
	 *         rules without modifying them.
	 */
	public LinkableBackwardLinkRule getBackwardLinkRuleHead();

	/**
	 * @param axiom
	 *            the {@link IndexedDisjointnessAxiom} to be checked for causing
	 *            inconsistency in this {@link ContextPremises}
	 * 
	 * @return {@code true} if the given {@link IndexedDisjointnessAxiom} makes
	 *         this {@link ContextPremises} inconsistent, that is, this context
	 *         contains at least two different {@link DisjointSubsumer}s for
	 *         this {@link IndexedDisjointnessAxiom}
	 */
	public boolean isInconsistForDisjointnessAxiom(
			IndexedDisjointnessAxiom axiom);

}
