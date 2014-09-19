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

import java.util.Collection;
import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.proofs.expressions.Expression;
import org.semanticweb.elk.proofs.expressions.SingleAxiomExpression;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.sideconditions.AxiomPresenceCondition;
import org.semanticweb.elk.proofs.sideconditions.SideCondition;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ClassSubsumption extends AbstractClassInference {

	private final Expression<ElkSubClassOfAxiom> premise_;

	private final SideCondition sideCondition_;

	// we aren't yet checking correctness (or mutual consistency) of these
	// parameters. Perhaps we could have an inference factory which would do it.
	public ClassSubsumption(ElkAxiom sideCondition,
			ElkClassExpression sub, ElkClassExpression sup,
			ElkClassExpression premise, ElkObjectFactory factory) {
		super(factory.getSubClassOfAxiom(sub, sup));

		premise_ = new SingleAxiomExpression<ElkSubClassOfAxiom>(factory.getSubClassOfAxiom(sub, premise));
		sideCondition_ = new AxiomPresenceCondition<ElkAxiom>(sideCondition);
	}

	@Override
	public Collection<Expression<ElkSubClassOfAxiom>> getPremises() {
		return Collections.<Expression<ElkSubClassOfAxiom>>singletonList(premise_);
	}

	@Override
	public SideCondition getSideCondition() {
		return sideCondition_;
	}

	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		//return visitor.visit(this);
		return null;
	}

}
