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
import java.io.Writer;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.InstanceTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.TestIncompleteness;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.Diff;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalRealizationRunner<T>
		extends RandomWalkIncrementalClassificationRunner<T> {

	public RandomWalkIncrementalRealizationRunner(int rounds, int iter,
			RandomWalkRunnerIO<T> io) {
		super(rounds, iter, io);
	}

	@Override
	protected void writeResultDiff(Reasoner correctReasoner,
			Reasoner testReasoner, Writer writer)
			throws IOException, ElkException {
		writer.write("TAXONOMY DIFF:\n");
		Diff.writeDiff(new InstanceTaxonomyTestOutput(correctReasoner),
				new InstanceTaxonomyTestOutput(testReasoner), writer);
		writer.flush();
	}

	@Override
	protected void printResult(Reasoner reasoner, Writer writer)
			throws IOException, ElkException {

		IncompleteResult<? extends InstanceTaxonomy<ElkClass, ElkNamedIndividual>> taxonomy = reasoner
				.getInstanceTaxonomyQuietly();
		writer.append("INSTANCE TAXONOMY");
		if (taxonomy.getIncompletenessMonitor().isIncompletenessDetected()) {
			writer.append(" (incomplete)");
		}
		TaxonomyPrinter.dumpInstanceTaxomomy(
				TestIncompleteness.getValue(taxonomy), writer, false);
		writer.flush();
	}

	@Override
	protected String getResultHash(Reasoner reasoner) throws ElkException {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = TestIncompleteness
				.getValue(reasoner.getInstanceTaxonomyQuietly());

		return TaxonomyPrinter.getInstanceHashString(taxonomy);
	}

}
