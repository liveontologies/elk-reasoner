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

import java.io.IOException;
import java.io.InputStream;

import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.ClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.testing.TestManifest;

/**
 * Loads test ontologies using Elk's native OWL 2 functional syntax parser
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 *
 */
public class CLIClassificationCorrectnessTest extends ClassificationCorrectnessTest {

	public CLIClassificationCorrectnessTest(final TestManifest testManifest) {
		super(testManifest);
	}

	@Override
	protected Reasoner createReasoner(final InputStream input) throws Owl2ParseException, IOException {
		IOReasoner reasoner = new IOReasoner();
		
		reasoner.loadOntologyFromStream(input);
		
		return reasoner;
	}

}
