/*
 * #%L
 * ELK Command Line Interface
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
/**
 * 
 */
package org.semanticweb.elk.cli;

import java.util.concurrent.Executors;

import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

/**
 * Instantiates {@link IOReasoner}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IOReasonerFactory extends ReasonerFactory {

	@Override
	public IOReasoner createReasoner() {
		return createReasoner(super.loadReasonerConfiguration());
	}

	@Override
	public IOReasoner createReasoner(ReasonerConfiguration config) {
		return new IOReasoner(Executors.newCachedThreadPool(), config.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS));
	}
	
	

}
