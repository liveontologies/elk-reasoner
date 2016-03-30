/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.proofs;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.tracing.TracingTestManifest;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The base class for tests of proofs.
 * 
 * @author Peter Skocovsky
 */
public class BaseProofTest {

	protected final TracingTestManifest manifest_;
	protected final OWLOntologyManager manager_;

	public BaseProofTest(final TracingTestManifest manifest) {
		this.manifest_ = manifest;
		this.manager_ = OWLManager.createOWLOntologyManager();
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest_.getInput()));
	}

	protected boolean ignore(final TestInput input) {
		return false;
	}

	protected OWLOntology loadOntology(final InputStream stream)
			throws IOException, Owl2ParseException {

		try {
			return manager_.loadOntologyFromOntologyDocument(stream);
		} catch (OWLOntologyCreationIOException e) {
			throw new IOException(e);
		} catch (OWLOntologyCreationException e) {
			throw new Owl2ParseException(e);
		}
	}

}
