package org.semanticweb.elk.reasoner.stages.debug;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * Checking invariants for {@code SaturatedPropertyChain}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturatedPropertyChainCheckingStage extends
		BasePostProcessingStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(SaturatedPropertyChainCheckingStage.class);

	private final OntologyIndex index_;

	public SaturatedPropertyChainCheckingStage(final OntologyIndex index) {
		this.index_ = index;
	}

	@Override
	public String getName() {
		return "Checking Saturation for Properties";
	}

	@Override
	public void execute() throws ElkException {

		for (IndexedPropertyChain ipc : index_.getIndexedPropertyChains()) {
			testLeftCompositions(ipc);
			testRightCompositions(ipc);
		}

	}

	/**
	 * checking that all compositions of this property to the left are computed
	 * in the saturations for the respective left properties
	 * 
	 * @param ipc
	 */
	static void testLeftCompositions(IndexedPropertyChain ipc) {
		SaturatedPropertyChain saturation = ipc.getSaturated();
		Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeft = saturation
				.getCompositionsByLeftSubProperty();
		if (compositionsByLeft == null)
			return;
		for (IndexedPropertyChain left : compositionsByLeft.keySet()) {
			for (IndexedPropertyChain composition : compositionsByLeft
					.get(left)) {
				SaturatedPropertyChain leftSaturation = left.getSaturated();
				Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRight = leftSaturation
						.getCompositionsByRightSubProperty();
				if (compositionsByRight == null
						|| !compositionsByRight.contains(ipc, composition))
					LOGGER_.error("Composition " + left + " o " + ipc + " => "
							+ composition + " is computed for " + ipc
							+ " but not for " + left);
			}
		}
	}

	/**
	 * checking that all compositions of this property to the right are computed
	 * in the saturations for the respective right properties
	 * 
	 * @param ipc
	 */
	static void testRightCompositions(IndexedPropertyChain ipc) {
		SaturatedPropertyChain saturation = ipc.getSaturated();
		Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByRight = saturation
				.getCompositionsByRightSubProperty();
		if (compositionsByRight == null)
			return;
		for (IndexedPropertyChain right : compositionsByRight.keySet()) {
			for (IndexedPropertyChain composition : compositionsByRight
					.get(right)) {
				SaturatedPropertyChain rightSaturation = right.getSaturated();
				Multimap<IndexedPropertyChain, IndexedPropertyChain> compositionsByLeft = rightSaturation
						.getCompositionsByLeftSubProperty();
				if (compositionsByLeft == null
						|| !compositionsByLeft.contains(ipc, composition))
					LOGGER_.error("Composition " + ipc + " o " + right + " => "
							+ composition + " is computed for " + ipc
							+ " but not for " + right);
			}
		}

	}
}
