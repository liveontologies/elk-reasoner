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
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InferenceFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingSaturationState implements SaturationState {

	private final SaturationState underlyingState_;
	
	private final Tracer tracer_;
	
	/**
	 * 
	 */
	public TracingSaturationState(SaturationState state) {
		underlyingState_ = state;
		tracer_ = new SimpleCentralizedTracer();
	}

	@Override
	public Collection<Context> getContexts() {
		return underlyingState_.getContexts();
	}

	@Override
	public Context getContext(IndexedClassExpression ice) {
		return underlyingState_.getContext(ice);
	}

	@Override
	public OntologyIndex getOntologyIndex() {
		return underlyingState_.getOntologyIndex();
	}

	@Override
	public Collection<IndexedClassExpression> getNotSaturatedContexts() {
		return underlyingState_.getNotSaturatedContexts();
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated) {
		
		return new ExtendedTracingWriter(underlyingState_.getExtendedWriter(
				contextCreationListener, contextModificationListener,
				ruleAppVisitor, conclusionVisitor,
				trackNewContextsAsUnsaturated), tracer_.getWriter());
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?, Context> conclusionVisitor) {
		
		return new BasicTracingWriter(underlyingState_.getWriter(contextModificationListener, conclusionVisitor), tracer_.getWriter());
	}

	@Override
	public BasicSaturationStateWriter getWriter(
			ConclusionVisitor<?, Context> conclusionVisitor) {
		
		return new BasicTracingWriter(underlyingState_.getWriter(conclusionVisitor), tracer_.getWriter());
	}

	@Override
	public ExtendedSaturationStateWriter getExtendedWriter(
			ConclusionVisitor<?, Context> conclusionVisitor) {
		
		return new ExtendedTracingWriter(underlyingState_.getExtendedWriter(conclusionVisitor), tracer_.getWriter());
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static class BasicTracingWriter implements BasicSaturationStateWriter {
		
		protected final BasicSaturationStateWriter underlyingWriter_;
		protected final TracingConclusionFactory factory_;
		private final ConclusionVisitor<Boolean, Context> traceInserter_;
		
		BasicTracingWriter(BasicSaturationStateWriter writer, Tracer.Writer traceWriter) {
			underlyingWriter_ = writer;
			factory_ = new TracingConclusionFactory(underlyingWriter_.getConclusionFactory(), new InferenceFactory());
			traceInserter_ = new TracingConclusionInsertionVisitor(traceWriter);
			
		}

		@Override
		public IndexedClassExpression getOwlThing() {
			return underlyingWriter_.getOwlThing();
		}

		@Override
		public IndexedClassExpression getOwlNothing() {
			return underlyingWriter_.getOwlNothing();
		}

		@Override
		public Context pollForActiveContext() {
			return underlyingWriter_.pollForActiveContext();
		}

		@Override
		public void produce(Context context, Conclusion conclusion) {
			underlyingWriter_.produce(context, conclusion);
		}

		@Override
		public boolean markAsNotSaturated(Context context) {
			return underlyingWriter_.markAsNotSaturated(context);
		}

		@Override
		public void clearNotSaturatedContexts() {
			underlyingWriter_.clearNotSaturatedContexts();
		}

		@Override
		public void resetContexts() {
			underlyingWriter_.resetContexts();
		}

		@Override
		public ConclusionFactory getConclusionFactory() {
			return factory_;
		}

		@Override
		public ConclusionVisitor<Boolean, Context> getConclusionInserter() {
			// return a combined visitor which first adds the conclusion
			// inference and then invokes the main inserter.
			return new CombinedConclusionVisitor<Context>(traceInserter_, underlyingWriter_.getConclusionInserter());
		}
		
	}
	
	/**
	 * 
	 */
	private static class ExtendedTracingWriter extends BasicTracingWriter implements ExtendedSaturationStateWriter {

		ExtendedTracingWriter(ExtendedSaturationStateWriter writer, Tracer.Writer traceWriter) {
			super(writer, traceWriter);
		}
		
		@Override
		public Context getCreateContext(IndexedClassExpression root) {
			return ((ExtendedSaturationStateWriter)underlyingWriter_).getCreateContext(root, factory_);
		}

		@Override
		public void initContext(Context context) {
			((ExtendedSaturationStateWriter)underlyingWriter_).initContext(context);
		}


		@Override
		public Context getCreateContext(IndexedClassExpression root, ConclusionFactory factory) {
			return getCreateContext(root);
		}

	}
}
