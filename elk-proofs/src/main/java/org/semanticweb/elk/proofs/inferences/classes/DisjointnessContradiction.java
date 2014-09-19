/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.classes;
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

import java.util.Arrays;
import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DisjointnessContradiction extends
		AbstractClassInference {

	private final Expression<?> firstPremise_;
	
	private final Expression<?> secondPremise_;
	
	private final ElkDisjointClassesAxiom sideCondition_;
	
	DisjointnessContradiction(ElkClassExpression sub, Expression<?> firstPremise, Expression<?> secondPremise, ElkDisjointClassesAxiom sideCondition, ElkObjectFactory factory) {
		super(factory.getSubClassOfAxiom(sub, PredefinedElkClass.OWL_NOTHING));

		firstPremise_ = firstPremise;
		secondPremise_ = secondPremise;
		sideCondition_ = sideCondition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Expression<?>> getPremises() {
		return Arrays.asList(firstPremise_, secondPremise_);
	}
	
	

	@Override
	public SideCondition getSideCondition() {
		return new AxiomPresenceCondition<ElkDisjointClassesAxiom>(sideCondition_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
