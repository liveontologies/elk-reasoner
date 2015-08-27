package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectHasSelfVisitor;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * Represents occurrences of an {@link ElkObjectHasSelf} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface IndexedObjectHasSelf extends IndexedClassExpression {

	/**
	 * @return The representation of the {@link ElkObjectProperty} that is a
	 *         property of the {@link ElkObjectHasSelf} represented by this
	 *         {@link IndexedObjectHasSelf}.
	 * 
	 * @see ElkObjectHasSelf#getProperty()
	 */
	public IndexedObjectProperty getProperty();

	public <O> O accept(IndexedObjectHasSelfVisitor<O> visitor);

	class Helper {

		public static void produceDecomposedExistentialLink(
				ConclusionProducer producer, IndexedContextRoot root,
				IndexedObjectHasSelf subsumer) {
			SaturatedPropertyChain propertySaturation = subsumer.getProperty()
					.getSaturated();
			if (propertySaturation.getCompositionsByLeftSubProperty().isEmpty()) {
				producer.produce(new DecomposedReflexiveBackwardLink(root,
						subsumer));
			} else {
				producer.produce(new DecomposedReflexiveForwardLink(root,
						subsumer));
			}
		}

	}

}
