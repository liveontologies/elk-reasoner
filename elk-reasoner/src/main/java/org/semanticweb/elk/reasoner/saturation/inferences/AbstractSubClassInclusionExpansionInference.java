package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * A {@link ClassInference} producing a {@link SubClassInclusionDecomposed} from
 * a {@link SubClassInclusionComposed} and other premises:<br>
 * 
 * <pre>
 *     (1)    
 *  [C] ⊑ +D  ...
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *      [C] ⊑ -E
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()}<br>
 * D = {@link #getPremiseSubsumer()}<br>
 * E = {@link #getConclusionSubsumer()}<br>
 * 
 * @author Yevgeny Kazakov
 *
 */
public abstract class AbstractSubClassInclusionExpansionInference
		extends AbstractSubClassInclusionDecomposedInference {

	private final IndexedClassExpression premiseSubsumer_;

	private final ElkAxiom reason_;
	
	public AbstractSubClassInclusionExpansionInference(
			IndexedContextRoot inferenceRoot,
			IndexedClassExpression premiseSubsumer,
			IndexedClassExpression conclusionSubsumer, ElkAxiom reason) {
		super(inferenceRoot, conclusionSubsumer);
		this.premiseSubsumer_ = premiseSubsumer;
		this.reason_ = reason;
	}
	
	public final IndexedClassExpression getPremiseSubsumer() {
		return this.premiseSubsumer_;
	}

	public final ElkAxiom getReason() {
		return reason_;
	}

	@Override
	public final IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public final SubClassInclusionComposed getFirstPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				premiseSubsumer_);
	}


}
