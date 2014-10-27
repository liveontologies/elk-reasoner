/**
 * 
 */
package org.semanticweb.elk.ore;
/*
 * #%L
 * ELK ORE build
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.Reasoner;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class NativeRunnerWithEagerLoading extends NativeRunner {

	public static void main(String[] args) throws Exception {
		Task task = validateArgs(args);
		// help
		if (task == null) {
			printHelp();
			return;
		}

		NativeRunner runner = new NativeRunnerWithEagerLoading();
		
		runner.run(args, task);
	}	
	
	@Override
	protected void loadOntology(Reasoner reasoner) throws ElkException {
		//long ts = System.currentTimeMillis();
		
		reasoner.forceLoading();
		
		//System.out.println((System.currentTimeMillis() - ts) + " loading done");
	}

	
}
