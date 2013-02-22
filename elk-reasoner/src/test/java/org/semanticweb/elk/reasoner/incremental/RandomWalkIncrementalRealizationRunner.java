/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
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

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalRealizationRunner<T> extends
		RandomWalkIncrementalClassificationRunner<T> {

	public RandomWalkIncrementalRealizationRunner(int rounds, int iter, RandomWalkRunnerIO<T> io) {
		super(rounds, iter, io);
	}

	@Override
	protected void printResult(Reasoner reasoner, Logger logger, Level trace)
			throws IOException {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();
		StringWriter writer = new StringWriter();
		
		TaxonomyPrinter.dumpInstanceTaxomomy(taxonomy, writer, false);
		writer.flush();
		
		logger.log(trace, "INSTANCE TAXONOMY");
		logger.log(trace, writer.getBuffer());
		writer.close();
	}

	@Override
	protected String getResultHash(Reasoner reasoner) {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = reasoner.getInstanceTaxonomyQuietly();
		
		/*for (TypeNode<ElkClass, ElkNamedIndividual> typeNode : taxonomy.getTypeNodes()) {
			System.out.println(typeNode + ": " + typeNode.getDirectInstanceNodes());
		}*/
		
		return TaxonomyPrinter.getInstanceHashString(taxonomy);
	}
	
}
