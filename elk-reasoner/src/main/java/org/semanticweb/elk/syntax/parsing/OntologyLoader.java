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
package org.semanticweb.elk.syntax.parsing;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.util.AbstractConcurrentComputation;

/**
 * Experimental version of indexing manager.
 * 
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 */
public class OntologyLoader extends
		AbstractConcurrentComputation<Future<? extends ElkAxiom>> {

	protected ElkAxiomProcessor elkAxiomProcessor;

	public OntologyLoader(ExecutorService executor, int workerNo,
			ElkAxiomProcessor elkAxiomProcessor) {
		super(executor, workerNo, 512, 0);
		this.elkAxiomProcessor = elkAxiomProcessor;
	}

	@Override
	protected void process(Future<? extends ElkAxiom> futureAxiom) {
		try {
			elkAxiomProcessor.process(futureAxiom.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void loadFutureAxiom(Future<? extends ElkAxiom> futureAxiom) {
		if (futureAxiom != null)
			submit(futureAxiom);
	}

}
