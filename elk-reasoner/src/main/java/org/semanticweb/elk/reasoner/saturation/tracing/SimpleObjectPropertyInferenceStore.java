/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.BottomUpPropertySubsumptionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.LeftReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.RightReflexiveSubPropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.TopDownPropertySubsumptionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.HashSetMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Stores and retrieves {@link ObjectPropertyInference}s.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleObjectPropertyInferenceStore implements ObjectPropertyInferenceStore {
	
	//private final static Logger LOGGER_ = LoggerFactory.getLogger(SimpleObjectPropertyInferenceStore.class);
	
	// TODO separate out property inferences and chain inferences?
	private final ConcurrentHashMap<IndexedPropertyChain, BasePropertyInferences> propertyToInferencesMap_ = new ConcurrentHashMap<IndexedPropertyChain, BasePropertyInferences>();

	/**
	 * records object property inferences
	 */
	private final ObjectPropertyInferenceVisitor<Void, Boolean> inferenceWriter_ = new ObjectPropertyInferenceVisitor<Void, Boolean>() {

		@Override
		public Boolean visit(PropertyChainInitialization conclusion, Void input) {
			// we aren't writing initialization inferences, will always generate them on the fly
			return false;
		}

		@Override
		public Boolean visit(ToldReflexiveProperty inference, Void input) {
			return writeReflexivityInference(inference.getPropertyChain(), inference);
		}

		@Override
		public Boolean visit(ReflexiveToldSubObjectProperty inference,
				Void input) {
			return writeReflexivityInference(inference.getPropertyChain(), inference);
		}

		@Override
		public Boolean visit(ReflexivePropertyChainInference inference,
				Void input) {
			return writeReflexivityInference(inference.getPropertyChain(), inference);
		}
		
		private BasePropertyInferences getInferences(IndexedPropertyChain chain) {
			BasePropertyInferences stored = propertyToInferencesMap_.get(chain);
			
			return stored != null ? stored : new BasePropertyInferences();
		}
		
		private void setInferences(IndexedPropertyChain chain, final BasePropertyInferences inferences) {
			propertyToInferencesMap_.put(chain, inferences);
		}
		
		private boolean writeReflexivityInference(IndexedPropertyChain chain, ObjectPropertyInference inference) {
			final BasePropertyInferences inferences = getInferences(chain);			
			
			boolean modified = inferences.reflexivityInferences.add(inference);
			
			setInferences(chain, inferences);
			
			return modified;
		}

		@Override
		public Boolean visit(LeftReflexiveSubPropertyChainInference inference, 	Void input) {
			return writeSubPropertyChainInference(inference.getSubPropertyChain(), inference.getSuperPropertyChain(), inference);
		}

		@Override
		public Boolean visit(RightReflexiveSubPropertyChainInference inference, Void input) {
			return writeSubPropertyChainInference(inference.getSubPropertyChain(), inference.getSuperPropertyChain(), inference);
		}

		@Override
		public Boolean visit(TopDownPropertySubsumptionInference inference, Void input) {
			return writeSubPropertyChainInference(inference.getSubPropertyChain(), inference.getSuperPropertyChain(), inference);
		}
		
		@Override
		public Boolean visit(BottomUpPropertySubsumptionInference inference, Void input) {
			return writeSubPropertyChainInference(inference.getSubPropertyChain(), inference.getSuperPropertyChain(), inference);
		}
		
		private Boolean writeSubPropertyChainInference(
				IndexedPropertyChain subPropertyChain,
				IndexedPropertyChain superPropertyChain,
				ObjectPropertyInference inference) {
			
			BasePropertyInferences inferences = getInferences(superPropertyChain);			
			boolean modified = inferences.subPropertyInfences.add(subPropertyChain, inference);
			
			setInferences(superPropertyChain, inferences);
			
			//LOGGER_.trace("writing sub-chain inference: {} -> {}, inference: {}, store: {}", subPropertyChain, superPropertyChain, inference, hashCode());
			
			return modified;
		}
		
	};
	
	/**
	 * retrieves object property inferences
	 */
	private final ObjectPropertyConclusionVisitor<ObjectPropertyInferenceVisitor<?,?>, Void> inferenceReader_ = new ObjectPropertyConclusionVisitor<ObjectPropertyInferenceVisitor<?,?>, Void>() {

		@Override
		public Void visit(SubPropertyChain<?, ?> conclusion, ObjectPropertyInferenceVisitor<?, ?> visitor) {
			if (conclusion.getSubPropertyChain() == conclusion.getSuperPropertyChain()) {
				visitInitializationInference(conclusion.getSubPropertyChain(), visitor);
			}
			else {
				visitSuperPropertyInferences(conclusion.getSuperPropertyChain(), conclusion.getSubPropertyChain(), visitor);
			}
			return null;
		}

		@Override
		public Void visit(ReflexivePropertyChain<?> conclusion, final ObjectPropertyInferenceVisitor<?,?> visitor) {
			visitReflexivityInferences(propertyToInferencesMap_.get(conclusion.getPropertyChain()), visitor);
			
			return null;
		}

		private void visitReflexivityInferences(BasePropertyInferences inferences, ObjectPropertyInferenceVisitor<?,?> visitor) {
			if (inferences != null) {
				for (ObjectPropertyInference inf : inferences.reflexivityInferences) {
					inf.acceptTraced(visitor, null);
				}
			}
			
		}		

		private void visitInitializationInference(	IndexedPropertyChain subChain, ObjectPropertyInferenceVisitor<?, ?> visitor) {
			// assume that this inference is always there and generate it on the fly
			new PropertyChainInitialization(subChain).acceptTraced(visitor, null);	
		}
		
		private void visitSuperPropertyInferences(IndexedPropertyChain superChain, IndexedPropertyChain subChain, ObjectPropertyInferenceVisitor<?,?> visitor) {
			BasePropertyInferences inferences = propertyToInferencesMap_.get(superChain);

			if (inferences == null) {
				//LOGGER_.trace("not found sub-chain inferences: {} -> {}, store: {}", subChain, superChain, hashCode());
				return;
			}

			for (ObjectPropertyInference inference : inferences.subPropertyInfences.get(subChain)) {
				inference.acceptTraced(visitor, null);
			}
		}
		
	};
	
	@Override
	public synchronized boolean addInference(ObjectPropertyInference inference) {
		//FIXME
		//System.err.println(inference);
		return inference.acceptTraced(inferenceWriter_, null);
	}

	@Override
	public void visitInferences(ObjectPropertyConclusion conclusion, ObjectPropertyInferenceVisitor<?, ?> visitor) {
		conclusion.accept(inferenceReader_, visitor);
	}
	
	@Override
	public void clear() {
		propertyToInferencesMap_.clear();
	}
	
	/**
	 * Interface for objects storing property or property chain inferences.
	 * 
	 * {@link ObjectPropertyInference}s can be produced multiple times (in
	 * contrast to {@link ClassInference}s) so we use sets and set multimaps to
	 * store unique inferences. We also require that implementations properly
	 * override equals() and hashCode().
	 * 
	 * @author Pavel Klinov
	 *
	 *         pavel.klinov@uni-ulm.de
	 */
	private static interface PropertyInferences {
		
	}

	private static class BasePropertyInferences implements PropertyInferences {
		
		final Set<ObjectPropertyInference> reflexivityInferences = new ArrayHashSet<ObjectPropertyInference>(1);
		final Multimap<IndexedPropertyChain, ObjectPropertyInference> subPropertyInfences = new HashSetMultimap<IndexedPropertyChain, ObjectPropertyInference>(2);
	}

}
