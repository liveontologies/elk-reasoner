/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalTaxonomyCleaningStage extends AbstractReasonerStage {

	public IncrementalTaxonomyCleaningStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CLEANING.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.TAXONOMY_CLEANING);
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new IncrementalConsistencyCheckingStage(reasoner));
	}

	@Override
	public void execute() throws ElkException {
		// TODO Auto-generated method stub

	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}
}
