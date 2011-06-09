/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.util.ConcurrentComputationManager;

/**
 * Experimental version of indexing manager.
 * 
 * @author Frantisek Simancik
 *
 */
public class IndexingManager2 extends
		ConcurrentComputationManager<Future<? extends ElkAxiom>> {

	protected SerialOntologyIndex ontologyIndex;
	protected AxiomIndexer axiomIndexer;

	public IndexingManager2(ExecutorService executor, int workerNo) {
		super(executor, workerNo, 0, 512);
		this.ontologyIndex = new SerialOntologyIndex();
		this.axiomIndexer = new AxiomIndexer(ontologyIndex);
	}

	@Override
	protected void process(Future<? extends ElkAxiom> futureAxiom) {
		try {
			futureAxiom.get().accept(axiomIndexer);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public OntologyIndex computeOntologyIndex() {
		waitCompletion();
		return ontologyIndex;
	}
}
