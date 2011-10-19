/*
 * #%L
 * ELK Command Line Interface
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
import java.io.IOException;
import java.util.concurrent.Executors;

import org.semanticweb.elk.cli.IOReasoner;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomyPrinter;

public class TestReasoner {

	public static void main(String[] args) throws ParseException, IOException {
		IOReasoner reasoner = new IOReasoner(Executors.newCachedThreadPool(), 8);
		reasoner.loadOntologyFromFile("e:/krr/ontologies/snomed.owl");
		reasoner.classify();
		System.err.println(ClassTaxonomyPrinter.getHashString(reasoner.getTaxonomy()));
		reasoner.shutdown();
	}

}