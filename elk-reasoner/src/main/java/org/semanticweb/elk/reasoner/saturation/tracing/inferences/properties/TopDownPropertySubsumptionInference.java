/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Property hierarchy inference based on a told sub-property subsumption.
 * R <= S if H <= S and R is a told sub-property of H. This class stores H as the premise.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TopDownPropertySubsumptionInference extends SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>
		implements ObjectPropertyInference {

	private final IndexedObjectProperty premise_;
	
	public TopDownPropertySubsumptionInference(IndexedPropertyChain chain,
			IndexedPropertyChain sup, IndexedObjectProperty premise) {
		super(chain, sup);
		premise_ = premise;
	}

	public SubPropertyChain<IndexedObjectProperty, IndexedPropertyChain> getPremise() {
		return new SubPropertyChain<IndexedObjectProperty, IndexedPropertyChain>(premise_, getSuperPropertyChain());
	}
	
	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public String toString() {
		return "Sub-chain: " + getSubPropertyChain() + " => " + getSuperPropertyChain() + ", premise: " + premise_ + " => " + getSuperPropertyChain();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TopDownPropertySubsumptionInference)) {
			return false;
		}
		
		TopDownPropertySubsumptionInference inf = (TopDownPropertySubsumptionInference) obj;
		
		return premise_.equals(inf.premise_) && getSubPropertyChain().equals(inf.getSubPropertyChain()) && getSuperPropertyChain().equals(inf.getSuperPropertyChain());
	}

	@Override
	public int hashCode() {
		return HashGenerator.combineListHash(premise_.hashCode(), getSubPropertyChain().hashCode(), getSuperPropertyChain().hashCode());
	}
}
