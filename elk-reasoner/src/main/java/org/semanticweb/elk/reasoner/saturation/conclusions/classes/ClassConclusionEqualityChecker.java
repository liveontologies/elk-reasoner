/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;
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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;

/**
 * Checks if they conclusions should be considered logically equal.
 * 
 * TODO complete
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassConclusionEqualityChecker implements ClassConclusion.Visitor<ClassConclusion, Boolean> {

	public static boolean equal(ClassConclusion first, ClassConclusion second) {
		if (first.getOriginRoot() != second.getOriginRoot()) {
			return false;
		}
		
		return first.accept(new ClassConclusionEqualityChecker(), second);
	}
	
	@Override
	public Boolean visit(SubClassInclusionComposed negSCE, ClassConclusion other) {
		return other.accept(new BaseBooleanClassConclusionVisitor<IndexedClassExpression>(){

			@Override
			public Boolean visit(SubClassInclusionComposed subsumer, IndexedClassExpression ice) {
				return subsumer.getSuperExpression() == ice;
			}

			@Override
			public Boolean visit(SubClassInclusionDecomposed subsumer, IndexedClassExpression ice) {
				return subsumer.getSuperExpression() == ice;
			}
			
			
		}, negSCE.getSuperExpression());
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposed posSCE, ClassConclusion other) {
		return other.accept(new BaseBooleanClassConclusionVisitor<IndexedClassExpression>(){

			@Override
			public Boolean visit(SubClassInclusionComposed subsumer, IndexedClassExpression ice) {
				return subsumer.getSuperExpression() == ice;
			}

			@Override
			public Boolean visit(SubClassInclusionDecomposed subsumer, IndexedClassExpression ice) {
				return subsumer.getSuperExpression() == ice;
			}
			
		}, posSCE.getSuperExpression());
	}

	@Override
	public Boolean visit(final BackwardLink link, ClassConclusion other) {
		return other.accept(new BaseBooleanClassConclusionVisitor<Void>(){

			@Override
			public Boolean visit(BackwardLink otherLink, Void ignored) {
				return otherLink.getBackwardRelation() == link.getBackwardRelation() && otherLink.getOriginRoot() == link.getOriginRoot();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(final ForwardLink link, ClassConclusion other) {
		return other.accept(new BaseBooleanClassConclusionVisitor<Void>(){

			@Override
			public Boolean visit(ForwardLink otherLink, Void ignored) {
				return otherLink.getForwardChain() == link.getForwardChain() && otherLink.getTarget() == link.getTarget();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(Contradiction bot, ClassConclusion other) {
		//TODO
		return false;
	}

	@Override
	public Boolean visit(final Propagation propagation, ClassConclusion other) {
		return other.accept(new BaseBooleanClassConclusionVisitor<Void>(){

			@Override
			public Boolean visit(Propagation otherPropagation, Void ignored) {
				return otherPropagation.getRelation() == propagation.getRelation() && otherPropagation.getCarry() == propagation.getCarry();
			}
			
		}, null);
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion, ClassConclusion input) {
		// TODO 
		return false;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion, ClassConclusion input) {
		// TODO 
		return false;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, ClassConclusion input) {
		// TODO 
		return false;
	}

}