/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleConclusionFactory implements ConclusionFactory {

	@Override
	public PositiveSubsumer createSubsumer(IndexedClassExpression ice) {
		return new PositiveSubsumerImpl(ice);
	}

	@Override
	public PositiveSubsumer createSubsumer(Conclusion premise,
			IndexedClassExpression subsumer) {
		return new PositiveSubsumerImpl(subsumer);
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, ForwardLink forwardLink, IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain, Context backwardLinkSource) {
		return new BackwardLinkImpl(backwardLinkSource, chain);
	}
	
	@Override
	public BackwardLink createComposedBackwardLink(Context context, BackwardLink backwardLink, IndexedPropertyChain forwardLinkChain, Context forwardLinkTarget, IndexedPropertyChain chain) {
		return new BackwardLinkImpl(backwardLink.getSource(), chain);
	}
	
	@Override
	public ForwardLink createForwardLink(BackwardLink backwardLink, Context target) {
		return new ForwardLinkImpl(backwardLink.getRelation(), target);
	}
	
	@Override
	public BackwardLink createBackwardLink(IndexedObjectSomeValuesFrom ice, Context target) {
		return new BackwardLinkImpl(target, ice.getRelation());
	}

	@Override
	public Propagation createPropagation(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		return new PropagationImpl(chain, carry);
	}
	
	@Override
	public NegativeSubsumer createPropagatedSubsumer(Propagation propagation, IndexedPropertyChain linkRelation, Context linkTarget, Context context) {
		return new NegativeSubsumerImpl(propagation.getCarry());
	}

	@Override
	public NegativeSubsumer createPropagatedSubsumer(BackwardLink bwLink, IndexedObjectSomeValuesFrom carry, Context context) {
		return new NegativeSubsumerImpl(carry);
	}

	@Override
	public NegativeSubsumer createdComposedConjunction(Conclusion premise,
			IndexedClassExpression conjunct, IndexedObjectIntersectionOf conjunction) {
		return new NegativeSubsumerImpl(conjunction);
	}

	@Override
	public PositiveSubsumer createConjunct(IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		return new PositiveSubsumerImpl(conjunct);
	}

	@Override
	public NegativeSubsumer createReflexiveSubsumer(
			IndexedObjectSomeValuesFrom existential) {
		return new NegativeSubsumerImpl(existential);
	}

}
