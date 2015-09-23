package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedDefinition;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link SubsumerDecompositionRule} that processes an {@link IndexedClass}
 * and produces {@link Subsumer}s for its definition
 * 
 * @see IndexedClass#getDefinition()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedClassDecompositionRule extends
		AbstractSubsumerDecompositionRule<IndexedClass> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedClassDecompositionRule.class);

	private static final IndexedClassDecompositionRule INSTANCE_ = new IndexedClassDecompositionRule();

	public static final String NAME = "Definition Expansion";

	@Override
	public String getName() {
		return NAME;
	}

	public static SubsumerDecompositionRule<IndexedClass> getInstance() {
		return INSTANCE_;
	}

	public static boolean tryAddRuleFor(ModifiableIndexedDefinitionAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		ModifiableIndexedClass definedClass = axiom.getDefinedClass();
		ModifiableIndexedClassExpression definition = axiom.getDefinition();
		boolean success = index.tryAddDefinition(definedClass, definition,
				reason);
		if (success)
			LOGGER_.trace("{}: added definition {} from {}", definedClass,
					definition, reason);
		return success;
	}

	public static boolean tryRemoveRuleFor(
			ModifiableIndexedDefinitionAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		ModifiableIndexedClass definedClass = axiom.getDefinedClass();
		ModifiableIndexedClassExpression definition = axiom.getDefinition();
		boolean success = index.tryRemoveDefinition(definedClass, definition,
				reason);
		if (success)
			LOGGER_.trace("{}: removed definition {} from {}", definedClass,
					definition, reason);
		return success;
	}

	@Override
	public void apply(IndexedClass premise, ContextPremises premises,
			ConclusionProducer producer) {
		IndexedClassExpression definedExpression = getDefinition(premise);
		if (definedExpression == null)
			return;
		producer.produce(new DecomposedDefinition(premises.getRoot(), premise,
				definedExpression, getDefinitionReason(premise)));
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public void accept(SubsumerDecompositionRuleVisitor<?> visitor,
			IndexedClass premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);

	}

	@SuppressWarnings("static-method")
	protected IndexedClassExpression getDefinition(IndexedClass premise) {
		// by default take from the premise, but it may be overridden, e.g., for
		// incremental reasoning
		return premise.getDefinition();
	}

	@SuppressWarnings("static-method")
	protected ElkAxiom getDefinitionReason(IndexedClass premise) {
		// by default take from the premise, but it may be overridden, e.g., for
		// incremental reasoning
		return premise.getDefinitionReason();
	}

}
