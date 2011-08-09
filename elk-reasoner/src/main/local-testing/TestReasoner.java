/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.util.ElkTimer;
import org.semanticweb.elk.util.Statistics;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomyPrinter;
import org.semanticweb.elk.syntax.ElkAxiomBuffer;
import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.parsing.javacc.ParseException;
import org.semanticweb.elk.syntax.preprocessing.MultiplyingAxiomProcessor;

/**
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class TestReasoner {

	protected final static Logger logger = Logger.getLogger(TestReasoner.class);

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		System.out.print("Press return to start parsing.");
		System.in.read();

		// ElkTimer mainTimer = ElkTimer.getTimerForCurrentThread("Main timer");
		// ElkTimer microTimer =
		// ElkTimer.getTimerForCurrentThread("Micro timer");
		// mainTimer.start();
		// int i = 0;
		// while ( i < 1000 ) {
		// microTimer.start();
		// i = i+1;
		// microTimer.stop();
		// }
		// mainTimer.stop();
		// mainTimer.log(logger);
		// microTimer.log(logger);
		//		
		// if (1==1) return;

		final String filename = "/home/markus/Dokumente/Ontologies/ElkTesting/snomed_functional.owl";
		//final String filename =
		//"/home/markus/Dokumente/Ontologies/EL-GALEN.owl";

		ElkTimer.startNamedTimer("TestReasoner.main()");

		Reasoner reasoner = new Reasoner(Executors.newCachedThreadPool(), 16);
		ElkAxiomProcessor axiomInserter = reasoner.getOntologyIndex()
				.getAxiomInserter();
		ElkAxiomBuffer axiomBuffer = new ElkAxiomBuffer(); 
//		MultiplyingAxiomProcessor multiplier = new MultiplyingAxiomProcessor(
//				axiomBuffer, 1);
		reasoner.loadOntologyFromFile(filename, axiomBuffer);

//		Statistics.logOperationStart("Rebuffering axioms", logger);
//		ElkAxiomBuffer axiomBuffer2 = new ElkAxiomBuffer();
//		axiomBuffer.sendAxiomsToProcessor(axiomBuffer2);
//		Statistics.logOperationFinish("Rebuffering axioms", logger);
//		Statistics.logMemoryUsage(logger);
		
		Statistics.logOperationStart("Indexing axioms", logger);
		axiomBuffer.sendAxiomsToProcessor(axiomInserter);
		Statistics.logOperationFinish("Indexing axioms", logger);
		Statistics.logMemoryUsage(logger);
		ElkTimer.stopNamedTimer("TestReasoner.main()");

		System.out.print("\nPress return to start computing taxonomy.");
		System.in.read();

		ElkTimer.startNamedTimer("TestReasoner.main()");

		reasoner.classify();

		reasoner.shutdown();

		ElkTimer.stopNamedTimer("TestReasoner.main()");

		ElkTimer.getNamedTotalTimer("ConcurrentSaturation#process").log(logger);
		ElkTimer.logAllNamedTimers("ConcurrentSaturation#process", logger);

		System.out.print("\nPress return to start computing hash code.");
		System.in.read();

		ElkTimer.startNamedTimer("TestReasoner.main()");
		ElkTimer.startNamedTimer("hashing taxonomy");

		System.out.print("Computing hash code...");
		String hashCode = ClassTaxonomyPrinter.getHashString(reasoner
				.getTaxonomy());
		System.out.println("Hash code: " + hashCode);
		System.out.println("Children hash code: " + reasoner
				.getTaxonomy().getChildrenBasedTaxonomyHash());
		System.out.println("Parent hash code: " + reasoner
				.getTaxonomy().getParentBasedTaxonomyHash());
		System.out.println("Number of nodes in taxonomy: "
				+ reasoner.getTaxonomy().getNodes().size());

//		ClassTaxonomyPrinter.dumpClassTaxomomyToFile(reasoner.getTaxonomy(),
//				"/home/markus/Dokumente/Ontologies/EL-GALEN-tax.owl", false);

		ElkTimer.stopNamedTimer("hashing taxonomy");
		ElkTimer.stopNamedTimer("TestReasoner.main()");
		ElkTimer.getNamedTimer("hashing taxonomy").log(logger);

		ElkTimer.getNamedTimer("TestReasoner.main()").log(logger);

	}

}
