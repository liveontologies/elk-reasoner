/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
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

import org.semanticweb.elk.benchmark.AllFilesMultiTask;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskFactory;

/**
 * Invokes {@link IncrementalClassificationTask} for every ontology in a folder
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AllFilesIncrementalClassificationTask extends AllFilesMultiTask {

	public AllFilesIncrementalClassificationTask(String[] args) {
		super(args);
	}

	@Override
	public Task instantiateSubTask(String[] args) {
		return TaskFactory.createTask(
				IncrementalClassificationTask.class.getName(), args);
	}

}
