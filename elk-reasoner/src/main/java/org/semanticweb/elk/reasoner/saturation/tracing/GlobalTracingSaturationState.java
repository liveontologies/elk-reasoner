/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.DelegatingBasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.DelegatingExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;

/**
 * An implementation of {@code ExtendedSaturationState} used for full tracing of
 * all conclusions in the entire saturation state.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class GlobalTracingSaturationState implements ExtendedSaturationState {

	private final ExtendedSaturationState mainSaturationState_;
	
	private final ConclusionFactory conclusionFactory_ = new TracingConclusionFactory();
	
	private final TraceStore.Writer traceWriter_;
	
	public GlobalTracingSaturationState(ExtendedSaturationState state, TraceStore.Writer traceWriter) {
		mainSaturationState_ = state;
		traceWriter_ = traceWriter;
	}
	
	@Override
	public Collection<Context> getContexts() {
		return mainSaturationState_.getContexts();
	}

	@Override
	public Context getContext(IndexedClassExpression ice) {
		return mainSaturationState_.getContext(ice);
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return mainSaturationState_.getOntologyIndex();
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ConclusionVisitor<?, Context> conclusionVisitor) {
		return new BasicWriter(mainSaturationState_.getWriter(conclusionVisitor));
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ConclusionVisitor<?, Context> conclusionVisitor,
			CompositionRuleApplicationVisitor initRuleAppVisitor) {
		return new ExtendedWriter(mainSaturationState_.getExtendedWriter(conclusionVisitor, initRuleAppVisitor));
	}

	@Override
	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return mainSaturationState_.getNotSaturatedContexts();
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated) {
		return new ExtendedWriter(mainSaturationState_.getExtendedWriter(
				contextCreationListener, contextModificationListener,
				ruleAppVisitor, conclusionVisitor, conclusionFactory_,
				trackNewContextsAsUnsaturated));
	}
	
	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			ConclusionFactory conclusionFactory,
			boolean trackNewContextsAsUnsaturated) {
		return new ExtendedWriter(mainSaturationState_.getExtendedWriter(
				contextCreationListener, contextModificationListener,
				ruleAppVisitor, conclusionVisitor, conclusionFactory_,
				trackNewContextsAsUnsaturated));
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?, Context> conclusionVisitor) {
		return new BasicWriter(mainSaturationState_.getWriter(contextModificationListener, conclusionVisitor));
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class BasicWriter extends DelegatingBasicSaturationStateWriter {

		public BasicWriter(BasicSaturationStateWriter writer) {
			super(writer);
		}

		@Override
		public ConclusionFactory getConclusionFactory() {
			return conclusionFactory_;
		}

		@Override
		public ConclusionVisitor<Boolean, Context> getConclusionInserter() {
			return new CombinedConclusionVisitor<Context>(new TracingConclusionInsertionVisitor(traceWriter_), super.getConclusionInserter());
		}
		
	}
	
	/**
	 * Uses a special conclusion inserter which first writes inferences and then
	 * inserts conclusions into contexts.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ExtendedWriter extends DelegatingExtendedSaturationStateWriter {

		public ExtendedWriter(ExtendedSaturationStateWriter writer) {
			super(writer);
		}
		
		@Override
		public ConclusionFactory getConclusionFactory() {
			return conclusionFactory_;
		}

		@Override
		public ConclusionVisitor<Boolean, Context> getConclusionInserter() {
			return new CombinedConclusionVisitor<Context>(new TracingConclusionInsertionVisitor(traceWriter_), super.getConclusionInserter());
		}
		
	}
}
