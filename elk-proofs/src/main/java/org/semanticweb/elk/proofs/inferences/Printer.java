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
import org.semanticweb.elk.proofs.inferences.properties.ChainSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.ReflexiveComposition;
import org.semanticweb.elk.proofs.inferences.properties.ReflexivityViaSubsumption;
import org.semanticweb.elk.proofs.inferences.properties.SubsumptionViaReflexivity;
import org.semanticweb.elk.proofs.inferences.properties.ToldReflexivity;

/**
 * Prints inferences
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Printer {

	public static String print(Inference inference) {
		return inference.accept(new PrintingVisitor(), null);
	}
	
	private static class PrintingVisitor implements InferenceVisitor<Void, String> {

		@Override
		public String visit(ClassInitialization inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ThingInitialization inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ClassSubsumption inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ConjunctionComposition inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ConjunctionDecomposition inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(DisjointnessContradiction inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(DisjunctionComposition inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ExistentialComposition inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ExistentialCompositionViaChain inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(InconsistentDisjointness inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(NegationContradiction inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ReflexiveExistentialComposition inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ChainSubsumption inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ReflexiveComposition inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ReflexivityViaSubsumption inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(SubsumptionViaReflexivity inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String visit(ToldReflexivity inf, Void input) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
