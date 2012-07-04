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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;

/**
 * 
 * The command line interface for Elk reasoner. Typically for the usage within
 * stand-alone executables.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws Exception {

		OptionParser parser = new OptionParser();
		OptionSpec<File> inputOntologyFile = parser
				.acceptsAll(asList("input-ontology", "i"),
						"load ontology from the file").withRequiredArg()
				.ofType(File.class).describedAs("ontology");
		OptionSpec<Void> classify = parser.acceptsAll(asList("classify", "c"),
				"classify the ontology");
		OptionSpec<File> outputTaxonomyFile = parser
				.acceptsAll(asList("output-taxonomy", "o"),
						"print taxonomy to the file").withRequiredArg()
				.ofType(File.class).describedAs("taxonomy");
		OptionSpec<Integer> nWorkers = parser
				.acceptsAll(asList("w", "concurrent-workers"),
						"number of concurrent classification workers")
				.withOptionalArg().ofType(Integer.class);
		OptionSpec<Void> version = parser.acceptsAll(asList("version", "v"),
				"print version information");
		OptionSpec<Void> help = parser.acceptsAll(asList("h", "help", "?"),
				"show help");

		OptionSet options = parser.parse(args);

		if (options.has(help))
			parser.printHelpOn(System.out);
		else if (options.has(version))
			System.out.println(Main.class.getPackage().getImplementationTitle()
					+ " " + Main.class.getPackage().getImplementationVersion());
		else if (!options.has(inputOntologyFile))
			parser.printHelpOn(System.out);
		else {
			ReasonerFactory reasoningFactory = new ReasonerFactory();
			ReasonerConfiguration configuraion = ReasonerConfiguration
					.getConfiguration();
			if (options.has(nWorkers) && options.hasArgument(nWorkers))
				configuraion.setParameter(
						ReasonerConfiguration.NUM_OF_WORKING_THREADS, options
								.valueOf(nWorkers).toString());
			Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();
			Reasoner reasoner = reasoningFactory
					.createReasoner(new LoggingStageExecutor());
			reasoner.registerOntologyLoader(new Owl2StreamLoader(parserFactory,
					options.valueOf(inputOntologyFile)));
			reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());

			if (options.has(classify))
				reasoner.getTaxonomy();
			if (options.hasArgument(outputTaxonomyFile))
				reasoner.writeTaxonomyToFile(options
						.valueOf(outputTaxonomyFile));
			reasoner.shutdown();
		}
	}
}
