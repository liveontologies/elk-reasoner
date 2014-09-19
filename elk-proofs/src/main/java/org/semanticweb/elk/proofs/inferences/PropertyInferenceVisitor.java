/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import org.semanticweb.elk.proofs.inferences.properties.ChainSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.ReflexiveComposition;
import org.semanticweb.elk.proofs.inferences.properties.ReflexivityViaSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.SubsumptionViaReflexivity;
import org.semanticweb.elk.proofs.inferences.properties.ToldReflexivity;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface PropertyInferenceVisitor<I, O> {

	public O visit(ChainSubsumption inf, I input);
	
	public O visit(ReflexiveComposition inf, I input);
	
	public O visit(ReflexivityViaSubsumption inf, I input);
	
	public O visit(SubsumptionViaReflexivity inf, I input);
	
	public O visit(ToldReflexivity inf, I input);
}
