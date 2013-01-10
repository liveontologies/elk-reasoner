/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class CheckCleaningStage extends BasePostProcessingStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(CheckCleaningStage.class);

	private final AbstractReasonerState reasoner_;

	public CheckCleaningStage(AbstractReasonerState reasoner) {
		reasoner_ = reasoner;
	}

	@Override
	public String getName() {
		return "Checking that unsaturated contexts are clean";
	}

	@Override
	public void execute() throws ElkException {
		Set<Context> cleanedContexts = new ArrayHashSet<Context>(1024);
		// checking subsumers of cleaned contexts
		for (IndexedClassExpression ice : reasoner_.saturationState
				.getNotSaturatedContexts()) {
			Context context = ice.getContext();
			if (context == null) {
				LOGGER_.error("Context removed for " + ice);
				continue;
			}
			cleanedContexts.add(context);
			if (ice.getContext().getSubsumers().size() > 0) {
				LOGGER_.error("Context not cleaned: " + ice.toString() + "\n"
						+ ice.getContext().getSubsumers().size()
						+ " subsumers: " + ice.getContext().getSubsumers());
			}
		}
		// checking backward links
		for (IndexedClassExpression ice : reasoner_
				.getIndexedClassExpressions()) {
			Context context = ice.getContext();
			if (context == null)
				continue;
			Multimap<IndexedPropertyChain, Context> backwardLinks = context
					.getBackwardLinksByObjectProperty();
			for (IndexedPropertyChain ipc : backwardLinks.keySet()) {
				for (Context target : backwardLinks.get(ipc))
					if (cleanedContexts.contains(target))
						LOGGER_.error("Backward link in " + context
								+ " via property " + ipc
								+ " to cleaned context " + target);
			}
		}
	}

}
