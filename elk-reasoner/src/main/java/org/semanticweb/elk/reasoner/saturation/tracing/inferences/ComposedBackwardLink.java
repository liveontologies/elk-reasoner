/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Represents a role composition inference. The premises are a backward link and
 * a forward link.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ComposedBackwardLink extends BackwardLinkImpl implements
		ClassInference {

	private final IndexedContextRoot inferenceRoot_;

	private final IndexedObjectProperty backwardRelation_;

	private final IndexedPropertyChain forwardChain_;

	private final IndexedComplexPropertyChain composition_;

	/**
	 * The {@link ElkAxiom} that yields the super-property of the composition
	 */
	private final ElkAxiom reason_;

	public ComposedBackwardLink(IndexedContextRoot originRoot,
			IndexedObjectProperty backwardRelation,
			IndexedContextRoot inferenceRoot,
			IndexedPropertyChain forwardChain, IndexedContextRoot targetRoot,
			IndexedComplexPropertyChain composition,
			IndexedObjectProperty compositionSuperProperty, ElkAxiom reason) {
		super(targetRoot, compositionSuperProperty, originRoot);
		this.backwardRelation_ = backwardRelation;
		this.forwardChain_ = forwardChain;
		this.inferenceRoot_ = inferenceRoot;
		this.composition_ = composition;
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return inferenceRoot_;
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLinkImpl(getInferenceRoot(), backwardRelation_,
				getOriginRoot());
	}

	public ForwardLink getForwardLink() {
		return new ForwardLinkImpl<IndexedPropertyChain>(getInferenceRoot(),
				forwardChain_, getConclusionRoot());
	}

	public SubObjectProperty getLeftSubObjectProperty() {
		return new SubObjectProperty(backwardRelation_,
				composition_.getFirstProperty());
	}

	public SubPropertyChain<?, ?> getRightSubObjectPropertyChain() {
		return new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(
				forwardChain_, composition_.getSuffixChain());
	}

	public ElkAxiom getReason() {
		return reason_;
	}

	@Override
	public String toString() {
		return super.toString() + " (composition)";
	}

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
