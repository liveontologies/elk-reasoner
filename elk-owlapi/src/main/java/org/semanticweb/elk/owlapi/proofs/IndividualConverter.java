/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class IndividualConverter implements ElkIndividualVisitor<OWLIndividual> {

	private final OWLDataFactory factory_;
	
	IndividualConverter(OWLDataFactory f) {
		factory_ = f;
	}
	
	@Override
	public OWLIndividual visit(ElkAnonymousIndividual ind) {
		return factory_.getOWLAnonymousIndividual(ind.getNodeId());
	}

	@Override
	public OWLIndividual visit(ElkNamedIndividual ind) {
		return factory_.getOWLNamedIndividual(IRI.create(ind.getIri().getFullIriAsString()));
	}

}
