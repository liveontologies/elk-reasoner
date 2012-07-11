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
/**
 * @author Yevgeny Kazakov, Jun 11, 2011
 */
package org.semanticweb.elk.cli;

import static java.util.Arrays.asList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.inconsistent.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * 
 * The command line interface for Elk reasoner. Typically for the usage within
 * stand-alone executables.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class Main {
	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(Main.class);

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws Exception {

		OptionParser parser = new OptionParser();

		// input and output files
		OptionSpec<File> inputFile = parser
				.acceptsAll(asList("input", "i"), "input ontology file")
				.withRequiredArg().ofType(File.class).describedAs("ontology");
		OptionSpec<File> outputFile = parser
				.acceptsAll(asList("output", "o"), "output taxonomy file")
				.withRequiredArg().ofType(File.class).describedAs("taxonomy");

		// reasoning tasks
		OptionSpec<Void> classify = parser.acceptsAll(asList("classify", "c"),
				"classify the ontology");
		OptionSpec<Void> realize = parser.acceptsAll(asList("realize", "r"),
				"realize the ontology");
		OptionSpec<Void> satisfiable = parser.acceptsAll(
				asList("consistent", "satisfiable", "s"),
				"check consistency of the ontology");

		// configuration
		OptionSpec<Integer> nWorkers = parser
				.acceptsAll(asList("workers", "w"),
						"number of concurrent worker threads")
				.withRequiredArg().ofType(Integer.class);

		// information
		OptionSpec<Void> version = parser.acceptsAll(asList("version", "v"),
				"print version information");
		OptionSpec<Void> help = parser.acceptsAll(asList("help", "h", "?"),
				"show help");

		OptionSet options = parser.parse(args);

		if (!options.hasOptions() || options.has(help)) {
			parser.printHelpOn(System.out);
			return;
		}

		if (options.has(version)) {
			System.out.println(Main.class.getPackage().getImplementationTitle()
					+ " " + Main.class.getPackage().getImplementationVersion());
			return;
		}

		int countTasks = 0;
		if (options.has(satisfiable))
			countTasks++;
		if (options.has(classify))
			countTasks++;
		if (options.has(realize))
			countTasks++;
		if (!options.has(inputFile) || countTasks != 1) {
			System.err
					.println("Specify an input ontology and exactly one reasoning task.");
			return;
		}

		ReasonerConfiguration configuration = ReasonerConfiguration
				.getConfiguration();
		if (options.has(nWorkers))
			configuration.setParameter(
					ReasonerConfiguration.NUM_OF_WORKING_THREADS, options
							.valueOf(nWorkers).toString());

		ReasonerFactory reasoningFactory = new ReasonerFactory();
		Reasoner reasoner = reasoningFactory.createReasoner(
				new LoggingStageExecutor(), configuration);

		try {
			Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();
			reasoner.registerOntologyLoader(new Owl2StreamLoader(parserFactory,
					options.valueOf(inputFile)));

			reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());

			if (options.has(satisfiable)) {
				boolean consistent = reasoner.isConsistent();
				if (options.hasArgument(outputFile)) {
					writeConsistencyToFile(options.valueOf(outputFile),
							consistent);
				}
			}

			if (options.has(classify)) {
				Taxonomy<ElkClass> taxonomy = null;
				try {
					taxonomy = reasoner.getTaxonomy();
				} catch (ElkInconsistentOntologyException e) {
					taxonomy = PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY;
				}
				if (options.hasArgument(outputFile))
					writeClassTaxonomyToFile(options.valueOf(outputFile),
							taxonomy);
			}

			if (options.has(realize)) {
				InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = null;
				try {
					taxonomy = reasoner.getInstanceTaxonomy();
				} catch (ElkInconsistentOntologyException e) {
					taxonomy = PredefinedTaxonomy.INCONSISTENT_INDIVIDUAL_TAXONOMY;
				}
				if (options.hasArgument(outputFile))
					writeInstanceTaxonomyToFile(options.valueOf(outputFile),
							taxonomy);
			}

		} finally {
			reasoner.shutdown();
		}

		if (!options.has(outputFile)) {
			System.out
					.println("No output was produced because the output file was not specified.");
		}
	}

	static void writeConsistencyToFile(File file, Boolean consistent)
			throws IOException, ElkException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing consistency to " + file);
		}
		FileWriter fstream = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(fstream);
		writer.write(consistent.toString() + "\n");
		writer.write("# The ontology is "
				+ (consistent ? "consistent" : "inconsistent") + ".\n");
		writer.close();
	}

	static void writeClassTaxonomyToFile(File file, Taxonomy<ElkClass> taxonomy)
			throws IOException, ElkInconsistentOntologyException, ElkException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing taxonomy to " + file);
		}
		Statistics.logOperationStart("Writing taxonomy", LOGGER_);
		TaxonomyPrinter.dumpClassTaxomomyToFile(taxonomy, file.getPath(), true);
		Statistics.logOperationFinish("Writing taxonomy", LOGGER_);
	}

	static void writeInstanceTaxonomyToFile(File file,
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy)
			throws IOException, ElkInconsistentOntologyException, ElkException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing taxonomy with instances to " + file);
		}
		Statistics
				.logOperationStart("Writing taxonomy with instances", LOGGER_);
		TaxonomyPrinter.dumpInstanceTaxomomyToFile(taxonomy, file.getPath(),
				true);
		Statistics.logOperationFinish("Writing taxonomy with instances",
				LOGGER_);
	}
}