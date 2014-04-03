package org.semanticweb.elk.alc.saturation;

/*
 * #%L
 * ALC Reasoner
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

import static org.junit.Assert.fail;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedAxiom;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.alc.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.util.collections.Operations.Condition;

public class SubsumptionCheckingAxiomVisitor implements
		IndexedAxiomVisitor<Void> {

	private final Condition<IndexedSubClassOfAxiom> condition_;

	public SubsumptionCheckingAxiomVisitor(Condition<IndexedSubClassOfAxiom> condition) {
		condition_ = condition;
	}
	
	Void failVisit(IndexedAxiom axiom) {
		fail("Condition violated: " + condition_ + ": " + axiom);
		return null;
	}

	@Override
	public Void visit(IndexedSubClassOfAxiom axiom) {
		if (!condition_.holds(axiom)) {
			return failVisit(axiom);
		}
		
		return null;
	}

	@Override
	public Void visit(IndexedDisjointnessAxiom axiom) {
		//no-op
		return null;
	}

}
