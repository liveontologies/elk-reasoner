/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import java.util.Iterator;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.util.collections.entryset.StrongKeyEntry;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * TODO
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class InferenceEntry<I extends Inference> extends StrongKeyEntry<I, I> {

	public InferenceEntry(I inf) {
		super(inf);
	}

	@Override
	public int computeHashCode() {
		return HashGenerator.combineListHash(key.getRule().hashCode(), HashGenerator.combinedHashCode(key.getPremises()), key.getConclusion().hashCode());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (object == null || !(object instanceof InferenceEntry)) {
			return false;
		}
		
		Inference otherInf = ((InferenceEntry<?>) object).key;
		
		return key.getRule().equals(otherInf.getRule()) && key.getConclusion().equals(otherInf.getConclusion()) && equal(key.getPremises(), otherInf.getPremises());
	}

	private boolean equal(Collection<? extends DerivedExpression> premises, Collection<? extends DerivedExpression> other) {
		Iterator<? extends DerivedExpression> premiseIter = premises.iterator();
		Iterator<? extends DerivedExpression> otherIter = other.iterator();
		
		while (premiseIter.hasNext()) {
			if (!otherIter.hasNext()) {
				return false;
			}
			
			DerivedExpression premise = premiseIter.next();
			DerivedExpression otherPremise = otherIter.next();
			
			if (!premise.equals(otherPremise)) {
				return false;
			}
		}
		
		return !otherIter.hasNext();
	}

}
