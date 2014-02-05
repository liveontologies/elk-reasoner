package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

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

/**
 * A general type of conclusions, produced by inference rules. This is the main
 * type of information that is exchanged between {@link Context}s. When a
 * {@link Conclusion} has been derived for a particular {@link Context}, it
 * should be processed within this context.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Conclusion {

	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input);

	/**
	 * 
	 * @param rootWhereStored
	 * @return The {@link IndexedClassExpression} for which this conclusion is
	 *         logically relevant for; it cannot be {@code null}
	 */
	public IndexedClassExpression getSourceRoot(
			IndexedClassExpression rootWhereStored);

	/**
	 * @param rootWhereStored
	 *            the {@code Context} for which to check locality of this
	 *            {@link BackwardLink}
	 * @return {@code true} if the source context of this {@link BackwardLink}
	 *         is the same the given {@link Context}
	 * 
	 * @see BackwardLink#getSourceRoot(IndexedClassExpression)
	 * @see Context#getRoot()
	 */
	public boolean isLocalFor(IndexedClassExpression rootWhereStored);

	/**
	 * Apply all non-redundant inferences for this {@link Conclusion} with other
	 * {@link ContextPremises}
	 * 
	 * @see #applyRedundantRules(RuleVisitor, ContextPremises, ConclusionProducer)
	 * 
	 * @param ruleAppVisitor
	 * @param premises
	 * @param producer
	 */
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer);

	/**
	 * Apply all redundant rules for this {@link Conclusion} and given
	 * {@link ContextPremises}. A rule is redundant if its application is not
	 * necessary for completeness. Redundancy of a rule depends on other
	 * {@link ContextPremises}: a non-redundant rule might become redundant when
	 * other {@link ContextPremises} are added.
	 * 
	 * @see #applyNonRedundantRules(RuleVisitor, ContextPremises,
	 *      ConclusionProducer)
	 * 
	 * @param ruleAppVisitor
	 * @param premises
	 * @param producer
	 */
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer);

	/**
	 * Apply all non-redundant rules that produce {@link Conclusion}s that
	 * logically belong to the same root as this {@link Conclusion}
	 * 
	 * @see #isLocalFor(IndexedClassExpression)
	 * @see #applyRedundantLocalRules(RuleVisitor, ContextPremises,
	 *      ConclusionProducer)
	 * 
	 * @param ruleAppVisitor
	 * @param premises
	 * @param producer
	 */
	public void applyNonRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer);

	/**
	 * Apply all redundant rules that produce {@link Conclusion}s that logically
	 * belong to the same root as this {@link Conclusion}
	 * 
	 * @see #applyNonRedundantLocalRules(RuleVisitor, ContextPremises,
	 *      ConclusionProducer)
	 * 
	 * @param ruleAppVisitor
	 * @param premises
	 * @param producer
	 */
	public void applyRedundantLocalRules(RuleVisitor ruleAppVisitor,
			ContextPremises premises, ConclusionProducer producer);

}
