package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;

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
 * A rule that can be applied to a given premise (either a {@link ClassConclusion} or
 * an object representing a {@link ClassConclusion}) together with other
 * {@link ClassConclusion}s stored in within {@link ContextPremises}. The rule
 * produces other {@link ClassConclusion}s using the given {@link ClassConclusionProducer}
 * .
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of premises to which the rule can be applied
 */
public interface Rule<P> {

	/**
	 * Apply the rule to the given premise representing a {@link ClassConclusion} and
	 * other {@link ClassConclusion}s stored in within the given
	 * {@link ContextPremises} and produce {@link ClassConclusion}s using the given
	 * {@link ClassConclusionProducer}
	 * 
	 * @param premise
	 *            the element to which the rule is applied, it represents a
	 *            {@link ClassConclusion}
	 * @param premises
	 *            the {@link ContextPremises} from which other matching premises
	 *            of the rule are taken
	 * @param producer
	 *            the {@link ClassConclusionProducer} using which {@link ClassConclusion}s
	 *            of the inferences are produced
	 * 
	 */
	public void apply(P premise, ContextPremises premises,
			ClassConclusionProducer producer);

	/**
	 * @return {@code true} if this {@link Rule} produces only
	 *         {@link ClassConclusion}s with the same origin root and sub-root as the
	 *         {@link ClassConclusion} (or its representation) to which the rule is
	 *         applied. Specifically, if applied for a {@link ClassConclusion} the
	 *         rule can produce only {@link ClassConclusion}s with the same value of
	 *         {@link ClassConclusion#getOriginRoot()}. Additionally, if applied to a
	 *         {@link SubClassConclusion} the rule can produce only
	 *         {@link SubClassConclusion} with the same values of
	 *         {@link SubClassConclusion#getOriginSubRoot()} or a {@link ClassConclusion}
	 *         if this value is {@code null}. Returns {@code false} if this
	 *         {@link Rule} produces only {@link ClassConclusion}s with the different
	 *         origin root or sub-root as the {@link ClassConclusion} (or its
	 *         representation) to which the rule is applied.
	 * 
	 * @see ClassConclusion#getOriginRoot()
	 * @see SubClassConclusion#getConclusionRoot()
	 */
	public boolean isLocal();

	public void accept(RuleVisitor<?> visitor, P premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
