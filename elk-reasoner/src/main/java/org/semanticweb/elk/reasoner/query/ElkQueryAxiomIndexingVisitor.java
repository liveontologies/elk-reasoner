package org.semanticweb.elk.reasoner.query;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedFeature;

/**
 * A {@link ElkAxiomVisitor} that throws a {@link ElkIndexingUnsupportedFeature}
 * for {@link ElkAxiom} that are not supported for indexing
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output of this visitor
 * 
 */
public class ElkQueryAxiomIndexingVisitor<O> implements ElkAxiomVisitor<O> {

	/**
	 * @param axiom
	 *            the visited {@link ElkAxiom}
	 * @return the output of the visitor ({@code null} by default)
	 */
	protected O defaultVisit(ElkAxiom axiom) {
		return null;
	}

	@Override
	public O visit(ElkAnnotationAssertionAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_ANNOTATION_ASSERTION_AXIOM);
	}

	@Override
	public O visit(ElkAnnotationPropertyDomainAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_ANNOTATION_PROPERTY_DOMAIN_AXIOM);
	}

	@Override
	public O visit(ElkAnnotationPropertyRangeAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_ANNOTATION_PROPERTY_RANGE_AXIOM);
	}

	@Override
	public O visit(ElkAsymmetricObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_ASYMMETRIC_OBJECT_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkClassAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDataPropertyAssertionAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DATA_PROPERTY_ASSERTION_AXIOM);
	}

	@Override
	public O visit(ElkDataPropertyDomainAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DATA_PROPERTY_DOMAIN_AXIOM);
	}

	@Override
	public O visit(ElkDataPropertyRangeAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DATA_PROPERTY_RANGE_AXIOM);
	}

	@Override
	public O visit(ElkDatatypeDefinitionAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DATATYPE_DEFINITION_AXIOM);
	}

	@Override
	public O visit(ElkDeclarationAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DECLARATION_AXIOM);
	}

	@Override
	public O visit(ElkDifferentIndividualsAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDisjointClassesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkDisjointDataPropertiesAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DISJOINT_DATA_PROPERTIES_AXIOM);
	}

	@Override
	public O visit(ElkDisjointObjectPropertiesAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DISJOINT_OBJECT_PROPERTIES_AXIOM);
	}

	@Override
	public O visit(ElkDisjointUnionAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_DISJOINT_UNION_AXIOM);
	}

	@Override
	public O visit(ElkEquivalentClassesAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkEquivalentDataPropertiesAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_EQUIVALENT_DATA_PROPERTIES_AXIOM);
	}

	@Override
	public O visit(ElkEquivalentObjectPropertiesAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_EQUIVALENT_OBJECT_PROPERTIES_AXIOM);
	}

	@Override
	public O visit(ElkFunctionalDataPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_FUNCTIONAL_DATA_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkFunctionalObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_FUNCTIONAL_OBJECT_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkHasKeyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(Feature.QUERY_HAS_KEY_AXIOM);
	}

	@Override
	public O visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_INVERSE_FUNCTIONAL_OBJECT_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkInverseObjectPropertiesAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_INVERSE_OBJECT_PROPERTIES_AXIOM);
	}

	@Override
	public O visit(ElkIrreflexiveObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_IRREFLEXIVE_OBJECT_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkNegativeDataPropertyAssertionAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_NEGATIVE_DATA_PROPERTY_ASSERTION_AXIOM);
	}

	@Override
	public O visit(ElkNegativeObjectPropertyAssertionAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_NEGATIVE_OBJECT_PROPERTY_ASSERTION_AXIOM);
	}

	@Override
	public O visit(ElkObjectPropertyAssertionAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyDomainAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkObjectPropertyRangeAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_OBJECT_PROPERTY_RANGE_AXIOM);
	}

	@Override
	public O visit(ElkReflexiveObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_REFLEXIVE_OBJECT_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkSameIndividualAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSubAnnotationPropertyOfAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_SUB_ANNOTATION_PROPERTY_OF_AXIOM);
	}

	@Override
	public O visit(ElkSubClassOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(ElkSubDataPropertyOfAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_SUB_DATA_PROPERTY_OF_AXIOM);
	}

	@Override
	public O visit(ElkSubObjectPropertyOfAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_SUB_OBJECT_PROPERTY_OF_AXIOM);
	}

	@Override
	public O visit(ElkSWRLRule axiom) {
		throw new ElkIndexingUnsupportedFeature(Feature.QUERY_SWRL_RULE);
	}

	@Override
	public O visit(ElkSymmetricObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_SYMMETRIC_OBJECT_PROPERTY_AXIOM);
	}

	@Override
	public O visit(ElkTransitiveObjectPropertyAxiom axiom) {
		throw new ElkIndexingUnsupportedFeature(
				Feature.QUERY_TRANSITIVE_OBJECT_PROPERTY_AXIOM);
	}

}
