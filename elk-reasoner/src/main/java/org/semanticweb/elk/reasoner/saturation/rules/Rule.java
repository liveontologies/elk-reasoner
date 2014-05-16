package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
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
 * A rule that can be applied to a given {@link Conclusion} together with other
 * {@link Conclusion}s stored in within {@link ContextPremises}. The rule
 * produces other {@link Conclusion}s using the given
 * {@link SaturationStateWriter}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <P>
 *            the type of premises to which the rule can be applied
 */
public interface Rule<P> {

	/**
	 * @return the name of this rule
	 */
	public String getName();

	/**
	 * Applying the rule to the given premise within the given {@link Context}
	 * and producing {@link Conclusion}s using the given
	 * {@link SaturationStateWriter}
	 * 
	 * @param premise
	 *            the element to which the rule is applied
	 * @param premises
	 *            the {@link Context} from which other matching premises of the
	 *            rule are taken
	 * @param producer
	 *            the {@link ConclusionProducer} using which {@link Conclusion}s
	 *            of the inferences are produced
	 * 
	 */
	public void apply(P premise, ContextPremises premises,
			ConclusionProducer producer);

	public void accept(RuleVisitor visitor, P premise,
			ContextPremises premises, ConclusionProducer producer);

}
