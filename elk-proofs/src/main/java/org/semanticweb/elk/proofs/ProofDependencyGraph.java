/**
 * 
 */
package org.semanticweb.elk.proofs;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ProofDependencyGraph {

	private final Map<DerivedExpression, Set<DerivedExpression>> depMap_ = new HashMap<DerivedExpression, Set<DerivedExpression>>();
	
	public Set<DerivedExpression> updateDependencies(DerivedExpression expression, Iterable<DerivedExpression> dependencies) {
		Set<DerivedExpression> existingDeps = depMap_.get(expression);
		
		if (existingDeps == null) {
			existingDeps = new HashSet<DerivedExpression>();
			
			for (DerivedExpression dep : dependencies) {
				existingDeps.add(dep);
			}
			
			depMap_.put(expression, existingDeps);
		}
		else {
			for (DerivedExpression dep : dependencies) {
				existingDeps.remove(dep);
			}
		}
		
		return existingDeps;
	}
	
	public Collection<DerivedExpression> getExpressions() {
		return depMap_.keySet();
	}

	public Set<DerivedExpression> getDependencies(DerivedExpression expression) {
		Set<DerivedExpression> existingDeps = depMap_.get(expression);
		
		return existingDeps == null ? Collections.<DerivedExpression>emptySet() : existingDeps;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (DerivedExpression expr : depMap_.keySet()) {
			builder.append(expr + " => " + depMap_.get(expr) + "\n");
		}
		
		return builder.toString();
	}
}
