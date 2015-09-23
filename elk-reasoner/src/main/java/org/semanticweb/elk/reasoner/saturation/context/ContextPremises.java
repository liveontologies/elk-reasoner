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

import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;

/**
 * A representation for a set of {@link Conclusion}s that can be used as
 * premises of inference rules associated with a given
 * {@link IndexedContextRoot} that can be obtained using {@link #getRoot()}.
 * Whenever a {@link Conclusion} can participate in an inference with this root,
 * it should be saved in the {@link ContextPremises} with the corresponding
 * root.
 * 
 * @see Rule
 * @author "Yevgeny Kazakov"
 */
public interface ContextPremises {

	/**
	 * @return the {@link IndexedContextRoot} for which the {@link Conclusion}s
	 *         stored in this {@link ContextPremises} are assigned. This serves
	 *         as the key for {@link ContextPremises} and can never been
	 *         {@code null}.
	 */
	IndexedContextRoot getRoot();

	/**
	 * @return the set of all subsumers (implied) {@link IndexedClassExpression}
	 *         s that subsume the root {@link IndexedClassExpression} created by
	 *         composition rules
	 */
	Set<IndexedClassExpression> getComposedSubsumers();

	/**
	 * @return the set of all subsumers (implied) {@link IndexedClassExpression}
	 *         s that subsume the root {@link IndexedClassExpression} created by
	 *         decomposition rules
	 */
	Set<IndexedClassExpression> getDecomposedSubsumers();

	/**
	 * @return the {@link Map} storing {@link SubContextPremises} for the
	 *         corresponding {@link IndexedPropertyChain}s. The
	 *         {@link SubContextPremises} store {@link Conclusion}s that can be
	 *         used as premises of rules that are associated with the
	 *         corresponding sub-root {@link IndexedObjectProperty} in addition
	 *         to the root {@link IndexedClassExpression}
	 * @see SubContextPremises
	 */
	Map<IndexedObjectProperty, ? extends SubContextPremises> getSubContextPremisesByObjectProperty();

	/**
	 * @return the {@link IndexedObjectProperty}s representing all derived
	 *         "local reflexive" existential restrictions, i.e., conclusions of
	 *         the form {@code SubClassOf(:A ObjectSomeValuesFrom(:r :A)}. In
	 *         this case {@code :r} is saved as a reflexive property in the
	 *         context {@code :A}. The returned {@link Set} is not thread safe
	 *         and concurrent access should be properly synchronized. It is
	 *         never {@code null}.
	 */
	Set<IndexedObjectProperty> getLocalReflexiveObjectProperties();

	/**
	 * @return the first backward link rule assigned to this
	 *         {@link ContextPremises}, or {@code null} if there no such rules;
	 *         all other rules can be obtained by traversing over
	 *         {@link LinkRule#next()}; this method should be used to access the
	 *         rules without modifying them.
	 */
	LinkableBackwardLinkRule getBackwardLinkRuleHead();

	/**
	 * @param axiom
	 *            the {@link IndexedDisjointClassesAxiom} whose members could be
	 *            subsumers in this {@link ContextPremises}.
	 * 
	 * @return the derived {@link IndexedClassExpression} subsumers by
	 *         {@link IndexedDisjointClassesAxiom}s in which they occur as
	 *         members
	 */
	IndexedClassExpression[] getDisjointSubsumers(
			IndexedDisjointClassesAxiom axiom);

	Iterable<? extends IndexedObjectSomeValuesFrom> getPropagatedSubsumers(
			IndexedPropertyChain subRoot);
}