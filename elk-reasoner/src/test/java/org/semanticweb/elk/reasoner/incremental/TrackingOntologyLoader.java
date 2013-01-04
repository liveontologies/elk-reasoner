package org.semanticweb.elk.reasoner.incremental;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * An {@link OntologyLoader} that additionally saves the loaded axioms into two
 * collections. The first one keeps changing axioms that can be added or removed
 * by the incremental changes. The second one keeps the remaining axioms.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class TrackingOntologyLoader implements OntologyLoader {

	private final OntologyLoader loader_;
	/**
	 * stores axioms that can be added and removed by incremental changes
	 */
	private final OnOffVector<ElkAxiom> changingAxioms_;

	/**
	 * stores axioms that should not be added or remove
	 */
	private final List<ElkAxiom> staticAxioms_;

	public TrackingOntologyLoader(OntologyLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms, List<ElkAxiom> untrackedAxioms) {
		this.loader_ = loader;
		this.changingAxioms_ = trackedAxioms;
		this.staticAxioms_ = untrackedAxioms;
	}

	TrackingOntologyLoader(OntologyLoader loader) {
		this(loader, new OnOffVector<ElkAxiom>(127), new ArrayList<ElkAxiom>());
	}

	public OnOffVector<ElkAxiom> getChangingAxioms() {
		return this.changingAxioms_;
	}

	public List<ElkAxiom> getStaticAxioms() {
		return this.staticAxioms_;
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter) {

		final ElkAxiomProcessor processor = new ElkAxiomProcessor() {

			@Override
			public void visit(ElkAxiom elkAxiom) {
				axiomInserter.visit(elkAxiom);
				// currently we only allow class axioms to be changed
				if (elkAxiom instanceof ElkClassAxiom) {
					changingAxioms_.add(elkAxiom);
				} else
					staticAxioms_.add(elkAxiom);
			}
		};

		return loader_.getLoader(processor);
	}

}
