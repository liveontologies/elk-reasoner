/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner;

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.lang.UnsupportedOperationException;

import org.semanticweb.elk.syntax.*;
import org.semanticweb.elk.util.ArraySet;

class Indexer {
	
	protected List<ElkClassAxiom> classAxioms;
	protected List<ElkObjectPropertyAxiom> objectPropertyAxioms;
	
	protected Map<ElkClassExpression, Concept> mapClassToConcept;
	protected Map<ElkObjectPropertyExpression, Role> mapObjectPropertyToRole;
	
	protected Set<ElkObjectSomeValuesFrom> negativeExistentials;	

	Indexer() {
		classAxioms = new Vector<ElkClassAxiom> ();
		objectPropertyAxioms = new Vector<ElkObjectPropertyAxiom> ();
		
		mapClassToConcept = new WeakHashMap<ElkClassExpression, Concept> ();
		mapObjectPropertyToRole = new WeakHashMap<ElkObjectPropertyExpression, Role> ();

		//this should really be a WeakSet
		negativeExistentials = new HashSet<ElkObjectSomeValuesFrom> ();
	}
	
	void addClassAxiom(ElkClassAxiom axiom) {
		axiom.accept(addClassAxiomVisitor);
		classAxioms.add(axiom);
	}
	
	void addObjectPropertyAxiom(ElkObjectPropertyAxiom axiom) {
		axiom.accept(addObjectPropertyAxiomVisitor);
		objectPropertyAxioms.add(axiom);
	}

	void reduceRoleHierarchy() {
		// expand negativeExistentials
		for (ElkObjectSomeValuesFrom existential : negativeExistentials) {
			Concept e = getConcept(existential);
			Role r = getRole(existential.getObjectPropertyExpression());
			Concept c = getConcept(existential.getClassExpression());
			for (Role s : r.getSubRoles())
				c.getUniversals().add(new Universal(s, e));
		}
	}

	protected Concept getConcept(ElkClassExpression ce) {
		Concept concept = mapClassToConcept.get(ce);
		if (concept == null) {
			concept = new Concept(ce);
			mapClassToConcept.put(ce, concept);
		}
		return concept;
	}
	
	protected Role getRole(ElkObjectPropertyExpression ope) {
		Role role = mapObjectPropertyToRole.get(ope);
		if (role == null) {
			role = new Role(ope);
			mapObjectPropertyToRole.put(ope, role);
		}
		return role;
	}
	
	protected final ElkClassAxiomVisitor<Void>
		addClassAxiomVisitor = new ElkClassAxiomVisitor<Void> () {
	
		public Void visit(ElkSubClassOfAxiom axiom) {
			ElkClassExpression subClass = axiom.getSubClassExpression();
			ElkClassExpression superClass = axiom.getSuperClassExpression();
			
			subClass.accept(negativeOccurrenceVisitor);
			superClass.accept(positiveOccurrenceVisitor);

			getConcept(subClass).getToldSuperConcepts().add(getConcept(superClass));
			
			return null;
		}
	
		public Void visit(ElkEquivalentClassesAxiom axiom) {
			Concept canonical = null;
			for (ElkClassExpression c : axiom.getEquivalentClassExpressions()) {
				canonical = mapClassToConcept.get(c);
				if (canonical != null)
					break;
			}
			
			if (canonical == null)
				canonical = new Concept();
			
			for (ElkClassExpression c : axiom.getEquivalentClassExpressions()) {
				Concept concept = mapClassToConcept.get(c);
				if (concept == null) {
					mapClassToConcept.put(c, canonical);
					canonical.getClassExpressions().add(c);
				}
				else {
					canonical.getToldSuperConcepts().add(concept);
					concept.getToldSuperConcepts().add(canonical);
				}
				
				c.accept(negativeOccurrenceVisitor);
				c.accept(positiveOccurrenceVisitor);
			}
			
			return null;
		}
	};
	
	protected final ElkClassExpressionVisitor<Void>
		negativeOccurrenceVisitor = new ElkClassExpressionVisitor<Void> () {
		
			public Void visit(ElkClass c) {
				getConcept(c);
				return null;
			}

			public Void visit(ElkObjectIntersectionOf c) {
				Set<Concept> premises = new ArraySet<Concept> (c.getClassExpressions().length);
				for (ElkClassExpression d : c.getClassExpressions()) {
					d.accept(this);
					premises.add(getConcept(d));
				}
				
				Conjunction conjunction = new Conjunction(premises, getConcept(c));
				for (Concept concept : premises)
					concept.getConjunctions().add(conjunction);
				
				return null;
			}

			public Void visit(ElkObjectSomeValuesFrom c) {
				getConcept(c);
				getRole(c.getObjectPropertyExpression());
				c.getClassExpression().accept(this);

				negativeExistentials.add(c);
				return null;
			}
		};
	
	protected final ElkClassExpressionVisitor<Void>		
		positiveOccurrenceVisitor = new ElkClassExpressionVisitor<Void> () {

			public Void visit(ElkClass c) {
				getConcept(c);
				return null;
			}

			public Void visit(ElkObjectIntersectionOf c) {
				Concept concept = getConcept(c);
				for (ElkClassExpression d : c.getClassExpressions()) {
					d.accept(this);
					concept.getToldSuperConcepts().add(getConcept(d));
				}
				return null;
			}

			public Void visit(ElkObjectSomeValuesFrom c) {
				ElkObjectPropertyExpression r = c.getObjectPropertyExpression();
				ElkClassExpression d = c.getClassExpression();
				
				d.accept(this);
				getConcept(c).getExistentials().add(
						new Existential(getRole(r), getConcept(d)));
				
				return null;
			}
		};
		
	protected final ElkObjectPropertyAxiomVisitor<Void>
		addObjectPropertyAxiomVisitor = new ElkObjectPropertyAxiomVisitor<Void> () {

			public Void visit(ElkFunctionalObjectPropertyAxiom axiom) {
				throw new UnsupportedOperationException(
					"functional object property axioms");
			}

			public Void visit(ElkInverseFunctionalObjectPropertyAxiom axiom) {
				throw new UnsupportedOperationException(
					"inverse functional object property axioms");
			}

			public Void visit(ElkInverseObjectPropertiesAxiom axiom) {
				throw new UnsupportedOperationException(
					"inverse object property axioms");
			}

			public Void visit(ElkSubObjectPropertyOfAxiom axiom) {
				ElkObjectPropertyExpression subObjectProperty = axiom.getSubObjectPropertyExpression();
				ElkObjectPropertyExpression superObjectProperty = axiom.getSuperObjectPropertyExpression();
				
				getRole(superObjectProperty).getToldSubRoles().add(getRole(subObjectProperty));			

				return null;
			}

			public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
				throw new UnsupportedOperationException(
				"inverse functional object property axioms");
			}	
		};
}