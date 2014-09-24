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
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.inferences.Printer;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ChainSubsumption implements Inference {

	private final Expression firstPremise_;

	private final Expression secondPremise_;

	private final Expression conclusion_;

	private final ElkSubObjectPropertyOfAxiom sideCondition_;

	// one premise and a side condition
	public ChainSubsumption(
			ElkSubObjectPropertyOfAxiom conclusion,
			ElkSubObjectPropertyOfAxiom premise, 
			ElkSubObjectPropertyOfAxiom sideCondition) {
		firstPremise_ = new SingleAxiomExpression(premise);
		secondPremise_ = null;
		conclusion_ = new SingleAxiomExpression(conclusion);
		sideCondition_ = sideCondition;
	}

	// two premises and no side condition
	public ChainSubsumption(
			ElkSubObjectPropertyOfAxiom conclusion,
			Expression first, 
			ElkSubObjectPropertyOfAxiom second) {
		firstPremise_ = first;
		secondPremise_ = new SingleAxiomExpression(second);
		conclusion_ = new SingleAxiomExpression(conclusion);
		sideCondition_ = null;
	}

	@Override
	public Collection<? extends Expression> getPremises() {
		if (secondPremise_ == null) {
			return Collections.singletonList(firstPremise_);
		}

		return Arrays.asList(firstPremise_, secondPremise_);
	}

	@Override
	public Expression getConclusion() {
		return conclusion_;
	}

	@Override
	public SideCondition getSideCondition() {
		return sideCondition_ == null ? null
				: new AxiomPresenceCondition<ElkSubObjectPropertyOfAxiom>(
						sideCondition_);
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return Printer.print(this);
	}
}
