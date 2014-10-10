/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.owl.AbstractElkAxiomVisitor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.proofs.expressions.ExpressionVisitor;
import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;
import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapitools.proofs.OWLInference;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ElkToOwlProofConverter {

	public static OWLExpression convert(DerivedExpression expression) {
		return expression.accept(new ExpressionVisitor<Void, OWLExpression>() {

			@Override
			public OWLExpression visit(DerivedAxiomExpression<? extends ElkAxiom> expr,
					Void input) {
				return new AxiomExpressionWrap(expr);
			}

			@Override
			public OWLExpression visit(DerivedLemmaExpression expr, Void input) {
				return new LemmaExpressionWrap(expr);
			}
		}, null);
	}
	
	public static OWLInference convert(Inference inference) {
		return new InferenceWrap(inference);		
	}

	public static OWLAxiom convert(ElkAxiom axiom) {
		final OWLDataFactory factory = OWLManager.getOWLDataFactory();
		
		return axiom.accept(new AbstractElkAxiomVisitor<OWLAxiom>() {

			@Override
			public OWLAxiom visit(ElkDisjointClassesAxiom elkDisjointClasses) {
				// TODO Auto-generated method stub
				return super.visit(elkDisjointClasses);
			}

			@Override
			public OWLAxiom visit(
					ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
				// TODO Auto-generated method stub
				return super.visit(elkEquivalentClassesAxiom);
			}

			@Override
			public OWLAxiom visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
				// TODO Auto-generated method stub
				return super.visit(elkSubClassOfAxiom);
			}

			@Override
			public OWLAxiom visit(
					ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
				// TODO Auto-generated method stub
				return super.visit(elkObjectPropertyDomainAxiom);
			}

			@Override
			public OWLAxiom visit(
					ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
				// TODO Auto-generated method stub
				return super.visit(elkReflexiveObjectPropertyAxiom);
			}

			@Override
			public OWLAxiom visit(
					ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
				// TODO Auto-generated method stub
				return super.visit(elkSubObjectPropertyOfAxiom);
			}
			
		});
	}
	
	
	
}
