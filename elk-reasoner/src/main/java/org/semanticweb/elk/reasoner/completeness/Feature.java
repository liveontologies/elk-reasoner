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
package org.semanticweb.elk.reasoner.completeness;

/**
 * A syntactic element, such as a keyword or an entity, occurrences of which are
 * important for completeness of reasoning results.
 * 
 * @author Yevgeny Kazakov
 */
public enum Feature {

	//
	ANONYMOUS_INDIVIDUAL("AnonymousIndividual"),
	//
	ASYMMETRIC_OBJECT_PROPERTY("AsymmetricObjectProperty"),
	//
	BOTTOM_OBJECT_PROPERTY_POSITIVE("owl:bottomObjectProperty",
			Polarity.POSITIVE),
	//
	DATA_ALL_VALUES_FROM("DataAllValuesFrom"),
	//
	DATA_EXACT_CARDINALITY("DataExactCardinality"),
	//
	DATA_HAS_VALUE("DataHasValue"),
	//
	DATA_MAX_CARDINALITY("DataMaxCardinality"),
	//
	DATA_MIN_CARDINALITY("DataMinCardinality"),
	//
	DATA_PROPERTY("DataProperty"),
	//
	DATA_PROPERTY_ASSERTION("DataPropertyAssertion"),
	//
	DATA_PROPERTY_DOMAIN("DataPropertyDomain"),
	//
	DATA_PROPERTY_RANGE("DataPropertyRange"),
	//
	DATA_SOME_VALUES_FROM("DataSomeValuesFrom"),
	//
	DATATYPE("Datatype"),
	//
	DATATYPE_DEFINITION("DatatypeDefinition"),
	//
	DISJOINT_CLASSES("DisjointClasses"),
	//
	DISJOINT_DATA_PROPERTIES("DisjointDataProperties"),
	//
	DISJOINT_OBJECT_PROPERTIES("DisjointObjectProperties"),
	//
	DISJOINT_UNION("DisjointUnion"),
	//
	EQUIVALENT_DATA_PROPERTIES("EquivalentDataProperties"),
	//
	FUNCTIONAL_DATA_PROPERTY("FunctionalDataProperty"),
	//
	FUNCTIONAL_OBJECT_PROPERTY("FunctionalObjectProperty"),
	//
	HAS_KEY("HasKey"),
	//
	INVERSE_FUNCTIONAL_OBJECT_PROPERTY("InverseFunctionalObjectProperty"),
	//
	INVERSE_OBJECT_PROPERTIES("InverseObjectProperties"),
	//
	IRREFLEXIVE_OBJECT_PROPERTY("IrreflexiveObjectProperty"),
	//
	NEGATIVE_DATA_PROPERTY_ASSERTION("NegativeDataPropertyAssertion"),
	//
	NEGATIVE_OBJECT_PROPERTY_ASSERTION("NegativeObjectPropertyAssertion"),
	//
	OBJECT_ALL_VALUES_FROM("ObjectAllValuesFrom"),
	//
	OBJECT_COMPLEMENT_OF_NEGATIVE("ObjectComplementOf", Polarity.NEGATIVE),
	//
	OBJECT_COMPLEMENT_OF_POSITIVE("ObjectComplementOf", Polarity.POSITIVE),
	//
	OBJECT_EXACT_CARDINALITY("ObjectExactCardinality"),
	//
	OBJECT_HAS_SELF("ObjectHasSelf"),
	//
	OBJECT_INVERSE_OF("ObjectInverseOf"),
	//
	OBJECT_MAX_CARDINALITY("ObjectMaxCardinality"),
	//
	OBJECT_MIN_CARDINALITY("ObjectMinCardinality"),
	//
	OBJECT_ONE_OF("ObjectOneOf"),
	//
	OBJECT_PROPERTY_ASSERTION("ObjectPropertyAssertion"),
	//
	OBJECT_PROPERTY_CHAIN("ObjectPropertyChain"),
	//
	OBJECT_PROPERTY_RANGE("ObjectPropertyRange"),
	//
	OBJECT_UNION_OF_POSITIVE("ObjectUnionOf", Polarity.POSITIVE),
	OWL_NOTHING_POSITIVE("owl:Nothing", Polarity.POSITIVE),
	//
	REFLEXIVE_OBJECT_PROPERTY("ReflexiveObjectProperty"),
	//
	SUB_DATA_PROPERTY_OF("SubDataPropertyOf"),
	//
	SWRL_RULE("SWRLRule"),
	//
	SYMMETRIC_OBJECT_PROPERTY("SymmetricObjectProperty"),
	//
	TOP_OBJECT_PROPERTY_NEGATIVE("owl:topObjectProperty", Polarity.NEGATIVE);

	public static enum Polarity {
		ANY, NEGATIVE, POSITIVE
	}

	private final String constructor_;

	private final Polarity polarity_;

	Feature(String constructor) {
		this(constructor, Polarity.ANY);
	}

	Feature(String constructor, Polarity polarity) {
		this.constructor_ = constructor;
		this.polarity_ = polarity;
	}

	public String getConstructor() {
		return constructor_;
	}

	public Polarity getPolarity() {
		return polarity_;
	}

	@Override
	public String toString() {
		switch (polarity_) {
		case POSITIVE:
			return "Positive occurrences of " + constructor_;
		case NEGATIVE:
			return "Negative occurrences of " + constructor_;
		case ANY:
			return "Occurrences of " + constructor_;
		}
		throw new RuntimeException("Incomplete cases");
	}

}
