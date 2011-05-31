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
import org.semanticweb.elk.reasoner.classification.ConcurrentClassTaxonomy;
import org.semanticweb.elk.reasoner.indexing.Index;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexingManager;
import org.semanticweb.elk.reasoner.indexing.SerialIndex;
import org.semanticweb.elk.reasoner.saturation.ConcurrentSaturation;
import org.semanticweb.elk.reasoner.saturation.Saturation;
import org.semanticweb.elk.reasoner.saturation.SaturationManager;
import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClass;

public class Reasoner {
	// executor used to run the jobs
	private final ExecutorService executor;

	final protected Index index;
	final protected IndexingManager indexingManager;

	final protected Saturation saturation;
	final protected SaturationManager saturationManager;

	final protected ClassTaxonomy classTaxonomy;
	final protected ClassificationManager classificationManager;

	public Reasoner(int nWorkers) {
		executor = Executors.newCachedThreadPool();
		index = new SerialIndex();
		// use 1 index worker since index is not thread-safe
		indexingManager = new IndexingManager(index, executor, 1);
		saturation = new ConcurrentSaturation(512);
		saturationManager = new SaturationManager(saturation, executor,
				nWorkers);
		classTaxonomy = new ConcurrentClassTaxonomy(index, saturation);
		classificationManager = new ClassificationManager(classTaxonomy,
				executor, nWorkers);
	}

	public Reasoner() {
		this(16);
	}

	public void load(Future<? extends ElkAxiom> futureAxiom) {
		if (futureAxiom != null)
			indexingManager.submit(futureAxiom);
	}

	public void finishLoading() {
		indexingManager.waitCompletion();
		index.reduceRoleHierarchy();
	}

	public void saturate() {
		for (IndexedClassExpression indexedClassExpression : index
				.getIndexedClassExpressions())
			if (indexedClassExpression.classExpression instanceof ElkClass)
				saturationManager.submit(indexedClassExpression);
		saturationManager.waitCompletion();
	}

	public void classify() {
		for (IndexedClassExpression indexedClassExpression : index
				.getIndexedClassExpressions())
			if (indexedClassExpression.classExpression instanceof ElkClass)
				classificationManager
						.submit((ElkClass) indexedClassExpression.classExpression);
		classificationManager.waitCompletion();
	}

	public Index getIndex() {
		return index;
	}

	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void shutdown() {
		executor.shutdownNow();
	}
}