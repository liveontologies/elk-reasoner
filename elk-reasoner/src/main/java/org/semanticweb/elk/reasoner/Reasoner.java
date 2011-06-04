/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.classification.ClassificationManager;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexingManager;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.Saturation;
import org.semanticweb.elk.reasoner.saturation.SaturationManager;
import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClass;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int nWorkers;

	protected final IndexingManager indexingManager;

	protected ClassTaxonomy classTaxonomy = null;

	public Reasoner(ExecutorService executor, int nWorkers) {
		this.executor = executor;
		this.nWorkers = nWorkers;
		indexingManager = new IndexingManager(executor, 1);
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), 16);
	}

	public void load(Future<? extends ElkAxiom> futureAxiom) {
		if (futureAxiom != null)
			indexingManager.submit(futureAxiom);
	}

	public void finishLoading() {
		indexingManager.computeRoleHierarchy();
	}

	public void classify() {
		OntologyIndex ontologyIndex = indexingManager.getOntologyIndex();
		SaturationManager saturationManager = new SaturationManager(executor,
				nWorkers);
		for (IndexedClassExpression indexedClassExpression : ontologyIndex
				.getIndexedClassExpressions())
			if (indexedClassExpression.classExpression instanceof ElkClass)
				saturationManager.submit(indexedClassExpression);
		Saturation saturation = saturationManager.getSaturation();
		ClassificationManager classificationManager = new ClassificationManager(
				executor, nWorkers, ontologyIndex, saturation);
		for (IndexedClassExpression indexedClassExpression : ontologyIndex
				.getIndexedClassExpressions())
			if (indexedClassExpression.classExpression instanceof ElkClass)
				classificationManager
						.submit((ElkClass) indexedClassExpression.classExpression);
		classTaxonomy = classificationManager.getClassTaxonomy();
	}

	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void shutdown() {
		executor.shutdownNow();
	}
}