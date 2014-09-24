/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.properties;
/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.proofs.expressions.CartesianExpression;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexiveComposition implements Inference {

	private final Expression firstReflexive_;
	
	private final Expression secondReflexive_;
	
	private final Expression conclusion_;
	
	@SuppressWarnings("unchecked")
	public ReflexiveComposition(ElkReflexiveObjectPropertyAxiom firstRefl, Expression secondRefl) {
		firstReflexive_ = new SingleAxiomExpression(firstRefl);
		secondReflexive_ = secondRefl;
		
		conclusion_ = new CartesianExpression(Arrays.asList(firstReflexive_.getExplanations(), secondReflexive_.getExplanations()));
	}
	
	@Override
	public Collection<? extends Expression> getPremises() {
		return Arrays.asList(firstReflexive_, secondReflexive_);
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Expression getConclusion() {
		return conclusion_;
	}

	@Override
	public SideCondition getSideCondition() {
		return null;
	}

}
