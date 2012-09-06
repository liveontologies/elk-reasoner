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

import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.indexing.rules.RuleEngine;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedDataHasValueVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class IndexedDataHasValue extends IndexedClassExpression {

	protected final ElkDataProperty property;
	protected final ElkLiteral filler;

	protected IndexedDataHasValue(ElkDataHasValue elkDataHasValue) {
		this.property = (ElkDataProperty) elkDataHasValue.getProperty();
		this.filler = elkDataHasValue.getFiller();
	}

	public ElkDataProperty getRelation() {
		return property;
	}

	public ElkLiteral getFiller() {
		return filler;
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {
		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;
	}

	public <O> O accept(IndexedDataHasValueVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedDataHasValueVisitor<O>) visitor);
	}

	@Override
	public String toString() {
		return "DataHasValue(" + '<' + this.property.getIri().getFullIriAsString()
				+ "> \"" + this.filler.getLexicalForm() + "\"^^<"
				+ this.filler.getDatatype().getIri().getFullIriAsString() + ">)";
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {
		// TODO Auto-generated method stub
		
	}
}
