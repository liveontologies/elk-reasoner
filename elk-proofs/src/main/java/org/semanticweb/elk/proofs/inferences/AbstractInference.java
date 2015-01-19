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
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.utils.InferencePrinter;
import org.semanticweb.elk.proofs.utils.TautologyChecker;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Operations;

/**
 * The base abstract class of all inferences.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractInference implements Inference {

	private List<DerivedExpression> premises_; 
	
	@Override
	public final Collection<? extends DerivedExpression> getPremises() {
		if (premises_ != null) {
			return premises_;
		}
		// TODO move this to a transformation
		premises_ = new LinkedList<DerivedExpression>();
		// filtering out tautologies
		final TautologyChecker checker = new TautologyChecker();
		
		for (DerivedExpression expr : Operations.filter(getRawPremises(), new Condition<DerivedExpression>() {

			@Override
			public boolean holds(DerivedExpression premise) {
				return !premise.accept(checker, null);
			}
			
		})) {
			premises_.add(expr);
		}
		
		return premises_;
	}
	
	@Override
	public String toString() {
		return InferencePrinter.print(this);
	}

	protected abstract Iterable<DerivedExpression> getRawPremises();
}
