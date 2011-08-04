package org.semanticweb.elk.syntax;

/**
 * Corresponds to an <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Individuals">Individual<a> in the OWL 2
 * specification.
 * 
 * @author Markus Kroetzsch
 */
public abstract class ElkIndividual extends ElkObject {

	public abstract <O> O accept(ElkIndividualVisitor<O> visitor);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ELKObject#accept(org.semanticweb.elk
	 * .reasoner.ELKObjectVisitor)
	 */
	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkIndividualVisitor<O>) visitor);
	}

}
