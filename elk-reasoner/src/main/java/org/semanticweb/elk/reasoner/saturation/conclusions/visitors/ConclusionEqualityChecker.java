/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;

/**
 * Checks if they conclusions should be considered logically equal.
 * 
 * TODO complete
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionEqualityChecker implements ConclusionVisitor<Conclusion, Boolean> {

	public static boolean equal(Conclusion first, Conclusion second) {
		if (first.getOriginRoot() != second.getOriginRoot()) {
			return false;
		}
		
		return first.accept(new ConclusionEqualityChecker(), second);
	}
	
	@Override
	public Boolean visit(ComposedSubsumer negSCE, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<IndexedClassExpression>(){

			@Override
			public Boolean visit(ComposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}

			@Override
			public Boolean visit(DecomposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}
			
			
		}, negSCE.getExpression());
	}

	@Override
	public Boolean visit(DecomposedSubsumer posSCE, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<IndexedClassExpression>(){

			@Override
			public Boolean visit(ComposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}

			@Override
			public Boolean visit(DecomposedSubsumer subsumer, IndexedClassExpression ice) {
				return subsumer.getExpression() == ice;
			}
			
		}, posSCE.getExpression());
	}

	@Override
	public Boolean visit(final BackwardLink link, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(BackwardLink otherLink, Void ignored) {
				return otherLink.getBackwardRelation() == link.getBackwardRelation() && otherLink.getOriginRoot() == link.getOriginRoot();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(final ForwardLink link, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(ForwardLink otherLink, Void ignored) {
				return otherLink.getForwardChain() == link.getForwardChain() && otherLink.getTarget() == link.getTarget();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(Contradiction bot, Conclusion other) {
		//TODO
		return false;
	}

	@Override
	public Boolean visit(final Propagation propagation, Conclusion other) {
		return other.accept(new BaseBooleanConclusionVisitor<Void>(){

			@Override
			public Boolean visit(Propagation otherPropagation, Void ignored) {
				return otherPropagation.getRelation() == propagation.getRelation() && otherPropagation.getCarry() == propagation.getCarry();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, Conclusion input) {
		// TODO 
		return false;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, Conclusion input) {
		// TODO 
		return false;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, Conclusion input) {
		// TODO 
		return false;
	}

}
