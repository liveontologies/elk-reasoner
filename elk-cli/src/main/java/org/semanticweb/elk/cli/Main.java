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

import org.apache.log4j.Level;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
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
import org.semanticweb.elk.reasoner.taxonomy.hashing.InstanceTaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.logging.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * 
 * The command line interface for Elk reasoner. Typically for the usage within
 * stand-alone executables.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
public class Main {
	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 * @throws Exception
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
		OptionSpec<Void> printHash = parser.acceptsAll(asList("print-hash"),
				"print taxonomy hash to log");

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
		OptionSpec<String> logging = parser
				.acceptsAll(asList("logging", "l"),
						"logging level for log4j; default INFO")
				.withRequiredArg().ofType(String.class).describedAs("level");
		OptionSpec<Void> verbose = parser.acceptsAll(asList("verbose", "v"),
				"equivalent to --logging=DEBUG");
		OptionSpec<Void> Verbose = parser.acceptsAll(asList("Verbose", "V"),
				"equivalent to --logging=TRACE");
		OptionSpec<Void> quiet = parser.acceptsAll(asList("quiet", "q"),
				"equivalent to --logging=ERROR");

		// information
		OptionSpec<Void> version = parser.acceptsAll(asList("version"),
				"print version information");
		OptionSpec<Void> help = parser.acceptsAll(asList("help", "h", "?"),
				"show help");

		OptionSet options = parser.parse(args);

		// help
		if (!options.hasOptions() || options.has(help)) {
			parser.printHelpOn(System.out);
			return;
		}

		// version
		if (options.has(version)) {
			System.out.println(Main.class.getPackage().getImplementationTitle()
					+ " " + Main.class.getPackage().getImplementationVersion());
			return;
		}

		// input and reasoning tasks
		if (!options.has(inputFile)
				|| countOptions(options, satisfiable, classify, realize) != 1) {
			System.err
					.println("An input ontology and exactly one reasoning task are required!");
			return;
		}

		// logging
		if (countOptions(options, logging, verbose, Verbose, quiet) > 1) {
			System.err.println("Cannot set more than one logging level!");
			return;
		}
		// SLF4J does not allow setting the logging level; we use a concrete
		// binding
		org.apache.log4j.Logger allLoggers = org.apache.log4j.Logger
				.getLogger("org.semanticweb.elk");

		if (options.has(logging))
			allLoggers.setLevel(Level.toLevel(options.valueOf(logging),
					Level.INFO));
		if (options.has(verbose))
			allLoggers.setLevel(Level.DEBUG);
		if (options.has(Verbose))
			allLoggers.setLevel(Level.TRACE);
		if (options.has(quiet))
			allLoggers.setLevel(Level.ERROR);

		// number of workers
		ReasonerConfiguration configuration = ReasonerConfiguration
				.getConfiguration();
		if (options.has(nWorkers))
			configuration.setParameter(
					ReasonerConfiguration.NUM_OF_WORKING_THREADS, options
							.valueOf(nWorkers).toString());

		// create reasoner
		ReasonerFactory reasoningFactory = new ReasonerFactory();
		Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();
		AxiomLoader.Factory loader = new Owl2StreamLoader.Factory(parserFactory,
				options.valueOf(inputFile));
		Reasoner reasoner = reasoningFactory.createReasoner(loader,
				new LoggingStageExecutor(), configuration);

		try {
			if (options.has(satisfiable)) {
				boolean inconsistent = reasoner.isInconsistent();
				if (options.hasArgument(outputFile)) {
					writeConsistencyToFile(options.valueOf(outputFile),
							!inconsistent);
				}
			}
			
			boolean addHash = options.has(printHash);

			if (options.has(classify)) {
				Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomyQuietly();
			
				if (options.hasArgument(outputFile))
					writeClassTaxonomyToFile(options.valueOf(outputFile),
							taxonomy, addHash);
				if (addHash)
					printTaxonomyHash(taxonomy);
			}

			if (options.has(realize)) {
				InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = null;
				taxonomy = reasoner.getInstanceTaxonomyQuietly();
				if (options.hasArgument(outputFile))
					writeInstanceTaxonomyToFile(options.valueOf(outputFile),
							taxonomy, addHash);
				if (addHash)
					printTaxonomyHash(taxonomy);
			}

		} finally {
			reasoner.shutdown();
		}
	}

	static void writeConsistencyToFile(File file, Boolean consistent)
			throws IOException, ElkException {
		LOGGER_.info("Writing consistency to {}", file);

		FileWriter fstream = new FileWriter(file);
		BufferedWriter writer = new BufferedWriter(fstream);
		writer.write(consistent.toString() + "\n");
		writer.write("# The ontology is "
				+ (consistent ? "consistent" : "inconsistent") + ".\n");
		writer.close();
	}

	static void writeClassTaxonomyToFile(File file, Taxonomy<ElkClass> taxonomy, boolean printHash)
			throws IOException, ElkInconsistentOntologyException, ElkException {
		LOGGER_.info("Writing taxonomy to {}", file);

		Statistics.logOperationStart("Writing taxonomy", LOGGER_);
		TaxonomyPrinter.dumpTaxomomyToFile(taxonomy, file.getPath(), printHash);
		Statistics.logOperationFinish("Writing taxonomy", LOGGER_);
	}

	static void writeInstanceTaxonomyToFile(File file,
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy, boolean printHash)
			throws IOException, ElkInconsistentOntologyException, ElkException {
		LOGGER_.info("Writing taxonomy with instances to {}", file);

		Statistics
				.logOperationStart("Writing taxonomy with instances", LOGGER_);
		TaxonomyPrinter.dumpInstanceTaxomomyToFile(taxonomy, file.getPath(),
				printHash);
		Statistics.logOperationFinish("Writing taxonomy with instances",
				LOGGER_);
	}

	static void printTaxonomyHash(Taxonomy<ElkClass> taxonomy) {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Taxonomy hash: "
					+ Integer.toHexString(TaxonomyHasher.hash(taxonomy)));
		}
	}

	static void printTaxonomyHash(
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy) {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Taxonomy hash: "
					+ Integer.toHexString(InstanceTaxonomyHasher.hash(taxonomy)));
		}
	}

	static int countOptions(OptionSet options, OptionSpec<?>... specs) {
		int count = 0;
		for (OptionSpec<?> s : specs)
			if (options.has(s))
				count++;
		return count;
	}
}