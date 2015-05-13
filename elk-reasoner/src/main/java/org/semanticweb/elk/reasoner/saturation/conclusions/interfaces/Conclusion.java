package org.semanticweb.elk.reasoner.saturation.conclusions.interfaces;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

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
 * A general type of conclusions, produced by inference rules and used as
 * premises of inference rules. The rules can be applied to {@link Conclusion}s
 * together with other {@link Conclusion}s stored in {@link ContextPremises}.
 * There are different type of rules that can be applied to a {@link Conclusion}
 * . Non-redundant rules are the (optimized) rules that do not cause
 * incompleteness (all atomic subsumers are guaranteed to be computed). These
 * rules are non-monotonic in the sense that a rule can be applicable with
 * {@link ContextPremises} but not applicable if some {@link Conclusion}s are
 * added to {@link ContextPremises}. This can result in non-deterministic result
 * if the rules are applied in different order. Redundant rules are in the sense
 * those rules that were not applied to {@link ContextPremises} but could be
 * applied if some {@link Conclusion}s were not inserted to
 * {@link ContextPremises}. Next, one can apply only local rules, namely those
 * rules that produce {@link Conclusion}s with the same source root as the
 * {@link Conclusion} to which the rule was applied. These rules are again
 * distinguished on redundant and non-redundant: redundant local rules is the
 * intersection of redundant rules and local rules, and similarly redundant
 * non-local rules.
 * 
 * @see Rule
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface Conclusion {

	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input);

	/**
	 * Returns the root of {@link Context} from which this {@link Conclusion}
	 * was derived (possibly in several steps). This depends on (and can be
	 * different from) the {@link Context} in which this {@link Conclusion} is
	 * stored (to apply further rules); thus the root of the {@link Context}
	 * where the {@link Conclusion} is stored should be given as a parameter of
	 * this method. If an inference produces some {@link Conclusion}s to be
	 * stored in a {@link Context}, this {@link Conclusion} should have the same
	 * source root as one of the premises of this inference. Furthermore, the
	 * {@link Conclusion} cannot be produced after the source {@link Context} is
	 * saturated {@link Context#isSaturated()}, unless it is a
	 * {@link SubConclusion}, which depends on a {@link SubContext} that could
	 * be created later on.
	 * 
	 * @param rootWhereStored
	 *            the {@link IndexedContextRoot} where this {@link Conclusion}
	 *            was derived
	 * @return The {@link IndexedContextRoot} for which this conclusion is
	 *         logically derived; it cannot be {@code null}
	 */
	public IndexedContextRoot getSourceRoot(IndexedContextRoot rootWhereStored);

}
