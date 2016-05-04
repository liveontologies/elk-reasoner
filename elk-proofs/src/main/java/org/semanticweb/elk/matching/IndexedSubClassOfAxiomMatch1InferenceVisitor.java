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

import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.indexing.model.ElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomOwlNothingConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;

class IndexedSubClassOfAxiomMatch1InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<IndexedSubClassOfAxiomMatch1>
		implements IndexedSubClassOfAxiomInference.Visitor<Void> {

	IndexedSubClassOfAxiomMatch1InferenceVisitor(InferenceMatch.Factory factory,
			IndexedSubClassOfAxiomMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(ElkClassAssertionAxiomConversion inference) {
		factory.getElkClassAssertionAxiomConversionMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(ElkDifferentIndividualsAxiomBinaryConversion inference) {
		factory.getElkDifferentIndividualsAxiomBinaryConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkDisjointClassesAxiomBinaryConversion inference) {
		factory.getElkDisjointClassesAxiomBinaryConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkDisjointUnionAxiomSubClassConversion inference) {
		factory.getElkDisjointUnionAxiomSubClassConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkDisjointUnionAxiomBinaryConversion inference) {
		factory.getElkDisjointUnionAxiomBinaryConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkEquivalentClassesAxiomSubClassConversion inference) {
		factory.getElkEquivalentClassesAxiomSubClassConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkObjectPropertyAssertionAxiomConversion inference) {
		factory.getElkObjectPropertyAssertionAxiomConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkObjectPropertyDomainAxiomConversion inference) {
		factory.getElkObjectPropertyDomainAxiomConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkReflexiveObjectPropertyAxiomConversion inference) {
		factory.getElkReflexiveObjectPropertyAxiomConversionMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ElkSameIndividualAxiomConversion inference) {
		factory.getElkSameIndividualAxiomConversionMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(ElkSubClassOfAxiomConversion inference) {
		factory.getElkSubClassOfAxiomConversionMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(ElkDisjointUnionAxiomOwlNothingConversion inference) {
		// TODO Auto-generated method stub
		return null;
	}

}
