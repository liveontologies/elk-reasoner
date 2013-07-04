/**
 * 
 */
package org.semanticweb.elk.ore;

/*
 * #%L
 * ELK Command Line Interface
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * The CLI tool for the ORE 2013 contest which uses the ELK's native OWL FSS
 * parser and includes the parsing/loading/indexing time in the total wall time.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NativeRunner {
	
	enum Task {
		SAT, QUERY, CLASSIFICATION, CONSISTENCY
	};

	/**
	 * Arguments are as follows: 0 - name of the reasoning task (SAT, query,
	 * classification, consistency) 1 - ontology path 2 - output path 3 -
	 * concept URI, in case of SAT
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Task task = validateArgs(args);
		// help
		if (task == null) {
			printHelp();
			return;
		}

		NativeRunner runner = new NativeRunner();
		
		runner.run(args, task);
	}
	
	void run(String[] args, Task task) throws Exception {
		File input = getOutputFile(args[1]);
		File output = getOutputFile(args[2]);
		// create reasoner
		ReasonerFactory reasoningFactory = new ReasonerFactory();
		Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();
		OntologyLoader loader = new Owl2StreamLoader(parserFactory, input);
		Reasoner reasoner = reasoningFactory.createReasoner(loader,
				new SimpleStageExecutor());

		try {
			reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());
			
			loadOntology(reasoner);
			
			long ts = System.currentTimeMillis();

			System.out.println("Started " + task.toString() + " on " + input);

			switch (task) {
			case SAT:
				String sep = System.getProperty("line.separator");
				ElkObjectFactory factory = new ElkObjectFactoryImpl();
				boolean isSat = reasoner.isSatisfiable(factory
						.getClass(new ElkFullIri(args[3])));

				printTime(ts);

				writeStringToFile(output, args[3] + "," + String.valueOf(isSat)
						+ sep, true);

				printCompleted(task, input);

				break;
			case CLASSIFICATION:
				Taxonomy<ElkClass> taxonomy = reasoner.getTaxonomyQuietly();

				printTime(ts);

				writeClassTaxonomyToFile(output, taxonomy);

				printCompleted(task, input);

				break;
			case CONSISTENCY:
				boolean isConsistent = reasoner.isInconsistent();

				printTime(ts);

				writeStringToFile(output, String.valueOf(!isConsistent), false);

				printCompleted(task, input);

				break;
			default:
			}
		} catch (ElkLoadingException e) {
			System.err
					.println("ELK could not parse the ontology. Please, make sure that it is in the OWL 2 Functional Style Syntax.");
		} finally {
			reasoner.shutdown();
		}
	}

	protected void loadOntology(Reasoner reasoner) throws ElkException {
		// no eager loading so it will be done later and included in measurements
	}

	private File getOutputFile(String path) {
		File file = new File(path);

		try {
			if (!file.isAbsolute()) {
				file = new File(new File(".").getCanonicalPath() + "/" + path);
			}

			file.getParentFile().mkdirs();

			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new IOException(
							"Output file does not exist and cannot be created");
				}
			}
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}

		return file;
	}

	private void printTime(long ts) {
		System.out.println("Operation time: "
				+ (System.currentTimeMillis() - ts));

	}

	private void printCompleted(Task task, File input) {
		System.out.println("Completed " + task.toString() + " on " + input);
	}

	static Task validateArgs(String[] args) {
		Task task = null;

		if (args.length < 3) {
			return null;// not enough arguments
		}

		for (Task t : Task.values()) {
			if (t.name().equalsIgnoreCase(args[0])) {
				task = t;
				break;
			}
		}

		if (Task.SAT == task && args.length < 4) {
			System.out.println("Missing concept URI for the SAT task");
			return null;
		}

		// strip possible quotes
		args[1] = stripQuotes(args[1]);
		args[2] = stripQuotes(args[2]);

		if (args.length >= 4) {
			args[3] = stripQuotes(args[3]);
		}

		return task;
	}

	private static String stripQuotes(String arg) {
		return arg.replaceAll("^\"|\"$", "");
	}

	static void printHelp() {
		System.out
				.println("The system requires the following command line arguments:\n"
						+ "* name of the reasoning task, one of: SAT, QUERY (not supported), CONSISTENCY, CLASSIFICATION, case insensitive\n"
						+ "* path to the ontology file\n"
						+ "* path to the output file\n"
						+ "* concept URI, in case of SAT");
	}

	void writeStringToFile(File file, String string, boolean append)
			throws IOException, ElkException {
		FileWriter fstream = new FileWriter(file, append);
		BufferedWriter writer = new BufferedWriter(fstream);

		writer.write(string);
		writer.flush();
		writer.close();
	}

	void writeClassTaxonomyToFile(File file, Taxonomy<ElkClass> taxonomy)
			throws IOException, ElkInconsistentOntologyException, ElkException {

		TaxonomyPrinter
				.dumpClassTaxomomyToFile(taxonomy, file.getPath(), false);
	}

	void writeInstanceTaxonomyToFile(File file,
			InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy)
			throws IOException, ElkInconsistentOntologyException, ElkException {
		TaxonomyPrinter.dumpInstanceTaxomomyToFile(taxonomy, file.getPath(),
				false);
	}

}