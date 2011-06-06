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
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.semanticweb.elk.parser.javacc.Owl2FunctionalStyleParser;
import org.semanticweb.elk.parser.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.classification.ClassificationManager;
import org.semanticweb.elk.reasoner.indexing.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.IndexingManager;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationManager;
import org.semanticweb.elk.reasoner.saturation.ObjectPropertySaturationManager;
import org.semanticweb.elk.reasoner.saturation.Saturation;
import org.semanticweb.elk.syntax.ElkAxiom;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.parsing.OntologyLoader;
import org.semanticweb.elk.util.Statistics;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int nWorkers;

	protected final IndexingManager indexingManager;

	protected final OntologyLoader ontologyLoader;

	protected ClassTaxonomy classTaxonomy = null;

	// logger for events
	protected final static Logger logger = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int nWorkers) {
		this.executor = executor;
		this.nWorkers = nWorkers;
		this.indexingManager = new IndexingManager(executor, 1);
		this.ontologyLoader = new OntologyLoaderImpl();
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), 16);
	}

	class OntologyLoaderImpl implements OntologyLoader {

		public void loadFutureAxiom(Future<? extends ElkAxiom> futureAxiom) {
			if (futureAxiom != null)
				indexingManager.submit(futureAxiom);
		}
	}

	public void loadOntologyFromStream(InputStream stream)
			throws ParseException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Loading started");
		}
		Owl2FunctionalStyleParser.Init(stream);
		Owl2FunctionalStyleParser.ontologyDocument(ontologyLoader);
		stream.close();
		if (logger.isInfoEnabled()) {
			logger.info("Loading finished");
		}
		Statistics.logMemoryUsage(logger);
		indexingManager.waitCompletion();
	}

	public void loadOntologyFromFile(String fileName) throws ParseException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Loading ontology from " + fileName);
		}
		loadOntologyFromStream(new FileInputStream(fileName));
	}

	public void loadOntologyFromString(String text) throws ParseException,
			IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Loading ontology from string");
		}
		loadOntologyFromStream(new ByteArrayInputStream(text.getBytes()));
	}

	public void classify() {
		// Saturation stage
		OntologyIndex ontologyIndex = indexingManager.computeOntologyIndex();
	
		ObjectPropertySaturationManager objectPropertySaturationManager =
			new ObjectPropertySaturationManager();
	
		ClassExpressionSaturationManager classExpressionSaturationManager = 
			new ClassExpressionSaturationManager(executor, nWorkers);

		if (logger.isInfoEnabled()) {
			logger.info("Saturation started");
		}

		for (IndexedObjectProperty iop : ontologyIndex.getIndexedObjectProperties())
			objectPropertySaturationManager.submit(iop);
		objectPropertySaturationManager.computeSaturation();

		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			classExpressionSaturationManager.submit(ic);
		Saturation saturation = classExpressionSaturationManager.computeSaturation();
		if (logger.isInfoEnabled()) {
			logger.info("Saturation finished");
		}
		Statistics.logMemoryUsage(logger);
		// Transitive reduction stage
		if (logger.isInfoEnabled()) {
			logger.info("Transitive reduction started");
		}
		ClassificationManager classificationManager = new ClassificationManager(
				executor, nWorkers, ontologyIndex, saturation);
		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			classificationManager.submit((ElkClass) ic.classExpression);
		classTaxonomy = classificationManager.getClassTaxonomy();
		if (logger.isInfoEnabled()) {
			logger.info("Transitive reduction finished");
		}
		Statistics.logMemoryUsage(logger);
	}

	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void shutdown() {
		executor.shutdownNow();
	}
}
