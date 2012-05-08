/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedNominalVisitor;

public class IndexedNominal extends IndexedClassExpression {
	/**
	 * The ElkNamedIndividual that is the sole instance of this nominal
	 */
	protected final ElkNamedIndividual elkNamedIndividual;
	
	protected int occurrenceNo = 0;

	protected IndexedNominal(ElkNamedIndividual elkNamedIndividual) {
		this.elkNamedIndividual = elkNamedIndividual;
	}

	/**
	 * @return The represented ElkNamedIndividual.
	 */
	public ElkNamedIndividual getElkNamedIndividual() {
		return elkNamedIndividual;
	}

	public <O> O accept(IndexedNominalVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {
		occurrenceNo += increment;
		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;
	}
	
	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public String toString() {
		return "ObjectOneOf(<"+elkNamedIndividual.getIri().asString()+">)";
	}

}
