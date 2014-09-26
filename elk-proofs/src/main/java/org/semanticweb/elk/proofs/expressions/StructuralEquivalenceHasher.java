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
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class StructuralEquivalenceHasher implements ExpressionHasher {

	@Override
	public int hashCode(Expression expression) {
		return expression.accept(new ExpressionVisitor<Void, Integer>() {

			@Override
			public Integer visit(AxiomExpression expr, Void input) {
				return new AxiomHasher().hashCode(expr.getAxiom());
			}

			@Override
			public Integer visit(LemmaExpression expr, Void input) {
				return new LemmaHasher().hashCode(expr.getLemma());
			}
			
		}, null);
	}

	private static class LemmaHasher implements ElkLemmaVisitor<ElkLemma, Integer> {

		int hashCode(ElkLemma lemma) {
			return lemma.hashCode();
		}
		
		@Override
		public Integer visit(ElkReflexivePropertyChainLemma lemma,
				ElkLemma input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer visit(ElkSubClassOfLemma lemma, ElkLemma input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer visit(ElkSubPropertyChainOfLemma lemma, ElkLemma input) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private static class AxiomHasher extends AbstractElkAxiomVisitor<Integer> {

		int hashCode(ElkAxiom ax) {
			return ax.accept(this);
		}
		
		@Override
		protected Integer defaultLogicalVisit(ElkAxiom axiom) {
			return axiom.hashCode();
		}

		@Override
		protected Integer defaultNonLogicalVisit(ElkAxiom axiom) {
			return axiom.hashCode();
		}

		@Override
		public Integer visit(ElkDisjointClassesAxiom elkDisjointClasses) {
			// TODO Auto-generated method stub
			return super.visit(elkDisjointClasses);
		}

		@Override
		public Integer visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkEquivalentClassesAxiom);
		}

		@Override
		public Integer visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkSubClassOfAxiom);
		}

		@Override
		public Integer visit(
				ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
			// TODO Auto-generated method stub
			return super.visit(elkEquivalentObjectProperties);
		}

		@Override
		public Integer visit(
				ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkObjectPropertyDomainAxiom);
		}

		@Override
		public Integer visit(
				ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkReflexiveObjectPropertyAxiom);
		}

		@Override
		public Integer visit(
				ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkSubObjectPropertyOfAxiom);
		}

		@Override
		public Integer visit(
				ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
			// TODO Auto-generated method stub
			return super.visit(elkTransitiveObjectPropertyAxiom);
		}
		
		
	}
	
	private static class EntityHasher extends AbstractElkObjectVisitor<Integer> implements ElkComplexClassExpressionVisitor<ElkObject, Integer> {

		
		@Override
		public Integer visit(ElkComplexObjectSomeValuesFrom ce, ElkObject input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer visit(ElkClassExpressionWrap ce, ElkObject input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Integer defaultVisit(ElkObject obj) {
			return obj.hashCode();
		}

		@Override
		public Integer visit(ElkSameIndividualAxiom obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkClass obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectComplementOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectIntersectionOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectOneOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectSomeValuesFrom obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectUnionOf obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectPropertyChain obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkObjectProperty obj) {
			// TODO Auto-generated method stub
			return super.visit(obj);
		}

		@Override
		public Integer visit(ElkNamedIndividual elkNamedIndividual) {
			// TODO Auto-generated method stub
			return super.visit(elkNamedIndividual);
		}

		@Override
		public Integer visit(ElkIri iri) {
			// TODO Auto-generated method stub
			return super.visit(iri);
		}
		
		
	}	
}
