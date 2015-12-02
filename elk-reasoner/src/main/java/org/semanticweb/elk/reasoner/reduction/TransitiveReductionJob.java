/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.reduction;

import org.semanticweb.elk.reasoner.ReasonerJob;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;

/**
 * The type of the transitive reduction job. The input of the transitive
 * reduction job is an indexed class expression for which it is required to
 * check satisfiability, and if satisfiable, compute equivalent classes and
 * direct super-classes. This result is set as the output of the job.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <R>
 *            the type of the input for the transitive reduction job.
 */
public class TransitiveReductionJob<R extends IndexedClassExpression> extends
		ReasonerJob<R, TransitiveReductionOutput<R>> {

	public TransitiveReductionJob(R input) {
		super(input);
	}

	@Override
	protected void setOutput(TransitiveReductionOutput<R> output) {
		super.setOutput(output);
	}

}
