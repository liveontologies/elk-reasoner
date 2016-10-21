/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.stages;

import org.semanticweb.elk.exceptions.ElkException;

/**
 * An abstract interface for defining how reasoner stages are executed by the
 * reasoner. For example, a reasoner may issue log messages before and after
 * execution, measure benchmarking information, or restart the stage in case it
 * has been interrupted.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ReasonerStageExecutor {

	/**
	 * Makes sure that the given stage is completed; it might not necessarily
	 * execute this stage if the results are already computed
	 * 
	 * @param stage
	 *            the reasoner stage to be completed
	 * @throws ElkException
	 *             if the stage cannot be competed successfully
	 */
	public void complete(ReasonerStage stage) throws ElkException;

}
