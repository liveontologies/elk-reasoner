package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkClassAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDeclarationAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDifferentIndividualsAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDifferentIndividualsAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointClassesAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointClassesAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomBinaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomNaryConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomOwlNothingConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkDisjointUnionAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentClassesAxiomEquivalenceConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentClassesAxiomSubClassConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkEquivalentObjectPropertiesAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkObjectPropertyAssertionAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkObjectPropertyDomainAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkObjectPropertyRangeAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkReflexiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSameIndividualAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSubClassOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkSubObjectPropertyOfAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableElkTransitiveObjectPropertyAxiomConversion;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;

public class ModifiableIndexedAxiomInferenceConclusionVisitor<O>
		implements ModifiableIndexedAxiomInference.Visitor<O> {

	private final ModifiableIndexedAxiom.Factory conclusionFactory_;

	private final ModifiableIndexedAxiom.Visitor<O> conclusionVisitor_;

	public ModifiableIndexedAxiomInferenceConclusionVisitor(
			ModifiableIndexedAxiom.Factory conclusionFactory,
			ModifiableIndexedAxiom.Visitor<O> conclusionVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionVisitor_ = conclusionVisitor;
	}

	public ModifiableIndexedAxiomInferenceConclusionVisitor(
			ModifiableIndexedAxiom.Visitor<O> conclusionVisitor) {
		this(new ModifiableIndexedObjectBaseFactory(), conclusionVisitor);
	}

	public ModifiableIndexedAxiomInferenceConclusionVisitor(
			ModifiableIndexedAxiom.Factory conclusionFactory) {
		this(conclusionFactory, new ModifiableIndexedAxiomDummyVisitor<O>());
	}

	@Override
	public O visit(
			ModifiableElkDifferentIndividualsAxiomNaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkDisjointClassesAxiomNaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkDisjointUnionAxiomNaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkClassAssertionAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkDifferentIndividualsAxiomBinaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkDisjointClassesAxiomBinaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkDisjointUnionAxiomOwlNothingConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkDisjointUnionAxiomSubClassConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkDisjointUnionAxiomBinaryConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkEquivalentClassesAxiomSubClassConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkObjectPropertyAssertionAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkObjectPropertyDomainAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkReflexiveObjectPropertyAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkSameIndividualAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkSubClassOfAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkDisjointUnionAxiomEquivalenceConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkEquivalentClassesAxiomEquivalenceConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkEquivalentObjectPropertiesAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkSubObjectPropertyOfAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(
			ModifiableElkTransitiveObjectPropertyAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkObjectPropertyRangeAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

	@Override
	public O visit(ModifiableElkDeclarationAxiomConversion inference) {
		return conclusionVisitor_
				.visit(inference.getConclusion(conclusionFactory_));
	}

}
