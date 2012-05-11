package org.semanticweb.elk.reasoner.taxonomy;

import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;

public class TaxonomyComputation extends ConcurrentComputation<IndexedClass> {

	final ClassTaxonomyEngine classTaxonomyEngine;

	public TaxonomyComputation(ExecutorService executor, int maxWorkers,
			ClassTaxonomyEngine classTaxonomyEngine) {
		super(classTaxonomyEngine, executor, maxWorkers, 8 * maxWorkers, 16);
		this.classTaxonomyEngine = classTaxonomyEngine;
	}

	public TaxonomyComputation(ExecutorService executor, int maxWorkers,
			OntologyIndex ontologyIndex) {
		this(executor, maxWorkers, new ClassTaxonomyEngine(ontologyIndex));
	}

	public Taxonomy<ElkClass> getClassTaxonomy() {
		return classTaxonomyEngine.getClassTaxonomy();
	}

	public void printStatistics() {
		classTaxonomyEngine.printStatistics();
	}
}