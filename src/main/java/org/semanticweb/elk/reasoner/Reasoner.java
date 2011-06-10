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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.parser.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.parser.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.classification.ClassificationManager;
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.SerialOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.ObjectPropertySaturation;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.parsing.ConcurrentFutureElkAxiomLoader;
import org.semanticweb.elk.util.Statistics;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int workerNo;

	protected final OntologyIndex ontologyIndex;

	protected ClassTaxonomy classTaxonomy;

	// logger for events
	protected final static Logger logger = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.ontologyIndex = new SerialOntologyIndex();
	}
	
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), 16);
	}
	
	public void loadOntologyFromStream(InputStream stream, ElkAxiomProcessor elkAxiomProcessor)
	throws ParseException, IOException {
		Statistics.logOperationStart("Loading", logger);

		ConcurrentFutureElkAxiomLoader loader = new ConcurrentFutureElkAxiomLoader(executor, 1, elkAxiomProcessor);
		Owl2FunctionalStyleParser.Init(stream);
		Owl2FunctionalStyleParser.ontologyDocument(loader);
		stream.close();
		loader.waitCompletion();
		Statistics.logOperationFinish("Loading", logger);
		Statistics.logMemoryUsage(logger);		
	}

	public void loadOntologyFromStream(InputStream stream)
			throws ParseException, IOException {
		loadOntologyFromStream(stream, ontologyIndex.getAxiomIndexer());
	}

	public void loadOntologyFromFile(String fileName, ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Loading ontology from " + fileName);
		}
		loadOntologyFromStream(new FileInputStream(fileName), elkAxiomProcessor);
	}
	
	public void loadOntologyFromFile(String fileName) throws ParseException,
	IOException {
		loadOntologyFromFile(fileName, ontologyIndex.getAxiomIndexer());
	}

	public void loadOntologyFromString(String text, ElkAxiomProcessor elkAxiomProcessor) throws ParseException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Loading ontology from string");
		}
		loadOntologyFromStream(new ByteArrayInputStream(text.getBytes()), elkAxiomProcessor);
	}
	
	public void loadOntologyFromString(String text) throws ParseException,
	IOException {
		loadOntologyFromString(text, ontologyIndex.getAxiomIndexer());
	}

	public void classify() {
		// Saturation stage

		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo);

		ClassExpressionSaturation classExpressionSaturation = new ClassExpressionSaturation(
				executor, workerNo);

		Statistics.logOperationStart("Saturation", logger);

		for (IndexedObjectProperty iop : ontologyIndex
				.getIndexedObjectProperties())
			objectPropertySaturation.submit(iop);
		objectPropertySaturation.waitCompletion();

		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			classExpressionSaturation.submit(ic);
		classExpressionSaturation.waitCompletion();

		Statistics.logOperationFinish("Saturation", logger);
		Statistics.logMemoryUsage(logger);

		// Transitive reduction stage
		Statistics.logOperationStart("Transitive reduction", logger);

		ClassificationManager classificationManager = new ClassificationManager(
				executor, workerNo, ontologyIndex);
		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			classificationManager.submit((ElkClass) ic.getClassExpression());
		classTaxonomy = classificationManager.getClassTaxonomy();

		Statistics.logOperationFinish("Transitive reduction", logger);
		Statistics.logMemoryUsage(logger);
	}

	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void shutdown() {
		executor.shutdownNow();
	}
}
