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
package org.semanticweb.elk.cli.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.loading.OntologyStreamLoader;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.InconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.TestStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.InconsistentTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.Taxonomy;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ComputeExpectedTaxonomies {

	static final String CLASSIFICATION_PATH = "../elk-reasoner/src/test/resources/classification_test_input";

	/**
	 * args[0]: path to the dir with source ontologies
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

		generateExpectedTaxonomy(CLASSIFICATION_PATH,
				new GetTaxonomy<ElkClass>() {

					@Override
					public Taxonomy<ElkClass> getTaxonomy(Reasoner reasoner) {
						try {
							return reasoner.getTaxonomy();
						} catch (InconsistentOntologyException e) {
							System.err.println("Inconsistent!");

							return new InconsistentTaxonomy<ElkClass>(
									objectFactory.getOwlThing(), objectFactory
											.getOwlNothing());
						}
					}
				});
	}

	static void generateExpectedTaxonomy(String path, GetTaxonomy<ElkClass> gt)
			throws IOException, Owl2ParseException {
		File srcDir = new File(path);

		ReasonerFactory reasonerFactory = new ReasonerFactory();
		ReasonerConfiguration configuraion = ReasonerConfiguration
				.getConfiguration();
		// use just one worker to minimize the risk of errors
		configuraion.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				"1");

		for (File ontFile : srcDir.listFiles(FileUtils
				.getExtBasedFilenameFilter("owl"))) {

			System.err.println(ontFile.getName());

			Reasoner reasoner = reasonerFactory.createReasoner(
					new OntologyStreamLoader(
							new Owl2FunctionalStyleParserFactory(), ontFile),
					new TestStageExecutor(), configuraion);

			Taxonomy<ElkClass> taxonomy = gt.getTaxonomy(reasoner);

			if (taxonomy != null) {
				// create the expected result file
				File out = new File(srcDir.getAbsolutePath() + "/"
						+ FileUtils.dropExtension(ontFile.getName())
						+ ".expected");
				OutputStreamWriter writer = new OutputStreamWriter(
						new FileOutputStream(out));

				ClassTaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);

				writer.flush();
				writer.close();
			} else {
				// TODO create inconistent ontology
			}
			reasoner.shutdown();
		}

	}

	interface GetTaxonomy<T extends ElkObject> {
		Taxonomy<T> getTaxonomy(Reasoner reasoner);
	}
}