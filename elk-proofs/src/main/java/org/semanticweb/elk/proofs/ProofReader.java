/**
 * 
 */
package org.semanticweb.elk.proofs;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.stages.ReasonerInferenceReader;

/**
 * The main entrance point for accessing proofs.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ProofReader {

	/**
	 * Starts reading proofs by retrieving the {@link DerivedExpression} which
	 * corresponds to the subsumption entailment between the give classes. The
	 * inferences can now be explored by calling
	 * {@code DerivedExpression#getInferences()} for each expression used as a
	 * premise.
	 * 
	 * @param reasoner
	 * @param subsumee
	 * @param subsumer
	 * @return
	 * @throws ElkException
	 */
	public static DerivedExpression start(Reasoner reasoner, ElkClass subsumee,
			ElkClass subsumer) throws ElkException {
		ReasonerInferenceReader reader = new ReasonerInferenceReader(reasoner);

		return reader.initialize(subsumee, subsumer);
	}
}
