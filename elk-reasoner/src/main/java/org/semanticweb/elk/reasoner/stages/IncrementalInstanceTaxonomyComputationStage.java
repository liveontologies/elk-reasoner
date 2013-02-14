/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalInstanceTaxonomyComputationStage extends
		InstanceTaxonomyComputationStage {

	/**
	 * @param reasoner
	 * @param preStages
	 */
	public IncrementalInstanceTaxonomyComputationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}	
	
	@Override
	public String getName() {
		return "Incremental Instance Taxonomy Computation";
	}

	@Override
	boolean preExecute() {
		// TODO Auto-generated method stub
		return super.preExecute();
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		// TODO Auto-generated method stub
		super.executeStage();
	}

	@Override
	boolean postExecute() {
		// TODO Auto-generated method stub
		return super.postExecute();
	}



	
	
}
