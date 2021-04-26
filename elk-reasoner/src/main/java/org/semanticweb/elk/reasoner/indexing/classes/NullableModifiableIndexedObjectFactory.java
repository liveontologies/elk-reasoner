package org.semanticweb.elk.reasoner.indexing.classes;

import java.util.List;

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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubObjectPropertyOfAxiom;

/**
 * A {@link ModifiableIndexedObject.Factory}, the methods of which can accept
 * {@code null} values for {@link IndexedObject} arguments, in which case it
 * returns {@code null} as the result
 * 
 * @author Yevgeny Kazakov
 *
 */
class NullableModifiableIndexedObjectFactory
		implements ModifiableIndexedObject.Factory {

	private final ModifiableIndexedObject.Factory delegate_;

	NullableModifiableIndexedObjectFactory(
			ModifiableIndexedObject.Factory delegate) {
		this.delegate_ = delegate;
	}

	@Override
	public ModifiableIndexedClass getIndexedClass(ElkClass elkClass) {
		return delegate_.getIndexedClass(elkClass);
	}

	@Override
	public ModifiableIndexedClassExpressionList getIndexedClassExpressionList(
			List<? extends ModifiableIndexedClassExpression> members) {
		for (ModifiableIndexedClassExpression member : members) {
			if (member == null) {
				return null;
			}
		}
		// else
		return delegate_.getIndexedClassExpressionList(members);
	}

	@Override
	public ModifiableIndexedComplexPropertyChain getIndexedComplexPropertyChain(
			ModifiableIndexedObjectProperty leftProperty,
			ModifiableIndexedPropertyChain rightProperty) {
		if (leftProperty == null || rightProperty == null) {
			return null;
		}
		return delegate_.getIndexedComplexPropertyChain(leftProperty,
				rightProperty);
	}

	@Override
	public ModifiableIndexedDataHasValue getIndexedDataHasValue(
			ElkDataHasValue elkDataHasValue) {
		return delegate_.getIndexedDataHasValue(elkDataHasValue);
	}

	@Override
	public ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedEntity entity) {
		if (originalAxiom == null || entity == null) {
			return null;
		}
		// else
		return delegate_.getIndexedDeclarationAxiom(originalAxiom, entity);
	}

	@Override
	public ModifiableIndexedDisjointClassesAxiom getIndexedDisjointClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		if (originalAxiom == null || members == null) {
			return null;
		}
		// else
		return delegate_.getIndexedDisjointClassesAxiom(originalAxiom, members);
	}

	@Override
	public ModifiableIndexedEquivalentClassesAxiom getIndexedEquivalentClassesAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		if (firstMember == null || secondMember == null) {
			return null;
		}
		// else
		return delegate_.getIndexedEquivalentClassesAxiom(originalAxiom,
				firstMember, secondMember);
	}

	@Override
	public ModifiableIndexedIndividual getIndexedIndividual(
			ElkNamedIndividual elkNamedIndividual) {
		return delegate_.getIndexedIndividual(elkNamedIndividual);
	}

	@Override
	public ModifiableIndexedObjectComplementOf getIndexedObjectComplementOf(
			ModifiableIndexedClassExpression negated) {
		if (negated == null) {
			return null;
		}
		// else
		return delegate_.getIndexedObjectComplementOf(negated);
	}

	@Override
	public ModifiableIndexedObjectHasSelf getIndexedObjectHasSelf(
			ModifiableIndexedObjectProperty property) {
		if (property == null) {
			return null;
		}
		// else
		return delegate_.getIndexedObjectHasSelf(property);
	}

	@Override
	public ModifiableIndexedObjectIntersectionOf getIndexedObjectIntersectionOf(
			ModifiableIndexedClassExpression conjunctA,
			ModifiableIndexedClassExpression conjunctB) {
		if (conjunctA == null || conjunctB == null) {
			return null;
		}
		// else
		return delegate_.getIndexedObjectIntersectionOf(conjunctA, conjunctB);
	}

	@Override
	public ModifiableIndexedObjectProperty getIndexedObjectProperty(
			ElkObjectProperty elkObjectProperty) {
		return delegate_.getIndexedObjectProperty(elkObjectProperty);
	}

	@Override
	public ModifiableIndexedObjectPropertyRangeAxiom getIndexedObjectPropertyRangeAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		if (originalAxiom == null || property == null || range == null) {
			return null;
		}
		// else
		return delegate_.getIndexedObjectPropertyRangeAxiom(originalAxiom,
				property, range);
	}

	@Override
	public ModifiableIndexedObjectSomeValuesFrom getIndexedObjectSomeValuesFrom(
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression filler) {
		if (property == null || filler == null) {
			return null;
		}
		// else
		return delegate_.getIndexedObjectSomeValuesFrom(property, filler);
	}

	@Override
	public ModifiableIndexedObjectUnionOf getIndexedObjectUnionOf(
			List<? extends ModifiableIndexedClassExpression> disjuncts) {
		for (ModifiableIndexedClassExpression disjunct : disjuncts) {
			if (disjunct == null) {
				return null;
			}
		}
		// else
		return delegate_.getIndexedObjectUnionOf(disjuncts);
	}

	@Override
	public ModifiableIndexedSubClassOfAxiom getIndexedSubClassOfAxiom(
			ElkAxiom originalAxiom, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		if (originalAxiom == null || subClass == null) {
			return null;
		}
		// else
		return delegate_.getIndexedSubClassOfAxiom(originalAxiom, subClass,
				superClass);
	}

	@Override
	public ModifiableIndexedSubObjectPropertyOfAxiom getIndexedSubObjectPropertyOfAxiom(
			ElkAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		if (originalAxiom == null || subPropertyChain == null
				|| superProperty == null) {
			return null;
		}
		// else
		return delegate_.getIndexedSubObjectPropertyOfAxiom(originalAxiom,
				subPropertyChain, superProperty);
	}

}
