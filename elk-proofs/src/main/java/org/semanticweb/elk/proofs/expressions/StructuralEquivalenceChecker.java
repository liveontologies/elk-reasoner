/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.AbstractElkObjectVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkClassExpressionWrap;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexClassExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkComplexObjectSomeValuesFrom;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkLemmaVisitor;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkReflexivePropertyChainLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubClassOfLemma;
import org.semanticweb.elk.proofs.expressions.lemmas.ElkSubPropertyChainOfLemma;


/**
 * Implements structural equivalence checking on ELK axioms and lemmas.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class StructuralEquivalenceChecker implements ExpressionEqualityChecker, ExpressionVisitor<Expression, Boolean> {

	@Override
	public boolean equal(final Expression first, final Expression second) {
		return first.accept(this , second);
	}

	@Override
	public Boolean visit(AxiomExpression expr, Expression second) {
		return second instanceof AxiomExpression ? new AxiomEquivalenceChecker().equal(expr.getAxiom(), ((AxiomExpression) second).getAxiom()) : Boolean.FALSE;
	}

	@Override
	public Boolean visit(LemmaExpression expr, Expression second) {
		return second instanceof ElkLemma ? new LemmaEquivalenceChecker().equal(expr.getLemma(), ((LemmaExpression)second).getLemma()) : Boolean.FALSE;
	}

	private static class LemmaEquivalenceChecker implements ElkLemmaVisitor<ElkLemma, Boolean> {

		boolean equal(ElkLemma first, ElkLemma second) {
			return false;
		}
		
		@Override
		public Boolean visit(ElkReflexivePropertyChainLemma lemma,
				ElkLemma input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean visit(ElkSubClassOfLemma lemma, ElkLemma input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean visit(ElkSubPropertyChainOfLemma lemma, ElkLemma input) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class AxiomEquivalenceChecker extends AbstractElkAxiomVisitor<Boolean> {

		boolean equal(ElkAxiom first, ElkAxiom second) {
			return false;
		}
		
		@Override
		protected Boolean defaultLogicalVisit(ElkAxiom axiom) {
			return Boolean.FALSE;
		}

		@Override
		protected Boolean defaultNonLogicalVisit(ElkAxiom axiom) {
			return Boolean.FALSE;
		}

		@Override
		public Boolean visit(ElkDisjointClassesAxiom elkDisjointClasses) {
			// TODO Auto-generated method stub
			return super.visit(elkDisjointClasses);
		}

		@Override
		public Boolean visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkEquivalentClassesAxiom);
		}

		@Override
		public Boolean visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkSubClassOfAxiom);
		}

		@Override
		public Boolean visit(
				ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
			// TODO Auto-generated method stub
			return super.visit(elkEquivalentObjectProperties);
		}

		@Override
		public Boolean visit(
				ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkObjectPropertyDomainAxiom);
		}

		@Override
		public Boolean visit(
				ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkReflexiveObjectPropertyAxiom);
		}

		@Override
		public Boolean visit(
				ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkSubObjectPropertyOfAxiom);
		}

		@Override
		public Boolean visit(
				ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkTransitiveObjectPropertyAxiom);
		}
		
		
	}
	
	private static class EntityEquivalenceChecker extends AbstractElkObjectVisitor<Boolean> implements ElkComplexClassExpressionVisitor<ElkObject, Boolean> {

		boolean equal(ElkObject first, ElkObject second) {
			return false;
		}
		
		@Override
		public Boolean visit(ElkComplexObjectSomeValuesFrom ce, ElkObject input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Boolean visit(ElkClassExpressionWrap ce, ElkObject input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Boolean defaultVisit(ElkObject obj) {
			return Boolean.FALSE;
		}

		@Override
		public Boolean visit(ElkSameIndividualAxiom obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkClass obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectComplementOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectIntersectionOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectOneOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectSomeValuesFrom obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectUnionOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectPropertyChain obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkObjectProperty obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Boolean visit(ElkNamedIndividual elkNamedIndividual) {
			// TODO Auto-generated method stub
			return super.visit(elkNamedIndividual);
		}

		@Override
		public Boolean visit(ElkIri iri) {
			// TODO Auto-generated method stub
			return super.visit(iri);
		}
		
		
	}
}
