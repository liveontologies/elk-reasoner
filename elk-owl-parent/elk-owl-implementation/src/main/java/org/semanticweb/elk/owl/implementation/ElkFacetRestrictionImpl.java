/**
 * 
 */
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implementation of {@link ElkFacetRestriction}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class ElkFacetRestrictionImpl implements ElkFacetRestriction {

	private final String facetURI;
	private final ElkLiteral literal;
	
	ElkFacetRestrictionImpl(String facetURI, ElkLiteral literal) {
		this.facetURI = facetURI;
		this.literal = literal;
	}
	
	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String getConstrainingFacet() {
		return facetURI;
	}

	@Override
	public ElkLiteral getRestrictionValue() {
		return literal;
	}
}
