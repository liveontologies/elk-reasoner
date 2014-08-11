/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Stores and retrieves {@link ObjectPropertyInference}s.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleObjectPropertyInferenceStore implements ObjectPropertyInferenceStore {
	// object property inferences and chain inferences are stored separately
	private final ConcurrentHashMap<IndexedObjectProperty, ObjectPropertyInferences> propertyToInferencesMap_ = new ConcurrentHashMap<IndexedObjectProperty, ObjectPropertyInferences>();
	
	//private final ConcurrentHashMap<IndexedBinaryPropertyChain, PropertyChainInferences> chainToInferencesMap_ = new ConcurrentHashMap<IndexedBinaryPropertyChain, PropertyChainInferences>();

	/**
	 * records object property inferences
	 */
	private final ObjectPropertyInferenceVisitor<Void, Boolean> inferenceWriter_ = new ObjectPropertyInferenceVisitor<Void, Boolean>() {

		@Override
		public Boolean visit(SubObjectPropertyInference conclusion, Void input) {
			ObjectPropertyInferences inferences = propertyToInferencesMap_.get(conclusion.getSuperProperty());
			boolean newProperty = false;
			boolean modified = false;
			
			if (inferences == null) {
				inferences = new ObjectPropertyInferences();
				newProperty = true; 
			}
			
			modified = inferences.subPropertyInfences.add(conclusion.getSubProperty(), conclusion);
			
			if (newProperty) {
				propertyToInferencesMap_.put(conclusion.getSuperProperty(), inferences);
			}
			
			return modified;
		}

		@Override
		public Boolean visit(PropertyChainInitialization conclusion, Void input) {
			// we aren't writing initialization inferences, will always generate them on the fly
			return false;
		}

	};
	
	/**
	 * retrieves object property inferences
	 */
	private final ObjectPropertyConclusionVisitor<ObjectPropertyInferenceVisitor<?,?>, Void> inferenceReader_ = new ObjectPropertyConclusionVisitor<ObjectPropertyInferenceVisitor<?,?>, Void>() {

		@Override
		public Void visit(SubObjectProperty conclusion, ObjectPropertyInferenceVisitor<?,?> visitor) {
			if (conclusion.getSubProperty() == conclusion.getSuperProperty()) {
				// there should always be an initialization inference
				visitInitializationInference(conclusion.getSubProperty(), visitor);
			} else {
				// such inferences are indexed by the super-property
				ObjectPropertyInferences inferences = propertyToInferencesMap_.get(conclusion.getSuperProperty());
				
				if (inferences == null) {
					return null;
				}
				
				for (ObjectPropertyInference inference : inferences.subPropertyInfences.get(conclusion.getSubProperty())) {
					inference.acceptTraced(visitor, null);
				}
			}
			
			return null;
		}

		@Override
		public Void visit(ReflexivePropertyChain conclusion, ObjectPropertyInferenceVisitor<?,?> visitor) {
			// TODO 
			throw new UnsupportedOperationException("NYI");
		}

		@Override
		public Void visit(SubPropertyChain conclusion,
				ObjectPropertyInferenceVisitor<?, ?> visitor) {
			visitInitializationInference(conclusion.getPropertyChain(), visitor);
			
			return null;
		}

		private void visitInitializationInference(	IndexedPropertyChain subChain, ObjectPropertyInferenceVisitor<?, ?> visitor) {
			// assume that this inference is always there and generate it on the fly
			new PropertyChainInitialization(subChain).acceptTraced(visitor, null);	
		}
		
	};
	
	@Override
	public synchronized boolean addInference(ObjectPropertyInference inference) {
		return inference.acceptTraced(inferenceWriter_, null);
	}

	@Override
	public void visitInferences(ObjectPropertyConclusion conclusion, ObjectPropertyInferenceVisitor<?, ?> visitor) {
		conclusion.accept(inferenceReader_, visitor);
	}
	
	/**
	 * Interface for objects storing property or property chain inferences
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static interface PropertyInferences {
		
	}

	private static class BasePropertyInferences implements PropertyInferences {
		// TODO 
		//final List<PropertyInferences> reflexivityInferences = null;
		//PropertyChainInitialization initialization;
	}
	
	private static class ObjectPropertyInferences  extends BasePropertyInferences {
		
		final Multimap<IndexedPropertyChain, ObjectPropertyInference> subPropertyInfences = new HashListMultimap<IndexedPropertyChain, ObjectPropertyInference>(2);
		
	}
	
	/*private static class PropertyChainInferences extends BasePropertyInferences {
		
	}*/
}
