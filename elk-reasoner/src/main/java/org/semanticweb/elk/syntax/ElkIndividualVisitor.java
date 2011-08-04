package org.semanticweb.elk.syntax;

/**
 * Visitor interface for ELKIndividual. 
 * 
 * @author Markus Kroetzsch
 */
public interface ElkIndividualVisitor<O> {

	O visit(ElkNamedIndividual elkNamedIndividual);

	O visit(ElkAnonymousIndividual elkAnonymousIndividual);
}
