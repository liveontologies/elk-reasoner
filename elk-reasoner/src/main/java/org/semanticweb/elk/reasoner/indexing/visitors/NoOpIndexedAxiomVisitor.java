package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubObjectPropertyOfAxiom;

public class NoOpIndexedAxiomVisitor<O> implements IndexedAxiomVisitor<O> {

	@SuppressWarnings("unused")
	protected O defaultVisit(IndexedAxiom axiom) {
		return null;
	}

	@Override
	public O visit(IndexedSubClassOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedSubObjectPropertyOfAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedReflexiveObjectPropertyAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedDisjointnessAxiom axiom) {
		return defaultVisit(axiom);
	}

	@Override
	public O visit(IndexedDeclarationAxiom axiom) {
		return defaultVisit(axiom);
	}

}
