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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingConclusionFactory implements ConclusionFactory {

	@Override
	public DecomposedSubsumer createSubsumer(IndexedClassExpression ice) {
		//no premise, must be an initialization inference
		return new InitializationSubsumer(ice);
	}

	@Override
	public DecomposedSubsumer createSubsumer(Conclusion premise,
			IndexedClassExpression subsumer) {
		//a subsumption inference
		return new SubClassOfSubsumer(premise, subsumer);
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, ForwardLink forwardLink,
			IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain,
			Context backwardLinkSource) {
		return new ComposedBackwardLink(chain, getMainContext(context), forwardLink, backwardLinkChain, getMainContext(backwardLinkSource));
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, BackwardLink backwardLink,
			IndexedPropertyChain forwardRelation, Context forwardTarget, IndexedPropertyChain chain) {
		return new ComposedBackwardLink(chain, getMainContext(context), backwardLink, forwardRelation, getMainContext(forwardTarget));
	}

	@Override
	public ForwardLink createForwardLink(BackwardLink backwardLink, Context target) {
		return new ReversedBackwardLink(backwardLink, getMainContext(target));
	}

	@Override
	public BackwardLink createBackwardLink(
			IndexedObjectSomeValuesFrom subsumer, Context source) {
		return new DecomposedExistential(subsumer, getMainContext(source));
	}

	@Override
	public Propagation createPropagation(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		return new TracedPropagation(chain, carry);
	}

	@Override
	public ComposedSubsumer createPropagatedSubsumer(BackwardLink bwLink,
			IndexedObjectSomeValuesFrom carry, Context context) {
		return new PropagatedSubsumer(context, bwLink, carry);
	}
	
	@Override
	public ComposedSubsumer createPropagatedSubsumer(Propagation propagation, IndexedPropertyChain linkSource, Context linkTarget, Context context) {
		return new PropagatedSubsumer(getMainContext(context), propagation, linkSource, getMainContext(linkTarget));
	}

	@Override
	public ComposedSubsumer createdComposedConjunction(Subsumer subsumer,
			IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		return new ComposedConjunction(subsumer.getExpression(), conjunct, conjunction);
	}

	@Override
	public DecomposedSubsumer createConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		return new DecomposedConjunction(conjunction, conjunct);
	}

	@Override
	public ComposedSubsumer createReflexiveSubsumer(
			IndexedObjectSomeValuesFrom existential) {
		return new ReflexiveSubsumer(existential);
	}
	
	private Context getMainContext(Context cxt) {
		return cxt.getRoot().getContext();
	}
}