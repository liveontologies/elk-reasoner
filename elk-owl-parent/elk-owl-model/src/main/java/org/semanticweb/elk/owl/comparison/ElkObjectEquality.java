package org.semanticweb.elk.owl.comparison;

/*
 * #%L
 * ELK OWL Object Interfaces
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

import java.util.Iterator;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAnnotation;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkDataMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkDataOneOf;
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkDataUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeDefinitionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentDataPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkHasKeyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectAllValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectExactCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectMinCardinalityUnqualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSWRLRule;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubAnnotationPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkAbbreviatedIri;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Static methods for computing syntactic equality between {@link ElkObject}s.
 * Two {@link ElkObject}s are syntactically equal if all their corresponding
 * values are syntactically equal.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ElkObjectHash
 */
public class ElkObjectEquality implements ElkObjectVisitor<ElkObject> {

	private final Object object_;

	private ElkObjectEquality(Object object) {
		this.object_ = object;
	}

	public static boolean equals(ElkObject first, Object second) {
		return first == null ? second == null
				: first.accept(new ElkObjectEquality(second)) == second;
	}

	public static boolean equals(List<? extends ElkObject> first,
			List<? extends ElkObject> second) {
		Iterator<? extends ElkObject> firstIterator = first.iterator();
		Iterator<? extends ElkObject> secondIterator = second.iterator();
		while (firstIterator.hasNext() && secondIterator.hasNext()) {
			if (equals(firstIterator.next(), secondIterator.next()))
				continue;
			return false;
		}
		return !(firstIterator.hasNext() || secondIterator.hasNext());
	}

	private static boolean equals(int first, int second) {
		return first == second;
	}

	private static boolean equals(String first, String second) {
		return first.equals(second);
	}

	@Override
	public ElkAnnotationAssertionAxiom visit(
			ElkAnnotationAssertionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkAnnotationAssertionAxiom) {
			ElkAnnotationAssertionAxiom result = (ElkAnnotationAssertionAxiom) object_;
			if (equals(result.getSubject(), axiom.getSubject())
					&& equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getValue(), axiom.getValue()))
				return result;
		}
		return null;
	}

	@Override
	public ElkAnnotationPropertyDomainAxiom visit(
			ElkAnnotationPropertyDomainAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkAnnotationPropertyDomainAxiom) {
			ElkAnnotationPropertyDomainAxiom result = (ElkAnnotationPropertyDomainAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getDomain(), axiom.getDomain()))
				return result;
		}
		return null;
	}

	@Override
	public ElkAnnotationPropertyRangeAxiom visit(
			ElkAnnotationPropertyRangeAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkAnnotationPropertyRangeAxiom) {
			ElkAnnotationPropertyRangeAxiom result = (ElkAnnotationPropertyRangeAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getRange(), axiom.getRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSubAnnotationPropertyOfAxiom visit(
			ElkSubAnnotationPropertyOfAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSubAnnotationPropertyOfAxiom) {
			ElkSubAnnotationPropertyOfAxiom result = (ElkSubAnnotationPropertyOfAxiom) object_;
			if (equals(result.getSubAnnotationProperty(),
					axiom.getSubAnnotationProperty())
					&& equals(result.getSuperAnnotationProperty(),
							axiom.getSuperAnnotationProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkClassAssertionAxiom visit(ElkClassAssertionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkClassAssertionAxiom) {
			ElkClassAssertionAxiom result = (ElkClassAssertionAxiom) object_;
			if (equals(result.getClassExpression(), axiom.getClassExpression())
					&& equals(result.getIndividual(), axiom.getIndividual()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDifferentIndividualsAxiom visit(
			ElkDifferentIndividualsAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDifferentIndividualsAxiom) {
			ElkDifferentIndividualsAxiom result = (ElkDifferentIndividualsAxiom) object_;
			if (equals(result.getIndividuals(), axiom.getIndividuals()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataPropertyAssertionAxiom visit(
			ElkDataPropertyAssertionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDataPropertyAssertionAxiom) {
			ElkDataPropertyAssertionAxiom result = (ElkDataPropertyAssertionAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getObject(), axiom.getObject())
					&& equals(result.getSubject(), axiom.getSubject()))
				return result;
		}
		return null;
	}

	@Override
	public ElkNegativeDataPropertyAssertionAxiom visit(
			ElkNegativeDataPropertyAssertionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkNegativeDataPropertyAssertionAxiom) {
			ElkNegativeDataPropertyAssertionAxiom result = (ElkNegativeDataPropertyAssertionAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getObject(), axiom.getObject())
					&& equals(result.getSubject(), axiom.getSubject()))
				return result;
		}
		return null;
	}

	@Override
	public ElkNegativeObjectPropertyAssertionAxiom visit(
			ElkNegativeObjectPropertyAssertionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkNegativeObjectPropertyAssertionAxiom) {
			ElkNegativeObjectPropertyAssertionAxiom result = (ElkNegativeObjectPropertyAssertionAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getObject(), axiom.getObject())
					&& equals(result.getSubject(), axiom.getSubject()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectPropertyAssertionAxiom visit(
			ElkObjectPropertyAssertionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkObjectPropertyAssertionAxiom) {

			ElkObjectPropertyAssertionAxiom result = (ElkObjectPropertyAssertionAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getObject(), axiom.getObject())
					&& equals(result.getSubject(), axiom.getSubject()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSameIndividualAxiom visit(ElkSameIndividualAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSameIndividualAxiom) {
			ElkSameIndividualAxiom result = (ElkSameIndividualAxiom) object_;
			if (equals(result.getIndividuals(), axiom.getIndividuals()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDisjointClassesAxiom visit(ElkDisjointClassesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDisjointClassesAxiom) {
			ElkDisjointClassesAxiom result = (ElkDisjointClassesAxiom) object_;
			if (equals(result.getClassExpressions(),
					axiom.getClassExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDisjointUnionAxiom visit(ElkDisjointUnionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDisjointUnionAxiom) {
			ElkDisjointUnionAxiom result = (ElkDisjointUnionAxiom) object_;
			if (equals(result.getDefinedClass(), axiom.getDefinedClass())
					&& equals(result.getClassExpressions(),
							axiom.getClassExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkEquivalentClassesAxiom visit(ElkEquivalentClassesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkEquivalentClassesAxiom) {
			ElkEquivalentClassesAxiom result = (ElkEquivalentClassesAxiom) object_;
			if (equals(result.getClassExpressions(),
					axiom.getClassExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSubClassOfAxiom visit(ElkSubClassOfAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSubClassOfAxiom) {
			ElkSubClassOfAxiom result = (ElkSubClassOfAxiom) object_;
			if (equals(result.getSubClassExpression(),
					axiom.getSubClassExpression())
					&& equals(result.getSuperClassExpression(),
							axiom.getSuperClassExpression()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataPropertyDomainAxiom visit(ElkDataPropertyDomainAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDataPropertyDomainAxiom) {
			ElkDataPropertyDomainAxiom result = (ElkDataPropertyDomainAxiom) object_;
			if (equals(result.getDomain(), axiom.getDomain())
					&& equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataPropertyRangeAxiom visit(ElkDataPropertyRangeAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDataPropertyRangeAxiom) {
			ElkDataPropertyRangeAxiom result = (ElkDataPropertyRangeAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getRange(), axiom.getRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDisjointDataPropertiesAxiom visit(
			ElkDisjointDataPropertiesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDisjointDataPropertiesAxiom) {
			ElkDisjointDataPropertiesAxiom result = (ElkDisjointDataPropertiesAxiom) object_;
			if (equals(result.getDataPropertyExpressions(),
					axiom.getDataPropertyExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkEquivalentDataPropertiesAxiom visit(
			ElkEquivalentDataPropertiesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkEquivalentDataPropertiesAxiom) {

			ElkEquivalentDataPropertiesAxiom result = (ElkEquivalentDataPropertiesAxiom) object_;
			if (equals(result.getDataPropertyExpressions(),
					axiom.getDataPropertyExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkFunctionalDataPropertyAxiom visit(
			ElkFunctionalDataPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkFunctionalDataPropertyAxiom) {
			ElkFunctionalDataPropertyAxiom result = (ElkFunctionalDataPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSubDataPropertyOfAxiom visit(ElkSubDataPropertyOfAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSubDataPropertyOfAxiom) {
			ElkSubDataPropertyOfAxiom result = (ElkSubDataPropertyOfAxiom) object_;
			if (equals(result.getSubDataPropertyExpression(),
					axiom.getSubDataPropertyExpression())
					&& equals(result.getSuperDataPropertyExpression(),
							axiom.getSuperDataPropertyExpression()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDatatypeDefinitionAxiom visit(ElkDatatypeDefinitionAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDatatypeDefinitionAxiom) {
			ElkDatatypeDefinitionAxiom result = (ElkDatatypeDefinitionAxiom) object_;
			if (equals(result.getDatatype(), axiom.getDatatype())
					&& equals(result.getDataRange(), axiom.getDataRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDeclarationAxiom visit(ElkDeclarationAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDeclarationAxiom) {
			ElkDeclarationAxiom result = (ElkDeclarationAxiom) object_;
			if (equals(result.getEntity(), axiom.getEntity()))
				return result;
		}
		return null;
	}

	@Override
	public ElkHasKeyAxiom visit(ElkHasKeyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkHasKeyAxiom) {
			ElkHasKeyAxiom result = (ElkHasKeyAxiom) object_;
			if (equals(result.getClassExpression(), axiom.getClassExpression())
					&& equals(result.getObjectPropertyExpressions(),
							axiom.getObjectPropertyExpressions())
					&& equals(result.getDataPropertyExpressions(),
							axiom.getDataPropertyExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkAsymmetricObjectPropertyAxiom visit(
			ElkAsymmetricObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkAsymmetricObjectPropertyAxiom) {
			ElkAsymmetricObjectPropertyAxiom result = (ElkAsymmetricObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDisjointObjectPropertiesAxiom visit(
			ElkDisjointObjectPropertiesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkDisjointObjectPropertiesAxiom) {
			ElkDisjointObjectPropertiesAxiom result = (ElkDisjointObjectPropertiesAxiom) object_;
			if (equals(result.getObjectPropertyExpressions(),
					axiom.getObjectPropertyExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkEquivalentObjectPropertiesAxiom visit(
			ElkEquivalentObjectPropertiesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkEquivalentObjectPropertiesAxiom) {
			ElkEquivalentObjectPropertiesAxiom result = (ElkEquivalentObjectPropertiesAxiom) object_;
			if (equals(result.getObjectPropertyExpressions(),
					axiom.getObjectPropertyExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkFunctionalObjectPropertyAxiom visit(
			ElkFunctionalObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkFunctionalObjectPropertyAxiom) {
			ElkFunctionalObjectPropertyAxiom result = (ElkFunctionalObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkInverseFunctionalObjectPropertyAxiom visit(
			ElkInverseFunctionalObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkInverseFunctionalObjectPropertyAxiom) {
			ElkInverseFunctionalObjectPropertyAxiom result = (ElkInverseFunctionalObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkInverseObjectPropertiesAxiom visit(
			ElkInverseObjectPropertiesAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkInverseObjectPropertiesAxiom) {
			ElkInverseObjectPropertiesAxiom result = (ElkInverseObjectPropertiesAxiom) object_;
			if (equals(result.getFirstObjectPropertyExpression(),
					axiom.getFirstObjectPropertyExpression())
					&& equals(result.getSecondObjectPropertyExpression(),
							axiom.getSecondObjectPropertyExpression()))
				return result;
		}
		return null;
	}

	@Override
	public ElkIrreflexiveObjectPropertyAxiom visit(
			ElkIrreflexiveObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkIrreflexiveObjectPropertyAxiom) {
			ElkIrreflexiveObjectPropertyAxiom result = (ElkIrreflexiveObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectPropertyDomainAxiom visit(
			ElkObjectPropertyDomainAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkObjectPropertyDomainAxiom) {
			ElkObjectPropertyDomainAxiom result = (ElkObjectPropertyDomainAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getDomain(), axiom.getDomain()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectPropertyRangeAxiom visit(
			ElkObjectPropertyRangeAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkObjectPropertyRangeAxiom) {
			ElkObjectPropertyRangeAxiom result = (ElkObjectPropertyRangeAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty())
					&& equals(result.getRange(), axiom.getRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkReflexiveObjectPropertyAxiom visit(
			ElkReflexiveObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkReflexiveObjectPropertyAxiom) {
			ElkReflexiveObjectPropertyAxiom result = (ElkReflexiveObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSubObjectPropertyOfAxiom visit(
			ElkSubObjectPropertyOfAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSubObjectPropertyOfAxiom) {
			ElkSubObjectPropertyOfAxiom result = (ElkSubObjectPropertyOfAxiom) object_;
			if (equals(result.getSubObjectPropertyExpression(),
					axiom.getSubObjectPropertyExpression())
					&& equals(result.getSuperObjectPropertyExpression(),
							axiom.getSuperObjectPropertyExpression()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSymmetricObjectPropertyAxiom visit(
			ElkSymmetricObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSymmetricObjectPropertyAxiom) {
			ElkSymmetricObjectPropertyAxiom result = (ElkSymmetricObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkTransitiveObjectPropertyAxiom visit(
			ElkTransitiveObjectPropertyAxiom axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkTransitiveObjectPropertyAxiom) {
			ElkTransitiveObjectPropertyAxiom result = (ElkTransitiveObjectPropertyAxiom) object_;
			if (equals(result.getProperty(), axiom.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkSWRLRule visit(ElkSWRLRule axiom) {
		if (object_ == axiom)
			return axiom;
		if (object_ instanceof ElkSWRLRule) {
			ElkSWRLRule result = (ElkSWRLRule) object_;
			// no fields, all SWRLRules are equal
			return result;
		}
		return null;
	}

	@Override
	public ElkClass visit(ElkClass expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkClass) {
			ElkClass result = (ElkClass) object_;
			if (equals(result.getIri(), expression.getIri()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataAllValuesFrom visit(ElkDataAllValuesFrom expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataAllValuesFrom) {
			ElkDataAllValuesFrom result = (ElkDataAllValuesFrom) object_;
			if (equals(result.getDataPropertyExpressions(),
					expression.getDataPropertyExpressions())
					&& equals(result.getDataRange(), expression.getDataRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataSomeValuesFrom visit(ElkDataSomeValuesFrom expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataSomeValuesFrom) {
			ElkDataSomeValuesFrom result = (ElkDataSomeValuesFrom) object_;
			if (equals(result.getDataPropertyExpressions(),
					expression.getDataPropertyExpressions())
					&& equals(result.getDataRange(), expression.getDataRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectComplementOf visit(ElkObjectComplementOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectComplementOf) {
			ElkObjectComplementOf result = (ElkObjectComplementOf) object_;
			if (equals(result.getClassExpression(),
					expression.getClassExpression()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectIntersectionOf visit(ElkObjectIntersectionOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectIntersectionOf) {
			ElkObjectIntersectionOf result = (ElkObjectIntersectionOf) object_;
			if (equals(result.getClassExpressions(),
					expression.getClassExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectOneOf visit(ElkObjectOneOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectOneOf) {
			ElkObjectOneOf result = (ElkObjectOneOf) object_;
			if (equals(result.getIndividuals(), expression.getIndividuals()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectUnionOf visit(ElkObjectUnionOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectUnionOf) {
			ElkObjectUnionOf result = (ElkObjectUnionOf) object_;
			if (equals(result.getClassExpressions(),
					expression.getClassExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataExactCardinalityQualified visit(
			ElkDataExactCardinalityQualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataExactCardinalityQualified) {
			ElkDataExactCardinalityQualified result = (ElkDataExactCardinalityQualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataMaxCardinalityQualified visit(
			ElkDataMaxCardinalityQualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataMaxCardinalityQualified) {
			ElkDataMaxCardinalityQualified result = (ElkDataMaxCardinalityQualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataMinCardinalityQualified visit(
			ElkDataMinCardinalityQualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataMinCardinalityQualified) {
			ElkDataMinCardinalityQualified result = (ElkDataMinCardinalityQualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectExactCardinalityQualified visit(
			ElkObjectExactCardinalityQualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectExactCardinalityQualified) {
			ElkObjectExactCardinalityQualified result = (ElkObjectExactCardinalityQualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectMaxCardinalityQualified visit(
			ElkObjectMaxCardinalityQualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectMaxCardinalityQualified) {
			ElkObjectMaxCardinalityQualified result = (ElkObjectMaxCardinalityQualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectMinCardinalityQualified visit(
			ElkObjectMinCardinalityQualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectMinCardinalityQualified) {
			ElkObjectMinCardinalityQualified result = (ElkObjectMinCardinalityQualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataExactCardinalityUnqualified visit(
			ElkDataExactCardinalityUnqualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataExactCardinalityUnqualified) {
			ElkDataExactCardinalityUnqualified result = (ElkDataExactCardinalityUnqualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataMaxCardinalityUnqualified visit(
			ElkDataMaxCardinalityUnqualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataMaxCardinalityUnqualified) {
			ElkDataMaxCardinalityUnqualified result = (ElkDataMaxCardinalityUnqualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataMinCardinalityUnqualified visit(
			ElkDataMinCardinalityUnqualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataMinCardinalityUnqualified) {
			ElkDataMinCardinalityUnqualified result = (ElkDataMinCardinalityUnqualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectExactCardinalityUnqualified visit(
			ElkObjectExactCardinalityUnqualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectExactCardinalityUnqualified) {
			ElkObjectExactCardinalityUnqualified result = (ElkObjectExactCardinalityUnqualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectMaxCardinalityUnqualified visit(
			ElkObjectMaxCardinalityUnqualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectMaxCardinalityUnqualified) {
			ElkObjectMaxCardinalityUnqualified result = (ElkObjectMaxCardinalityUnqualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectMinCardinalityUnqualified visit(
			ElkObjectMinCardinalityUnqualified expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectMinCardinalityUnqualified) {
			ElkObjectMinCardinalityUnqualified result = (ElkObjectMinCardinalityUnqualified) object_;
			if (equals(result.getCardinality(), expression.getCardinality())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
			return result;
		}
		return null;
	}

	@Override
	public ElkObjectHasSelf visit(ElkObjectHasSelf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectHasSelf) {
			ElkObjectHasSelf result = (ElkObjectHasSelf) object_;
			if (equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataHasValue visit(ElkDataHasValue expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataHasValue) {
			ElkDataHasValue result = (ElkDataHasValue) object_;
			if (equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectAllValuesFrom visit(ElkObjectAllValuesFrom expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectAllValuesFrom) {
			ElkObjectAllValuesFrom result = (ElkObjectAllValuesFrom) object_;
			if (equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectHasValue visit(ElkObjectHasValue expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectHasValue) {
			ElkObjectHasValue result = (ElkObjectHasValue) object_;
			if (equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectSomeValuesFrom visit(ElkObjectSomeValuesFrom expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectSomeValuesFrom) {
			ElkObjectSomeValuesFrom result = (ElkObjectSomeValuesFrom) object_;
			if (equals(result.getFiller(), expression.getFiller())
					&& equals(result.getProperty(), expression.getProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectPropertyChain visit(ElkObjectPropertyChain expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectPropertyChain) {
			ElkObjectPropertyChain result = (ElkObjectPropertyChain) object_;
			if (equals(result.getObjectPropertyExpressions(),
					expression.getObjectPropertyExpressions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectInverseOf visit(ElkObjectInverseOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectInverseOf) {
			ElkObjectInverseOf result = (ElkObjectInverseOf) object_;
			if (equals(result.getObjectProperty(),
					expression.getObjectProperty()))
				return result;
		}
		return null;
	}

	@Override
	public ElkObjectProperty visit(ElkObjectProperty expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkObjectProperty) {
			ElkObjectProperty result = (ElkObjectProperty) object_;
			if (equals(result.getIri(), expression.getIri()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataProperty visit(ElkDataProperty expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataProperty) {
			ElkDataProperty result = (ElkDataProperty) object_;
			if (equals(result.getIri(), expression.getIri()))
				return result;
		}
		return null;
	}

	@Override
	public ElkAnonymousIndividual visit(ElkAnonymousIndividual expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkAnonymousIndividual) {
			ElkAnonymousIndividual result = (ElkAnonymousIndividual) object_;
			if (equals(result.getNodeId(), expression.getNodeId()))
				return result;
		}
		return null;
	}

	@Override
	public ElkNamedIndividual visit(ElkNamedIndividual expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkNamedIndividual) {
			ElkNamedIndividual result = (ElkNamedIndividual) object_;
			if (equals(result.getIri(), expression.getIri()))
				return result;
		}
		return null;
	}

	@Override
	public ElkLiteral visit(ElkLiteral expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkLiteral) {
			ElkLiteral result = (ElkLiteral) object_;
			if (equals(result.getLexicalForm(), expression.getLexicalForm())
					&& equals(result.getDatatype(), expression.getDatatype()))
				return result;
		}
		return null;
	}

	@Override
	public ElkAnnotationProperty visit(ElkAnnotationProperty expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkAnnotationProperty) {
			ElkAnnotationProperty result = (ElkAnnotationProperty) object_;
			if (equals(result.getIri(), expression.getIri()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDatatype visit(ElkDatatype expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDatatype) {
			ElkDatatype result = (ElkDatatype) object_;
			if (equals(result.getIri(), expression.getIri()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataComplementOf visit(ElkDataComplementOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataComplementOf) {
			ElkDataComplementOf result = (ElkDataComplementOf) object_;
			if (equals(result.getDataRange(), expression.getDataRange()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataIntersectionOf visit(ElkDataIntersectionOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataIntersectionOf) {
			ElkDataIntersectionOf result = (ElkDataIntersectionOf) object_;
			if (equals(result.getDataRanges(), expression.getDataRanges()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataOneOf visit(ElkDataOneOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataOneOf) {
			ElkDataOneOf result = (ElkDataOneOf) object_;
			if (equals(result.getLiterals(), expression.getLiterals()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDatatypeRestriction visit(ElkDatatypeRestriction expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDatatypeRestriction) {
			ElkDatatypeRestriction result = (ElkDatatypeRestriction) object_;
			if (equals(result.getDatatype(), expression.getDatatype())
					&& equals(result.getFacetRestrictions(),
							expression.getFacetRestrictions()))
				return result;
		}
		return null;
	}

	@Override
	public ElkDataUnionOf visit(ElkDataUnionOf expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkDataUnionOf) {
			ElkDataUnionOf result = (ElkDataUnionOf) object_;
			if (equals(result.getDataRanges(), expression.getDataRanges()))
				return result;
		}
		return null;
	}

	@Override
	public ElkFacetRestriction visit(ElkFacetRestriction expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkFacetRestriction) {
			ElkFacetRestriction result = (ElkFacetRestriction) object_;
			if (equals(result.getConstrainingFacet(),
					expression.getConstrainingFacet())
					&& equals(result.getRestrictionValue(),
							expression.getRestrictionValue()))
				return result;
		}
		return null;
	}

	@Override
	public ElkAnnotation visit(ElkAnnotation expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkAnnotation) {
			ElkAnnotation result = (ElkAnnotation) object_;
			if (equals(result.getProperty(), expression.getProperty())
					&& equals(result.getValue(), expression.getValue()))
				return result;
		}
		return null;
	}

	@Override
	public ElkIri visit(ElkFullIri expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkIri) {
			ElkIri result = (ElkIri) object_;
			if (equals(result.getFullIriAsString(),
					expression.getFullIriAsString()))
				return result;
		}
		return null;
	}

	@Override
	public ElkIri visit(ElkAbbreviatedIri expression) {
		if (object_ == expression)
			return expression;
		if (object_ instanceof ElkIri) {
			ElkIri result = (ElkIri) object_;
			if (equals(result.getFullIriAsString(),
					expression.getFullIriAsString()))
				return result;
		}
		return null;
	}
}
