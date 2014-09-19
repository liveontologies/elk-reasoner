/**
 * 
 */
package org.semanticweb.elk.proofs.inferences;

import org.semanticweb.elk.proofs.inferences.classes.ClassInitialization;
import org.semanticweb.elk.proofs.inferences.classes.ClassSubsumption;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ConjunctionDecomposition;
import org.semanticweb.elk.proofs.inferences.classes.DisjointnessContradiction;
import org.semanticweb.elk.proofs.inferences.classes.DisjunctionComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ExistentialCompositionViaChain;
import org.semanticweb.elk.proofs.inferences.classes.InconsistentDisjointness;
import org.semanticweb.elk.proofs.inferences.classes.NegationContradiction;
import org.semanticweb.elk.proofs.inferences.classes.ReflexiveExistentialComposition;
import org.semanticweb.elk.proofs.inferences.classes.ThingInitialization;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ClassInferenceVisitor<I, O> {

	public O visit(ClassInitialization inf, I input);
	
	public O visit(ThingInitialization inf, I input);
	
	public O visit(ClassSubsumption inf, I input);
	
	public O visit(ConjunctionComposition inf, I input);
	
	public O visit(ConjunctionDecomposition inf, I input);
	
	public O visit(DisjointnessContradiction inf, I input);
	
	public O visit(DisjunctionComposition inf, I input);
	
	public O visit(ExistentialComposition inf, I input);
	
	public O visit(ExistentialCompositionViaChain inf, I input);
	
	public O visit(InconsistentDisjointness inf, I input);
	
	public O visit(NegationContradiction inf, I input);
	
	public O visit(ReflexiveExistentialComposition inf, I input);
}
