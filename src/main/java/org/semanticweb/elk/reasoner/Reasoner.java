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
package org.semanticweb.elk.reasoner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.parser.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.parser.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomyComputation;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomyPrinter;
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.SerialOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.ObjectPropertySaturation;
import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.parsing.ConcurrentFutureElkAxiomLoader;
import org.semanticweb.elk.util.Statistics;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int workerNo;

	protected final OntologyIndex ontologyIndex;
	
	protected final ElkAxiomProcessor axiomInserter, axiomDeleter;

	protected ClassTaxonomy classTaxonomy;

	// logger for events
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.ontologyIndex = new SerialOntologyIndex();
		axiomInserter = ontologyIndex.getAxiomInserter();
		axiomDeleter = ontologyIndex.getAxiomDeleter();
	}

	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), 16);
	}

	public void loadOntologyFromStream(InputStream stream,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		Statistics.logOperationStart("Loading", LOGGER_);

		ConcurrentFutureElkAxiomLoader loader = new ConcurrentFutureElkAxiomLoader(
				executor, 1, elkAxiomProcessor);
		Owl2FunctionalStyleParser parser = new Owl2FunctionalStyleParser(stream);
		parser.ontologyDocument(loader);
		stream.close();
		loader.waitCompletion();
		Statistics.logOperationFinish("Loading", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
	}

	public void loadOntologyFromStream(InputStream stream)
			throws ParseException, IOException {
		loadOntologyFromStream(stream, axiomInserter);
	}

	public void loadOntologyFromFile(File file,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Loading ontology from " + file);
		}
		loadOntologyFromStream(new FileInputStream(file), elkAxiomProcessor);
	}

	public void loadOntologyFromFile(String fileName,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		loadOntologyFromFile(new File(fileName), elkAxiomProcessor);
	}

	public void loadOntologyFromFile(File file) throws ParseException,
			IOException {
		loadOntologyFromFile(file, axiomInserter);
	}

	public void loadOntologyFromFile(String fileName) throws ParseException,
			IOException {
		loadOntologyFromFile(new File(fileName));
	}

	public void loadOntologyFromString(String text,
			ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Loading ontology from string");
		}
		loadOntologyFromStream(new ByteArrayInputStream(text.getBytes()),
				elkAxiomProcessor);
	}

	public void loadOntologyFromString(String text) throws ParseException,
			IOException {
		loadOntologyFromString(text, axiomInserter);
	}
	
	public void addAxiom(ElkAxiom axiom) {
		axiomInserter.process(axiom);
	}
	
	public void removeAxiom(ElkAxiom axiom) {
		axiomDeleter.process(axiom);
	}

	public void classify() {
		// Saturation stage

		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		ClassExpressionSaturation classExpressionSaturation = new ClassExpressionSaturation(
				executor, workerNo, ontologyIndex);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Saturation using " + workerNo + " workers");
		Statistics.logOperationStart("Saturation", LOGGER_);

		for (IndexedObjectProperty iop : ontologyIndex
				.getIndexedObjectProperties())
			objectPropertySaturation.submit(iop);
		objectPropertySaturation.waitCompletion();

		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			classExpressionSaturation.submit(ic);
		classExpressionSaturation.waitCompletion();

		Statistics.logOperationFinish("Saturation", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);

		// Transitive reduction stage
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Transitive reduction using " + workerNo + " workers");
		Statistics.logOperationStart("Transitive reduction", LOGGER_);

		ClassTaxonomyComputation classification = new ClassTaxonomyComputation(
				executor, workerNo);
		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			classification.submit(ic);
		classTaxonomy = classification.computeTaxonomy();

		Statistics.logOperationFinish("Transitive reduction", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
	}

	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void writeTaxonomyToFile(File file) throws IOException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing taxonomy to " + file);
		}
		Statistics.logOperationStart("Writing taxonomy", LOGGER_);
		ClassTaxonomyPrinter.dumpClassTaxomomyToFile(this.getTaxonomy(),
				file.getPath(), true);
		Statistics.logOperationFinish("Writing taxonomy", LOGGER_);
	}

	public void shutdown() {
		executor.shutdownNow();
	}
}
