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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.UnsupportedOperationException;

import org.semanticweb.elk.syntax.*;

class Indexer {
	protected Map<ElkClassExpression, Concept> mapClassToConcept
	 			= new HashMap<ElkClassExpression, Concept> ();
	protected Map<ElkObjectPropertyExpression, Role> mapObjectPropertyToRole
	 			= new HashMap<ElkObjectPropertyExpression, Role> ();
	
	void indexAxiom(ElkAxiom axiom) {
		axiom.accept(addAxiomVisitor);
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

	protected final ElkClassExpressionVisitor<Void> negativeOccurrenceVisitor =  
	new ElkClassExpressionVisitor<Void> () {

		public Void visit(ElkClass c) {
			getConcept(c).negativeOccurrenceNo++;
			
			return null;
		}

		public Void visit(ElkObjectIntersectionOf c) {
			Concept concept = getConcept(c);
			if (concept.negativeOccurrenceNo++ == 0) {

				List<Concept> premises = 
					new ArrayList<Concept> (c.getClassExpressions().size());
				for (ElkClassExpression d : c.getClassExpressions()) {
					d.accept(this);
					premises.add(getConcept(d));
				}

				Conjunction conjunction = new Conjunction(premises, concept);
				for (Concept p : premises)
					p.getConjunctions().add(conjunction);

			}

			return null;
		}

		public Void visit(ElkObjectSomeValuesFrom c) {
			Concept concept = getConcept(c);
			if (concept.negativeOccurrenceNo++ == 0) {
				ElkObjectPropertyExpression r = c.getObjectPropertyExpression();
				ElkClassExpression d = c.getClassExpression();
				d.accept(this);
				getConcept(d).getUniversals().add(
						new Quantifier(getRole(r), concept));
			}

			return null;
		}
	};

	protected final ElkClassExpressionVisitor<Void>		
	positiveOccurrenceVisitor = new ElkClassExpressionVisitor<Void> () {

		public Void visit(ElkClass c) {
			getConcept(c).positiveOccurrenceNo++;
			
			return null;
		}

		public Void visit(ElkObjectIntersectionOf c) {
			Concept concept = getConcept(c);
			if (concept.positiveOccurrenceNo++ == 0) {
				for (ElkClassExpression d : c.getClassExpressions()) {
					d.accept(this);
					concept.getToldSuperConcepts().add(getConcept(d));
				}
			}
			
			return null;
		}

		public Void visit(ElkObjectSomeValuesFrom c) {
			Concept concept = getConcept(c);
			if (concept.positiveOccurrenceNo++ == 0) {
				ElkObjectPropertyExpression r = c.getObjectPropertyExpression();
				ElkClassExpression d = c.getClassExpression();
				d.accept(this);
				concept.getExistentials().add(
						new Existential(getRole(r), getConcept(d)));
			}

			return null;
		}
	};

	protected final ElkAxiomVisitor<Void>
	addAxiomVisitor = new ElkAxiomVisitor<Void> () {

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
				Concept concept = getConcept(c);
				if (canonical == null)
					canonical = concept;
				else {
					concept.getToldSuperConcepts().add(canonical);
					canonical.getToldSuperConcepts().add(concept);
				}
				c.accept(negativeOccurrenceVisitor);
				c.accept(positiveOccurrenceVisitor);
			}

			return null;
		}

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
			Role subRole = 	getRole(axiom.getSubObjectPropertyExpression());
			Role superRole = getRole(axiom.getSuperObjectPropertyExpression());

			subRole.getToldSuperRoles().add(superRole);
			superRole.getToldSubRoles().add(subRole);		
			
			return null;
		}

		public Void visit(ElkTransitiveObjectPropertyAxiom axiom) {
			getRole(axiom.getObjectPropertyExpression()).setTransitive(true);
			
			return null;
		}	
	};
}