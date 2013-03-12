/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.util.logging.ElkMessage;

/**
 * Basically an adapter from {@link ElkAxiomVisitor} to
 * {@link ElkAxiomProcessor}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ChangeIndexingProcessor implements ElkAxiomProcessor {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ChangeIndexingProcessor.class);

	private final ElkAxiomIndexingVisitor indexer_;

	public ChangeIndexingProcessor(ElkAxiomIndexingVisitor indexer) {
		indexer_ = indexer;
	}

	@Override
	public void visit(ElkAxiom elkAxiom) {
		try {
			elkAxiom.accept(indexer_);
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace("indexing "
						+ OwlFunctionalStylePrinter.toString(elkAxiom)
						+ " for "
						+ (indexer_.getMultiplicity() == 1 ? "addition"
								: "deletion"));
		} catch (ElkIndexingUnsupportedException e) {
			if (LOGGER_.isEnabledFor(Level.WARN))
				LOGGER_.warn(new ElkMessage(e.getMessage()
						+ " Axiom ignored:\n"
						+ OwlFunctionalStylePrinter.toString(elkAxiom),
						"reasoner.indexing.axiomIgnored"));
		}
	}
}
