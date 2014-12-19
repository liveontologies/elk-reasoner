package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDeclarationAxiom;

public interface IndexedDeclarationAxiomVisitor<O> {

	O visit(IndexedDeclarationAxiom axiom);

}