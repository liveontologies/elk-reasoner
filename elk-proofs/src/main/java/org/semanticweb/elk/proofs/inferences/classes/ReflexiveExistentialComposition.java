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
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpressionFactory;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.proofs.utils.InferencePrinter;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexiveExistentialComposition extends
		AbstractClassInference {

	private final DerivedExpression reflexPremise_;
	
	private final DerivedExpression subsumerPremise_;
	
	private final DerivedExpression propertyPremise_;
	
	public ReflexiveExistentialComposition(ElkClassExpression sub,
			ElkSubClassOfAxiom subsumerPremise,
			ElkReflexiveObjectPropertyAxiom reflPremise,
			ElkSubObjectPropertyOfAxiom propPremise, ElkObjectFactory factory, DerivedExpressionFactory exprFactory) {
		super(getConclusion(sub, subsumerPremise, propPremise, factory), exprFactory);
		
		reflexPremise_ = exprFactory.create(reflPremise);
		subsumerPremise_ = exprFactory.create(subsumerPremise);
		propertyPremise_ = exprFactory.create(propPremise);
	}

	private static ElkSubClassOfAxiom getConclusion(ElkClassExpression sub, ElkSubClassOfAxiom subPremise, ElkSubObjectPropertyOfAxiom propPremise, ElkObjectFactory factory) {
		ElkObjectProperty supProperty = propPremise.getSuperObjectPropertyExpression().accept(new ElkObjectPropertyExpressionVisitor<ElkObjectProperty>() {

			@Override
			public ElkObjectProperty visit(ElkObjectInverseOf elkObjectInverseOf) {
				throw new IllegalArgumentException("Inverses aren't in EL");
			}

			@Override
			public ElkObjectProperty visit(ElkObjectProperty elkObjectProperty) {
				return elkObjectProperty;
			}
			
		});
		
		ElkObjectSomeValuesFrom existential = factory.getObjectSomeValuesFrom(supProperty, subPremise.getSuperClassExpression());
		
		return factory.getSubClassOfAxiom(sub, existential);
	}
	
	@Override
	public <I, O> O accept(InferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public Collection<? extends DerivedExpression> getPremises() {
		return Arrays.asList(reflexPremise_, subsumerPremise_, propertyPremise_);
	}

	@Override
	public String toString() {
		return InferencePrinter.print(this);
	}
}
