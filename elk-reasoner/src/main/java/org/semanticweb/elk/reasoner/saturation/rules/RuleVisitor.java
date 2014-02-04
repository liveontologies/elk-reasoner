/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.ContextInitRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.DisjointSubsumerRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ForwardLinkRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.PropagationRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerRuleVisitor;

/**
 * A visitor pattern for {@link Rule}s together with the parameters for which
 * these rules are applied.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
/**
 * @author "Yevgeny Kazakov"
 * 
 */
public interface RuleVisitor extends SubsumerRuleVisitor,
		BackwardLinkRuleVisitor, ContextInitRuleVisitor,
		ContradictionRuleVisitor, DisjointSubsumerRuleVisitor,
		ForwardLinkRuleVisitor, PropagationRuleVisitor {
}
