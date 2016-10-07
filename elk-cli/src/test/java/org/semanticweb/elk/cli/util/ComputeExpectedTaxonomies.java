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
import java.io.Writer;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.FailingOnInterruptStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ComputeExpectedTaxonomies {

	static final String CLASSIFICATION_PATH = "../elk-reasoner/src/test/resources/classification_test_input";
	static final String REALIZATION_PATH = "../elk-reasoner/src/test/resources/realization_test_input";

	static final ReasonerFactory reasonerFactory = new ReasonerFactory();

	/**
	 * args[0]: path to the dir with source ontologies
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		generateExpectedTaxonomy(CLASSIFICATION_PATH,
				new GetTaxonomy<ElkClass>() {

					@Override
					public Taxonomy<ElkClass> getTaxonomy(Reasoner reasoner)
							throws ElkException {
						return reasoner.getTaxonomyQuietly();
					}

					@Override
					public void dumpTaxonomy(Taxonomy<ElkClass> taxonomy,
							Writer writer) throws IOException {
						TaxonomyPrinter.dumpTaxomomy(taxonomy, writer,
								false);
					}
				});

		generateExpectedTaxonomy(REALIZATION_PATH, new GetTaxonomy<ElkClass>() {

			@Override
			public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy(
					Reasoner reasoner) throws ElkException {
				return reasoner.getInstanceTaxonomyQuietly();
			}

			@SuppressWarnings("unchecked")
			@Override
			public void dumpTaxonomy(Taxonomy<ElkClass> taxonomy, Writer writer)
					throws IOException {
				TaxonomyPrinter
						.dumpInstanceTaxomomy(
								(InstanceTaxonomy<ElkClass, ElkNamedIndividual>) taxonomy,
								writer, false);
			}
		});
	}

	static void generateExpectedTaxonomy(String path, GetTaxonomy<ElkClass> gt)
			throws IOException, Owl2ParseException, ElkException,
			InterruptedException {
		File srcDir = new File(path);

		ReasonerConfiguration configuraion = ReasonerConfiguration
				.getConfiguration();
		// use just one worker to minimize the risk of errors
		configuraion.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				"1");

		File[] ontFiles = srcDir.listFiles(FileUtils
				.getExtBasedFilenameFilter("owl"));
		
		if (ontFiles == null) {
			throw new RuntimeException("Not a directory: " + path);
		}
		
		for (File ontFile : ontFiles) {

			System.err.println(ontFile.getName());

			AxiomLoader loader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontFile);
			Reasoner reasoner = reasonerFactory.createReasoner(loader,
					new FailingOnInterruptStageExecutor(), configuraion);

			Taxonomy<ElkClass> taxonomy = gt.getTaxonomy(reasoner);
			// create the expected result file
			File out = new File(srcDir.getAbsolutePath() + "/"
					+ FileUtils.dropExtension(ontFile.getName()) + ".expected");
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(out));

			gt.dumpTaxonomy(taxonomy, writer);

			writer.flush();
			writer.close();
			reasoner.shutdown();
		}

	}

	interface GetTaxonomy<T extends ElkEntity> {
		Taxonomy<T> getTaxonomy(Reasoner reasoner) throws ElkException;

		void dumpTaxonomy(Taxonomy<T> taxonomy, Writer writer)
				throws IOException;
	}
}