/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.LocalSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;

/**
 * A generic factory for enumerating all conclusions which logically belong to a
 * given context. Input contexts may not be closed under the rules but all
 * remaining conclusions must be derivable.
 * 
 * The factory is supposed to be used by subclasses which implement specific
 * logic for each observed conclusion.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ConclusionIterationFactory extends RuleApplicationFactory {

	protected final SaturationState localSaturationState;
	
	/**
	 * @param saturationState
	 */
	public ConclusionIterationFactory(ExtendedSaturationState saturationState) {
		super(saturationState);
		localSaturationState = new LocalSaturationState(saturationState.getOntologyIndex());
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	protected class IterationWriter extends RuleApplicationFactory.DefaultEngine {

		protected IterationWriter(ContextCreationListener listener,
				ContextModificationListener modListener) {
			super(listener, modListener);
			// TODO Auto-generated constructor stub
		}
		
		
	}
}
