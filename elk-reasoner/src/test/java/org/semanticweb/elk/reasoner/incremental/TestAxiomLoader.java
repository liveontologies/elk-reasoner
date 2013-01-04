package org.semanticweb.elk.reasoner.incremental;

import java.util.Iterator;

import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

public class TestAxiomLoader implements OntologyLoader {

	private final Iterable<ElkAxiom> axioms_;

	public TestAxiomLoader(Iterable<ElkAxiom> axioms) {
		this.axioms_ = axioms;
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomLoader) {

		return new Loader() {

			private final Iterator<ElkAxiom> axiomIterator = axioms_.iterator();

			@Override
			public void load() throws ElkLoadingException {
				while (axiomIterator.hasNext()) {
					if (Thread.currentThread().isInterrupted())
						break;
					axiomLoader.visit(axiomIterator.next());
				}
			}

			@Override
			public void dispose() {
				// nothing to do
			}

		};
	}

}
