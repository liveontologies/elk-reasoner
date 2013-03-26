/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturationStateFactory {

	/**
	 * 
	 * @param ontologyIndex
	 * @return
	 */
	public static SaturationState createSaturationState(OntologyIndex ontologyIndex) {
		return new SaturationStateImpl(ontologyIndex);
	}
}
