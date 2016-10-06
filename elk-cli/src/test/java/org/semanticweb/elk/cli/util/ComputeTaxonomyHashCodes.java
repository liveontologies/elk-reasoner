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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.io.FileUtils;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.FailingOnInterruptStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * Computes correct class taxonomy hash codes for a set of ontologies
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class ComputeTaxonomyHashCodes {

	static final String CLASSIFICATION_PATH = "../elk-reasoner/src/test/resources/classification_test_input";
	static final String REALIZATION_PATH = "../elk-reasoner/src/test/resources/realization_test_input";

	/**
	 * args[0]: path to the dir with source ontologies
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		generateHashCodes(CLASSIFICATION_PATH, new TestHasher() {

			@Override
			public int hash(Reasoner reasoner) {
				Taxonomy<ElkClass> taxonomy = null;

				try {
					taxonomy = reasoner.getTaxonomy();
				} catch (ElkInconsistentOntologyException e) {
					return 0;
				} catch (ElkException e) {
					// TODO: what to do?
					throw new RuntimeException(e);
				}

				return TaxonomyHasher.hash(taxonomy);
			}
		});

		generateHashCodes(REALIZATION_PATH, new TestHasher() {

			@Override
			public int hash(Reasoner reasoner) {
				InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = null;

				try {
					taxonomy = reasoner.getInstanceTaxonomy();
				} catch (ElkInconsistentOntologyException e) {
					return 0;
				} catch (ElkException e) {
					// TODO: what to do?
					throw new RuntimeException(e);
				}

				return InstanceTaxonomyHasher.hash(taxonomy);
			}
		});
	}

	static void generateHashCodes(String path, TestHasher hasher)
			throws IOException, Owl2ParseException, InterruptedException {
		File srcDir = new File(path);

		ReasonerFactory reasonerFactory = new ReasonerFactory();
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

			int hash = hasher.hash(reasoner);
			// create the expected result file
			File out = new File(srcDir.getAbsolutePath() + "/"
					+ FileUtils.dropExtension(ontFile.getName())
					+ ".expected.hash");
			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(out));

			writer.write(Integer.valueOf(hash).toString());
			writer.flush();
			writer.close();

			reasoner.shutdown();
		}
	}

	interface TestHasher {
		int hash(Reasoner reasoner);
	}
}