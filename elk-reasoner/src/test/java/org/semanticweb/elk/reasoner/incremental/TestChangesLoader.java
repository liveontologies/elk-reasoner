/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TestChangesLoader implements ChangesLoader, OntologyLoader {

	private final Queue<ElkAxiom> additions_ = new LinkedList<ElkAxiom>();
	private final Queue<ElkAxiom> deletions_ = new LinkedList<ElkAxiom>();
	
	public TestChangesLoader add(final ElkAxiom axiom) {
		additions_.add(axiom);
		return this;
	}
	
	public TestChangesLoader remove(final ElkAxiom axiom) {
		deletions_.add(axiom);
		return this;
	}
	
	public void clear() {
		additions_.clear();
		deletions_.clear();
	}	
	
	@Override
	public Loader getLoader(ElkAxiomProcessor axiomInserter, ElkAxiomProcessor axiomDeleter) {
		return new TestLoader(axiomInserter, axiomDeleter);
	}
	
	@Override
	public Loader getLoader(ElkAxiomProcessor axiomLoader) {
		return new TestLoader(axiomLoader, new ElkAxiomProcessor(){
			@Override
			public void visit(ElkAxiom elkAxiom) {
				// does nothing
			}});
	}	

	/**
	 * 
	 */
	class TestLoader implements Loader {

		private final ElkAxiomProcessor inserter_, deleter_;
		
		TestLoader(ElkAxiomProcessor inserter, ElkAxiomProcessor deleter) {
			inserter_ = inserter;
			deleter_ = deleter;
		}

		@Override
		public void load() throws ElkLoadingException {
			for (ElkAxiom axiom : additions_) {
				inserter_.visit(axiom);
			}
			
			for (ElkAxiom axiom : deletions_) {
				deleter_.visit(axiom);
			}
		}

		@Override
		public void dispose() {}		
	}
}