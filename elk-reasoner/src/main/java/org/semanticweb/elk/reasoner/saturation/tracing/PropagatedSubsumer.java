/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PropagatedSubsumer extends NegativeSubsumerImpl implements TracedConclusion {

	private final IndexedPropertyChain linkRelation_;
	
	private final Context linkSource_;
	
	private final Context inferenceContext_;
	
	public PropagatedSubsumer(Context context, BackwardLink backwardLink, IndexedObjectSomeValuesFrom carry) {
		super(carry);
		linkSource_ = backwardLink.getSource();
		linkRelation_ = backwardLink.getRelation();
		inferenceContext_ = context;
	}
	
	public PropagatedSubsumer(Context context, Propagation propagation, IndexedPropertyChain linkRelation, Context linkSource) {
		super(propagation.getCarry());
		linkSource_ = linkSource;
		linkRelation_ = linkRelation;
		inferenceContext_ = context;
	}
	
	public Propagation getPropagation() {
		return TracingUtils.getPropagationWrapper(linkRelation_, (IndexedObjectSomeValuesFrom) expression);
	}

	public BackwardLink getBackwardLink() {
		return TracingUtils.getBackwardLinkWrapper(linkRelation_, linkSource_);
	}	
	
	@Override
	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public Context getInferenceContext(Context defaultContext) {
		return inferenceContext_;
	}

}
