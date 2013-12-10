/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InferenceVisitor;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleContextTraceStore implements ContextTracer {
	
	private final Multimap<IndexedClassExpression, Inference> subsumerInferenceMap_;
	
	private final Map<IndexedPropertyChain, Multimap<Context, Inference>> backwardLinkInferenceMap_;
	
	private final ConclusionVisitor<Void, InferenceVisitor<?>> inferenceReader_ = new BaseConclusionVisitor<Void, InferenceVisitor<?>>() {

		private void visitAll(Iterable<Inference> inferences, InferenceVisitor<?> visitor) {
			for (Inference inf : inferences) {
				inf.accept(visitor);
			}
		}
		
		@Override
		public Void visit(NegativeSubsumer negSCE, InferenceVisitor<?> visitor) {
			visitAll(getSubsumerInferences(negSCE.getExpression()), visitor);
			
			return null;
		}

		@Override
		public Void visit(PositiveSubsumer posSCE, InferenceVisitor<?> visitor) {
			visitAll(getSubsumerInferences(posSCE.getExpression()), visitor);
			
			return null;
		}

		@Override
		public Void visit(BackwardLink link, InferenceVisitor<?> visitor) {
			visitAll(getBackwardLinkInferences(link.getRelation(), link.getSource()), visitor);
			
			return null;
		}
		
	}; 
	
	private final ConclusionVisitor<Boolean, Inference> inferenceWriter_ = new BaseConclusionVisitor<Boolean, Inference>() {

		@Override
		public Boolean visit(NegativeSubsumer negSCE, Inference inf) {
			return addSubsumerInference(negSCE.getExpression(), inf);
		}

		@Override
		public Boolean visit(PositiveSubsumer posSCE, Inference inf) {
			return addSubsumerInference(posSCE.getExpression(), inf);
		}

		@Override
		public Boolean visit(BackwardLink link, Inference inf) {
			return addBackwardLinkInference(link.getRelation(), link.getSource(), inf);
		}
		
	};
	
	/**
	 * 
	 */
	public SimpleContextTraceStore() {
		subsumerInferenceMap_ = new HashSetMultimap<IndexedClassExpression, Inference>();
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,Inference>>();
	}
	

	protected Boolean addBackwardLinkInference(IndexedPropertyChain relation,
			Context source, Inference inf) {
		Multimap<Context, Inference> infMap = backwardLinkInferenceMap_.get(relation);
		
		if (infMap == null) {
			infMap = new HashSetMultimap<Context, Inference>();
			
			infMap.add(source, inf);
			backwardLinkInferenceMap_.put(relation, infMap);
			
			return true;
		}
		
		return infMap.add(source, inf);
	}


	protected Boolean addSubsumerInference(IndexedClassExpression ice, Inference inf) {
		return subsumerInferenceMap_.add(ice, inf);
	}


	@Override
	public void accept(Conclusion conclusion, InferenceVisitor<?> visitor) {
		conclusion.accept(inferenceReader_, visitor);
	}

	public Iterable<Inference> getSubsumerInferences(IndexedClassExpression conclusion) {
		return subsumerInferenceMap_.get(conclusion);
	}

	public Iterable<Inference> getBackwardLinkInferences(
			IndexedPropertyChain linkRelation, Context linkSource) {
		Multimap<Context, Inference> infMap = backwardLinkInferenceMap_.get(linkRelation);
		
		return infMap == null ? null : infMap.get(linkSource);
	}

	@Override
	public boolean addInference(Conclusion conclusion, Inference inference) {
		return conclusion.accept(inferenceWriter_, inference);
	}

}
