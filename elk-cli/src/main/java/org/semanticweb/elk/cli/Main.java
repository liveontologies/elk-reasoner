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
import java.util.concurrent.Executors;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.semanticweb.elk.owl.parsing.javacc.ParseException;

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
				.withOptionalArg().ofType(Integer.class).defaultsTo(4);
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
			IOReasoner reasoner = new IOReasoner(Executors.newCachedThreadPool(),
					nWorkers.value(options));
			reasoner.loadOntologyFromFile(inputOntologyFile.value(options));
			if (options.has(classify))
				reasoner.classify();
			if (options.hasArgument(outputTaxonomyFile))
				reasoner.writeTaxonomyToFile(outputTaxonomyFile.value(options));
			reasoner.shutdown();
		}
	}
}
