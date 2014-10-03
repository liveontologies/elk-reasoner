package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A visitor pattern for {@link LinkedSubsumerRule}s
 * 
 * @author "Yevgeny Kazakov"
 */
public interface LinkedSubsumerRuleVisitor {

	void visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(IndexedClassFromDefinitionRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer);

	void visit(SuperClassFromSubClassRule rule, IndexedClassExpression premise,
			ContextPremises premises, ConclusionProducer producer);
}
