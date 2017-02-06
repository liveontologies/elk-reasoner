package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
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

/**
 * An {@link IndexedSubClassOfAxiomInference} that can be modified as a result
 * of updating the {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ModifiableIndexedSubClassOfAxiomInference extends
		ModifiableIndexedAxiomInference, IndexedSubClassOfAxiomInference {

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	ModifiableIndexedSubClassOfAxiom getConclusion(
			ModifiableIndexedSubClassOfAxiom.Factory factory);

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory
			extends ModifiableElkClassAssertionAxiomConversion.Factory,
			ModifiableElkDifferentIndividualsAxiomBinaryConversion.Factory,
			ModifiableElkDisjointClassesAxiomBinaryConversion.Factory,
			ModifiableElkDisjointUnionAxiomOwlNothingConversion.Factory,
			ModifiableElkDisjointUnionAxiomSubClassConversion.Factory,
			ModifiableElkDisjointUnionAxiomBinaryConversion.Factory,
			ModifiableElkEquivalentClassesAxiomSubClassConversion.Factory,
			ModifiableElkObjectPropertyAssertionAxiomConversion.Factory,
			ModifiableElkObjectPropertyDomainAxiomConversion.Factory,
			ModifiableElkReflexiveObjectPropertyAxiomConversion.Factory,
			ModifiableElkSameIndividualAxiomConversion.Factory,
			ModifiableElkSubClassOfAxiomConversion.Factory {

		// combined interface

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O>
			extends ModifiableElkClassAssertionAxiomConversion.Visitor<O>,
			ModifiableElkDifferentIndividualsAxiomBinaryConversion.Visitor<O>,
			ModifiableElkDisjointClassesAxiomBinaryConversion.Visitor<O>,
			ModifiableElkDisjointUnionAxiomOwlNothingConversion.Visitor<O>,
			ModifiableElkDisjointUnionAxiomSubClassConversion.Visitor<O>,
			ModifiableElkDisjointUnionAxiomBinaryConversion.Visitor<O>,
			ModifiableElkEquivalentClassesAxiomSubClassConversion.Visitor<O>,
			ModifiableElkObjectPropertyAssertionAxiomConversion.Visitor<O>,
			ModifiableElkObjectPropertyDomainAxiomConversion.Visitor<O>,
			ModifiableElkReflexiveObjectPropertyAxiomConversion.Visitor<O>,
			ModifiableElkSameIndividualAxiomConversion.Visitor<O>,
			ModifiableElkSubClassOfAxiomConversion.Visitor<O> {

		// combined interface

	}

	<O> O accept(Visitor<O> visitor);

}
