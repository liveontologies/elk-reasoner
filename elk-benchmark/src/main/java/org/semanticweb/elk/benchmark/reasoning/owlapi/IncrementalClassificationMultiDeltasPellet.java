package org.semanticweb.elk.benchmark.reasoning.owlapi;
/*
 * #%L
 * ELK Benchmarking Package
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

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationMultiDeltasPellet extends
		OWLAPIIncrementalClassificationMultiDeltas {

	public IncrementalClassificationMultiDeltasPellet(String[] args) {
		super(args);
	}

	@Override
	protected OWLReasonerFactory getOWLReasonerFactory() {
		
		try {
			Class<?> pelletFactoryClass = Class.forName("com.clarkparsia.modularity.PelletIncremantalReasonerFactory");
			//Class<?> pelletFactoryClass = Class.forName("com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory");
			OWLReasonerFactory pelletFactory = (OWLReasonerFactory) pelletFactoryClass.getConstructor().newInstance();
			
			return pelletFactory;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
