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

import java.util.ArrayList;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ForwardLink}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            The type of the forward relation
 */
public class ForwardLinkImpl<R extends IndexedPropertyChain> extends
		AbstractConclusion implements ForwardLink {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(ForwardLinkImpl.class);

	/**
	 * the {@link IndexedPropertyChain} in the existential restriction
	 * corresponding to this {@link ForwardLinkImpl}
	 */
	final R forwardChain_;

	/**
	 * the {@link IndexedContextRoot} corresponding to the filler of the
	 * existential restriction corresponding to this {@link ForwardLinkImpl}
	 */
	final IndexedContextRoot target_;

	public ForwardLinkImpl(IndexedContextRoot root, R relation,
			IndexedContextRoot target) {
		super(root);
		this.forwardChain_ = relation;
		this.target_ = target;
	}

	@Override
	public R getForwardChain() {
		return forwardChain_;
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
		return forwardChain_ + "->" + target_;
	}

	// TODO: find a better place for the following methods

	public static void produceDecomposedExistentialLink(
			ConclusionProducer producer, IndexedContextRoot source,
			IndexedObjectSomeValuesFrom existential) {
		SaturatedPropertyChain propertySaturation = existential.getProperty()
				.getSaturated();
		if (propertySaturation.getCompositionsByLeftSubProperty().isEmpty()) {
			producer.produce(new DecomposedExistentialBackwardLink(source,
					existential));
		} else {
			producer.produce(new DecomposedExistentialForwardLink(source,
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
			ArrayList<IndexedObjectProperty> toldSuperProperties = composition
					.getToldSuperProperties();
			ArrayList<ElkAxiom> toldSuperPropertiesReasons = composition
					.getToldSuperPropertiesReasons();
			for (int i = 0; i < toldSuperProperties.size(); i++) {
				producer.produce(new ComposedBackwardLink(source,
						backwardRelation, inferenceRoot, forwardRelation,
						target, composition, toldSuperProperties.get(i),
						toldSuperPropertiesReasons.get(i)));
			}
		} else {
			producer.produce(new ComposedForwardLink(source, backwardRelation,
					inferenceRoot, forwardRelation, target, composition));
		}
	}
}
