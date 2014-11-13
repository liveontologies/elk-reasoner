package org.semanticweb.elk.benchmark.tracing;
/*
 * #%L
 * ELK Benchmarking Package
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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.util.collections.Pair;

/**
 * Counts the number of distinct SubClassOf axioms in visited inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionCollector extends UsedInferencesCounter {

	private Set<Pair<IndexedClassExpression, IndexedClassExpression>> subclassAxioms_ = new HashSet<Pair<IndexedClassExpression, IndexedClassExpression>>();
	
	@Override
	public Boolean visit(SubClassOfSubsumer<?> conclusion, IndexedClassExpression input) {
		subclassAxioms_.add(new Pair<IndexedClassExpression, IndexedClassExpression>(((Subsumer<?>)conclusion.getPremise()).getExpression(), conclusion.getExpression()));
		
		return super.visit(conclusion, input);
	}
	
	public Set<Pair<IndexedClassExpression, IndexedClassExpression>> getSubClassOfAxioms() {
		return subclassAxioms_;
	}
}