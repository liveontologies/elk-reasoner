package org.semanticweb.elk.reasoner.indexing.classes;

/*-
 * #%L
 * ELK Reasoner Core
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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
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
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;

/**
 * A {@link ModifiableIndexedObject.Factory}, the methods of which can accept
 * {@code null} values for {@link IndexedObject} arguments, in which case it
 * returns {@code null} as the result
 * 
 * @author Yevgeny Kazakov
 *
 */
class NullableModifiableIndexedObjectFactory
		extends NullableCachedIndexedObjectFactory
		implements ModifiableIndexedObject.Factory {

	private final ModifiableIndexedObject.Factory delegate_;

	<F extends CachedIndexedObject.Factory & ModifiableIndexedObject.Factory> NullableModifiableIndexedObjectFactory(
			F delegate) {
		super(delegate);
		this.delegate_ = delegate;
	}

	NullableModifiableIndexedObjectFactory() {
		this(new BaseModifiableIndexedObjectFactory());
	}

	@Override
	public ModifiableElkSubClassOfAxiomConversion getElkSubClassOfAxiomConversion(
			ElkSubClassOfAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		if (subClass == null || superClass == null) {
			return null;
		}
		// else
		return delegate_.getElkSubClassOfAxiomConversion(originalAxiom,
				subClass, superClass);
	}

	@Override
	public ModifiableElkDeclarationAxiomConversion getElkDeclarationAxiomConversion(
			ElkDeclarationAxiom originalAxiom, ModifiableIndexedEntity entity) {
		if (entity == null) {
			return null;
		}
		// else
		return delegate_.getElkDeclarationAxiomConversion(originalAxiom,
				entity);
	}

	@Override
	public ModifiableElkSameIndividualAxiomConversion getElkSameIndividualAxiomConversion(
			ElkSameIndividualAxiom originalAxiom, int subIndividualPosition,
			int superIndividualPosition,
			ModifiableIndexedIndividual subIndividual,
			ModifiableIndexedIndividual superIndividual) {
		if (subIndividual == null || superIndividual == null) {
			return null;
		}
		// else
		return delegate_.getElkSameIndividualAxiomConversion(originalAxiom,
				subIndividualPosition, superIndividualPosition, subIndividual,
				superIndividual);
	}

	@Override
	public ModifiableElkClassAssertionAxiomConversion getElkClassAssertionAxiomConversion(
			ElkClassAssertionAxiom originalAxiom,
			ModifiableIndexedIndividual instance,
			ModifiableIndexedClassExpression type) {
		if (instance == null || type == null) {
			return null;
		}
		// else
		return delegate_.getElkClassAssertionAxiomConversion(originalAxiom,
				instance, type);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomNaryConversion getElkDisjointUnionAxiomNaryConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		if (disjointClasses == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointUnionAxiomNaryConversion(originalAxiom,
				disjointClasses);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomBinaryConversion getElkDisjointUnionAxiomBinaryConversion(
			ElkDisjointUnionAxiom originalAxiom, int firstDisjunctPosition,
			int secondDisjunctPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		if (conjunction == null || bottom == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointUnionAxiomBinaryConversion(originalAxiom,
				firstDisjunctPosition, secondDisjunctPosition, conjunction,
				bottom);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomSubClassConversion getElkDisjointUnionAxiomSubClassConversion(
			ElkDisjointUnionAxiom originalAxiom, int disjunctPosition,
			ModifiableIndexedClassExpression disjunct,
			ModifiableIndexedClass definedClass) {
		if (disjunct == null || definedClass == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointUnionAxiomSubClassConversion(
				originalAxiom, disjunctPosition, disjunct, definedClass);
	}

	@Override
	public ModifiableElkDisjointClassesAxiomNaryConversion getElkDisjointClassesAxiomNaryConversion(
			ElkDisjointClassesAxiom originalAxiom,
			ModifiableIndexedClassExpressionList disjointClasses) {
		if (disjointClasses == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointClassesAxiomNaryConversion(originalAxiom,
				disjointClasses);
	}

	@Override
	public ModifiableElkDisjointClassesAxiomBinaryConversion getElkDisjointClassesAxiomBinaryConversion(
			ElkDisjointClassesAxiom originalAxiom, int firstClassPosition,
			int secondClassPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		if (conjunction == null || bottom == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointClassesAxiomBinaryConversion(
				originalAxiom, firstClassPosition, secondClassPosition,
				conjunction, bottom);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomOwlNothingConversion getElkDisjointUnionAxiomOwlNothingConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClass bottom) {
		if (definedClass == null || bottom == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointUnionAxiomOwlNothingConversion(
				originalAxiom, definedClass, bottom);
	}

	@Override
	public ModifiableElkObjectPropertyDomainAxiomConversion getElkObjectPropertyDomainAxiomConversion(
			ElkObjectPropertyDomainAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		if (subClass == null || superClass == null) {
			return null;
		}
		// else
		return delegate_.getElkObjectPropertyDomainAxiomConversion(
				originalAxiom, subClass, superClass);
	}

	@Override
	public ModifiableElkObjectPropertyRangeAxiomConversion getElkObjectPropertyRangeAxiomConversion(
			ElkObjectPropertyRangeAxiom originalAxiom,
			ModifiableIndexedObjectProperty property,
			ModifiableIndexedClassExpression range) {
		if (property == null || range == null) {
			return null;
		}
		// else
		return delegate_.getElkObjectPropertyRangeAxiomConversion(originalAxiom,
				property, range);
	}

	@Override
	public ModifiableElkSubObjectPropertyOfAxiomConversion getElkSubObjectPropertyOfAxiomConversion(
			ElkSubObjectPropertyOfAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		if (subPropertyChain == null || superProperty == null) {
			return null;
		}
		// else
		return delegate_.getElkSubObjectPropertyOfAxiomConversion(originalAxiom,
				subPropertyChain, superProperty);
	}

	@Override
	public ModifiableElkDisjointUnionAxiomEquivalenceConversion getElkDisjointUnionAxiomEquivalenceConversion(
			ElkDisjointUnionAxiom originalAxiom,
			ModifiableIndexedClass definedClass,
			ModifiableIndexedClassExpression definition) {
		if (definedClass == null || definition == null) {
			return null;
		}
		// else
		return delegate_.getElkDisjointUnionAxiomEquivalenceConversion(
				originalAxiom, definedClass, definition);
	}

	@Override
	public ModifiableElkObjectPropertyAssertionAxiomConversion getElkObjectPropertyAssertionAxiomConversion(
			ElkObjectPropertyAssertionAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		if (subClass == null || superClass == null) {
			return null;
		}
		// else
		return delegate_.getElkObjectPropertyAssertionAxiomConversion(
				originalAxiom, subClass, superClass);
	}

	@Override
	public ModifiableElkReflexiveObjectPropertyAxiomConversion getElkReflexiveObjectPropertyAxiomConversion(
			ElkReflexiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		if (subClass == null || superClass == null) {
			return null;
		}
		// else
		return delegate_.getElkReflexiveObjectPropertyAxiomConversion(
				originalAxiom, subClass, superClass);
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomSubClassConversion getElkEquivalentClassesAxiomSubClassConversion(
			ElkEquivalentClassesAxiom originalAxiom, int subClassPosition,
			int superClassPosition, ModifiableIndexedClassExpression subClass,
			ModifiableIndexedClassExpression superClass) {
		if (subClass == null || superClass == null) {
			return null;
		}
		// else
		return delegate_.getElkEquivalentClassesAxiomSubClassConversion(
				originalAxiom, subClassPosition, superClassPosition, subClass,
				superClass);
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomNaryConversion getElkDifferentIndividualsAxiomNaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			ModifiableIndexedClassExpressionList differentIndividuals) {
		if (differentIndividuals == null) {
			return null;
		}
		// else
		return delegate_.getElkDifferentIndividualsAxiomNaryConversion(
				originalAxiom, differentIndividuals);
	}

	@Override
	public ModifiableElkDifferentIndividualsAxiomBinaryConversion getElkDifferentIndividualsAxiomBinaryConversion(
			ElkDifferentIndividualsAxiom originalAxiom,
			int firstIndividualPosition, int secondIndividualPosition,
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableIndexedClass bottom) {
		if (conjunction == null || bottom == null) {
			return null;
		}
		// else
		return delegate_.getElkDifferentIndividualsAxiomBinaryConversion(
				originalAxiom, firstIndividualPosition,
				secondIndividualPosition, conjunction, bottom);
	}

	@Override
	public ModifiableElkEquivalentClassesAxiomEquivalenceConversion getElkEquivalentClassesAxiomEquivalenceConversion(
			ElkEquivalentClassesAxiom originalAxiom, int firstMemberPosition,
			int secondMemberPosition,
			ModifiableIndexedClassExpression firstMember,
			ModifiableIndexedClassExpression secondMember) {
		if (firstMember == null || secondMember == null) {
			return null;
		}
		// else
		return delegate_.getElkEquivalentClassesAxiomEquivalenceConversion(
				originalAxiom, firstMemberPosition, secondMemberPosition,
				firstMember, secondMember);
	}

	@Override
	public ModifiableElkTransitiveObjectPropertyAxiomConversion getElkTransitiveObjectPropertyAxiomConversion(
			ElkTransitiveObjectPropertyAxiom originalAxiom,
			ModifiableIndexedPropertyChain subPropertyChain,
			ModifiableIndexedObjectProperty superProperty) {
		if (subPropertyChain == null || superProperty == null) {
			return null;
		}
		// else
		return delegate_.getElkTransitiveObjectPropertyAxiomConversion(
				originalAxiom, subPropertyChain, superProperty);
	}

	@Override
	public ModifiableElkEquivalentObjectPropertiesAxiomConversion getElkEquivalentObjectPropertiesAxiomConversion(
			ElkEquivalentObjectPropertiesAxiom originalAxiom,
			int subPropertyPosition, int superPropertyPosition,
			ModifiableIndexedObjectProperty subProperty,
			ModifiableIndexedObjectProperty superProperty) {
		if (subProperty == null || superProperty == null) {
			return null;
		}
		// else
		return delegate_.getElkEquivalentObjectPropertiesAxiomConversion(
				originalAxiom, subPropertyPosition, superPropertyPosition,
				subProperty, superProperty);
	}

}
