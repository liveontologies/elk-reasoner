package org.semanticweb.elk.matching;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiomInference;

class IndexedSubObjectPropertyOfAxiomMatch1InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<IndexedSubObjectPropertyOfAxiomMatch1>
		implements IndexedSubObjectPropertyOfAxiomInference.Visitor<Void> {

	IndexedSubObjectPropertyOfAxiomMatch1InferenceVisitor(
			InferenceMatch.Factory factory,
			IndexedSubObjectPropertyOfAxiomMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(ElkEquivalentObjectPropertiesAxiomConversion inference) {
		factory.getElkEquivalentObjectPropertiesAxiomConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkSubObjectPropertyOfAxiomConversion inference) {
		factory.getElkSubObjectPropertyOfAxiomConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkTransitiveObjectPropertyAxiomConversion inference) {
		factory.getElkTransitiveObjectPropertyAxiomConversionMatch1(inference,
				child);
		return null;
	}

}
