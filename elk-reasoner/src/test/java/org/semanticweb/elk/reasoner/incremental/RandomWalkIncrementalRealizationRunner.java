/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

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
		return TaxonomyPrinter.getInstanceHashString(reasoner.getInstanceTaxonomyQuietly());
	}
	
}
