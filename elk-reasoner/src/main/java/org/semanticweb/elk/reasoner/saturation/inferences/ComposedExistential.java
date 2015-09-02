/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ComposedSubsumerInferenceVisitor;

/**
 * Represents an existential composition inference from a {@link BackwardLink}
 * and a {@link Propagation}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ComposedExistential extends
		AbstractComposedSubsumerInference<IndexedObjectSomeValuesFrom> {

	private final IndexedObjectProperty propagationRelation_;

	private final IndexedContextRoot inferenceRoot_;

	public ComposedExistential(Propagation premise,
			IndexedContextRoot conclusionRoot) {
		super(conclusionRoot, premise.getCarry());
		inferenceRoot_ = premise.getConclusionRoot();
		propagationRelation_ = premise.getRelation();
	}

	public ComposedExistential(BackwardLink premise,
			IndexedObjectSomeValuesFrom carry) {
		super(premise.getOriginRoot(), carry);
		inferenceRoot_ = premise.getConclusionRoot();
		propagationRelation_ = premise.getBackwardRelation();
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return inferenceRoot_;
	}

	public Propagation getPropagation() {
		return new PropagationImpl(getInferenceRoot(), propagationRelation_,
				getExpression());
	}

	public BackwardLink getBackwardLink() {
		return new BackwardLinkImpl(getInferenceRoot(), propagationRelation_,
				getConclusionRoot());
	}

	@Override
	public String toString() {
		return super.toString() + " (propagation)";
	}

	@Override
	public <I, O> O accept(ComposedSubsumerInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
