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
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
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
	
	private final Map<IndexedPropertyChain, Multimap<Context, Inference>> forwardLinkInferenceMap_;
	
	private final ConclusionVisitor<Void, InferenceVisitor<?>> inferenceReader_ = new BaseConclusionVisitor<Void, InferenceVisitor<?>>() {

		private void visitAll(Iterable<Inference> inferences, InferenceVisitor<?> visitor) {
			if (inferences != null) {
				for (Inference inf : inferences) {
					inf.accept(visitor);
				}
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
			visitAll(getLinkInferences(backwardLinkInferenceMap_, link.getRelation(), link.getSource()), visitor);
			
			return null;
		}
		
		@Override
		public Void visit(ForwardLink link, InferenceVisitor<?> visitor) {
			visitAll(getLinkInferences(forwardLinkInferenceMap_, link.getRelation(), link.getTarget()), visitor);
			
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
			return addLinkInference(backwardLinkInferenceMap_, link.getRelation(), link.getSource(), inf);
		}
		
		@Override
		public Boolean visit(ForwardLink link, Inference inf) {
			return addLinkInference(forwardLinkInferenceMap_, link.getRelation(), link.getTarget(), inf);
		}
		
	};
	
	/**
	 * 
	 */
	public SimpleContextTraceStore() {
		subsumerInferenceMap_ = new HashSetMultimap<IndexedClassExpression, Inference>();
		backwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,Inference>>();
		forwardLinkInferenceMap_ = new ArrayHashMap<IndexedPropertyChain, Multimap<Context,Inference>>();
	}
	

	protected Boolean addLinkInference(Map<IndexedPropertyChain, Multimap<Context, Inference>> linkMap, IndexedPropertyChain relation,
			Context source, Inference inf) {
		Multimap<Context, Inference> infMap = linkMap.get(relation);
		
		if (infMap == null) {
			infMap = new HashSetMultimap<Context, Inference>();
			
			infMap.add(source, inf);
			linkMap.put(relation, infMap);
			
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

	public Iterable<Inference> getLinkInferences(Map<IndexedPropertyChain, Multimap<Context, Inference>> linkMap, 
			IndexedPropertyChain linkRelation, Context linkSource) {
		Multimap<Context, Inference> infMap = linkMap.get(linkRelation);
		
		return infMap == null ? null : infMap.get(linkSource);
	}

	@Override
	public boolean addInference(Conclusion conclusion, Inference inference) {
		return conclusion.accept(inferenceWriter_, inference);
	}

}
