
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAsymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyRangeAxiom;
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
import org.semanticweb.elk.owl.interfaces.ElkInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkInverseObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIrreflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyRangeAxiom;
import org.semanticweb.elk.owl.interfaces.ElkReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSameIndividualAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubDataPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

public class GalenAxiomRewriter implements ElkAxiomProcessor, ElkAxiomVisitor<ElkAxiom> {

	protected ElkAxiomProcessor baseProcessor;
	
	protected ElkObjectFactory factory = new ElkObjectFactoryImpl();
	
	public GalenAxiomRewriter( ElkAxiomProcessor baseProcessor ) {
		this.baseProcessor = baseProcessor;
	}
	
	protected ElkClassExpression rewriteClassExpression(ElkClassExpression elkClassExpression) {
		return elkClassExpression;
	}

	protected List<ElkClassExpression> rewriteClassExpressionList(List<? extends ElkClassExpression> elkClassExpressions) {
		List<ElkClassExpression> result = new ArrayList<ElkClassExpression>();
		for ( ElkClassExpression e : elkClassExpressions) {
			result.add(rewriteClassExpression(e));
		}
		return result;
	}
	
	public void process(ElkAxiom elkAxiom) {
		ElkAxiom newAxiom = elkAxiom.accept(this);
		baseProcessor.process(newAxiom);
	}

	public ElkAxiom visit(ElkDisjointClassesAxiom elkDisjointClasses) {
		//factory.getDisjointClassesAxiom
		
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkDisjointUnionAxiom elkDisjointUnionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkEquivalentClassesAxiom elkEquivalentClassesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkSubClassOfAxiom elkSubClassOfAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkAsymmetricObjectPropertyAxiom elkAsymmetricObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkDisjointObjectPropertiesAxiom elkDisjointObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkEquivalentObjectPropertiesAxiom elkEquivalentObjectProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkFunctionalObjectPropertyAxiom elkFunctionalObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkInverseFunctionalObjectPropertyAxiom elkInverseFunctionalObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkInverseObjectPropertiesAxiom elkInverseObjectPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkIrreflexiveObjectPropertyAxiom elkIrreflexiveObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkObjectPropertyDomainAxiom elkObjectPropertyDomainAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkObjectPropertyRangeAxiom elkObjectPropertyRangeAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkReflexiveObjectPropertyAxiom elkReflexiveObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkSubObjectPropertyOfAxiom elkSubObjectPropertyOfAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkSymmetricObjectPropertyAxiom elkSymmetricObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkTransitiveObjectPropertyAxiom elkTransitiveObjectPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkDataPropertyDomainAxiom elkDataPropertyDomainAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkDataPropertyRangeAxiom elkDataPropertyRangeAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkDisjointDataPropertiesAxiom elkDisjointDataPropertiesAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkEquivalentDataPropertiesAxiom elkEquivalentDataProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkFunctionalDataPropertyAxiom elkFunctionalDataPropertyAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkSubDataPropertyOfAxiom elkSubDataPropertyOfAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkClassAssertionAxiom elkClassAssertionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkDataPropertyAssertionAxiom elkDataPropertyAssertionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkDifferentIndividualsAxiom elkDifferentIndividualsAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkNegativeDataPropertyAssertionAxiom elkNegativeDataPropertyAssertion) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkNegativeObjectPropertyAssertionAxiom elkNegativeObjectPropertyAssertion) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(
			ElkObjectPropertyAssertionAxiom elkObjectPropertyAssertionAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkSameIndividualAxiom elkSameIndividualAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkDeclarationAxiom elkDeclarationAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

	public ElkAxiom visit(ElkAnnotationAxiom elkAnnotationAxiom) {
		// TODO Auto-generated method stub
		return null;
	}

}
