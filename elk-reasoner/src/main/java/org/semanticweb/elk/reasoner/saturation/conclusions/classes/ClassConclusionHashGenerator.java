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
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Generates a hash code for the given {@link ClassConclusion}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ClassConclusionHashGenerator implements ClassConclusion.Visitor<Void, Integer> {

	@Override
	public Integer visit(SubClassInclusionComposed negSCE, Void context) {
		return negSCE.getSuperExpression().hashCode();
	}

	@Override
	public Integer visit(SubClassInclusionDecomposed posSCE, Void context) {
		return posSCE.getSuperExpression().hashCode();
	}

	@Override
	public Integer visit(BackwardLink link, Void context) {
		return HashGenerator.combineListHash(link.getBackwardRelation().hashCode(), link.getOriginRoot().hashCode());
	}

	@Override
	public Integer visit(ForwardLink link, Void context) {
		return HashGenerator.combineListHash(link.getForwardChain().hashCode(), link.getTarget().hashCode());
	}

	@Override
	public Integer visit(Contradiction bot, Void context) {
		return bot.hashCode();
	}

	@Override
	public Integer visit(Propagation propagation, Void context) {
		return HashGenerator.combineListHash(propagation.getRelation().hashCode(), propagation.getCarry().hashCode());	}

	@Override
	public Integer visit(SubContextInitialization subConclusion, Void input) {
		return subConclusion.getConclusionSubRoot().hashCode();
	}

	@Override
	public Integer visit(ContextInitialization conclusion, Void input) {
		return conclusion.hashCode();
	}

	@Override
	public Integer visit(DisjointSubsumer conclusion, Void input) {
		return HashGenerator.combineListHash(conclusion.getDisjointExpressions().hashCode(), conclusion.getPosition());
	}

	

}
