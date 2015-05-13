/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.conclusions.implementation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialForwardLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ForwardLink}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ForwardLinkImpl extends AbstractConclusion implements ForwardLink {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(ForwardLinkImpl.class);

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this {@link ForwardLinkImpl}
	 */
	final IndexedPropertyChain relation_;

	/**
	 * the {@link IndexedContextRoot} corresponding to the filler of the
	 * existential restriction corresponding to this {@link ForwardLinkImpl}
	 */
	final IndexedContextRoot target_;

	public ForwardLinkImpl(IndexedPropertyChain relation,
			IndexedContextRoot target) {
		this.relation_ = relation;
		this.target_ = target;
	}

	@Override
	public IndexedPropertyChain getRelation() {
		return relation_;
	}

	@Override
	public IndexedContextRoot getTarget() {
		return target_;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return relation_ + "->" + target_;
	}

	// TODO: find a better place for the following methods

	public static void produceDecomposedExistentialLink(
			ConclusionProducer producer, IndexedContextRoot source,
			IndexedObjectSomeValuesFrom existential) {
		if (existential.getProperty().getSaturated()
				.getCompositionsByLeftSubProperty().isEmpty()) {
			// TODO: check property ranges
			producer.produce(existential.getFillerConcept(),
					new DecomposedExistentialBackwardLink(source, existential));
		} else {
			producer.produce(source, new DecomposedExistentialForwardLink(
					existential));
		}
	}

	public static void produceComposedLink(ConclusionProducer producer,
			IndexedContextRoot source, IndexedObjectProperty backwardRelation,
			IndexedContextRoot inferenceRoot,
			IndexedPropertyChain forwardRelation, IndexedContextRoot target,
			IndexedComplexPropertyChain composition) {

		if (composition.getSaturated().getCompositionsByLeftSubProperty()
				.isEmpty()) {
			for (IndexedObjectProperty toldSuper : composition
					.getToldSuperProperties()) {
				producer.produce(target, new ComposedBackwardLink(source,
						backwardRelation, inferenceRoot, forwardRelation,
						target, toldSuper));
			}
		} else {
			producer.produce(source, new ComposedForwardLink(source,
					backwardRelation, inferenceRoot, forwardRelation, target,
					composition));
		}
	}
}
