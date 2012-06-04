/*
 * #%L
 * ELK Command Line Interface
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
package org.semanticweb.elk.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.iris.ElkPrefixDeclarationsImpl;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.reasoner.InconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyPrinter;
import org.semanticweb.elk.util.logging.Statistics;

public class IOReasoner extends Reasoner {

	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(IOReasoner.class);

	public IOReasoner(ReasonerStageExecutor stageExecutor,
			ExecutorService executor, int workerNo) {
		super(stageExecutor, executor, workerNo);
	}

	public void loadOntologyFromStream(InputStream stream) throws IOException,
			Owl2ParseException {
		Statistics.logOperationStart("Loading", LOGGER_);

		reset();
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		parser.setPrefixDeclarations(new ElkPrefixDeclarationsImpl());
		parser.parseOntology(getAxiomInserter());
		stream.close();
		Statistics.logOperationFinish("Loading", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
	}

	public void loadOntologyFromFile(File file) throws IOException,
			Owl2ParseException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Loading ontology from " + file);
		}
		loadOntologyFromStream(new FileInputStream(file));
	}

	public void loadOntologyFromFile(String fileName)
			throws Owl2ParseException, IOException {
		loadOntologyFromFile(new File(fileName));
	}

	public void loadOntologyFromString(String text) throws Owl2ParseException,
			IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Loading ontology from string");
		}
		loadOntologyFromStream(new ByteArrayInputStream(text.getBytes()));
	}

	public void writeTaxonomyToFile(File file) throws IOException,
			InconsistentOntologyException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing taxonomy to " + file);
		}
		Statistics.logOperationStart("Writing taxonomy", LOGGER_);
		ClassTaxonomyPrinter.dumpClassTaxomomyToFile(this.getTaxonomy(),
				file.getPath(), true);
		Statistics.logOperationFinish("Writing taxonomy", LOGGER_);
	}

	// used only in tests
	@Override
	protected OntologyIndex getOntologyIndex() {
		return super.getOntologyIndex();
	}

}
