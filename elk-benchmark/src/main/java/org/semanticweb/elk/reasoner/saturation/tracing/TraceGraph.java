/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps each conclusion to a collection of {@link Inference}s which produce
 * other conclusions. Basically this object is a reversed {@link TraceStore}.
 * 
 * TODO serialize this
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TraceGraph implements Serializable {

	protected static final Logger LOGGER_ = LoggerFactory	.getLogger(TraceGraph.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5601103129005386693L;
	
	private final Multimap<Conclusion, Inference> inferenceMap_;
	
	private final List<Conclusion> initializationInferences_;

	/**
	 * 
	 */
	public TraceGraph(TraceStore.Reader traceReader) {
		initializationInferences_ = new LinkedList<Conclusion>();
		inferenceMap_ = new HashListMultimap<Conclusion, Inference>();

		buildGraph(traceReader);
	}
	
	public Collection<Conclusion> getInitializationInferences() {
		return initializationInferences_;
	}

	private void buildGraph(final TraceStore.Reader traceReader) {
		for (final Context context : traceReader.getContexts()) {
			
			LOGGER_.trace("Adding inferences for context {}", context);
			
			traceReader.visitConclusions(context,
					new BaseConclusionVisitor<Void, Void>() {

						@Override
						protected Void defaultVisit(Conclusion conclusion,
								Void cxt) {

							addInference(context, conclusion, traceReader);

							return null;
						}

					});
		}
	}

	void addInference(final Context context, Conclusion conclusion,
			TraceStore.Reader traceReader) {
				
		traceReader.accept(context, conclusion,
				new TracedConclusionVisitor<Void, Void>() {

					@Override
					public Void visit(InitializationSubsumer traced, Void _null) {
						initializationInferences_.add(traced);
						return null;
					}

					@Override
					public Void visit(SubClassOfSubsumer traced, Void parameter) {
						
						LOGGER_.trace("Unary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(traced.getPremise(),
								new UnaryInference(context, traced));
						return null;
					}

					@Override
					public Void visit(ComposedConjunction traced, Void parameter) {
						
						LOGGER_.trace("Binary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(
								traced.getFirstConjunct(),
								new BinaryInference(context, traced, traced
										.getSecondConjunct()));
						inferenceMap_.add(
								traced.getSecondConjunct(),
								new BinaryInference(context, traced, traced
										.getFirstConjunct()));
						return null;
					}

					@Override
					public Void visit(DecomposedConjunction traced,
							Void parameter) {
						
						LOGGER_.trace("Unary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(traced.getConjunction(),
								new UnaryInference(context, traced));
						return null;
					}

					@Override
					public Void visit(PropagatedSubsumer traced, Void parameter) {
						
						LOGGER_.trace("Binary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(
								traced.getPropagation(),
								new BinaryInference(context, traced, traced
										.getBackwardLink()));
						inferenceMap_.add(
								traced.getBackwardLink(),
								new BinaryInference(context, traced, traced
										.getPropagation()));
						return null;
					}

					@Override
					public Void visit(ReflexiveSubsumer traced, Void parameter) {
						// TODO
						return null;
					}

					@Override
					public Void visit(ComposedBackwardLink traced,
							Void parameter) {
						
						LOGGER_.trace("Binary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(
								traced.getBackwardLink(),
								new BinaryInference(context, traced, traced
										.getForwardLink()));
						inferenceMap_.add(
								traced.getForwardLink(),
								new BinaryInference(context, traced, traced
										.getBackwardLink()));
						return null;
					}

					@Override
					public Void visit(ReversedBackwardLink traced,
							Void parameter) {
						
						LOGGER_.trace("Unary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(traced.getSourceLink(),
								new UnaryInference(context, traced));
						return null;
					}

					@Override
					public Void visit(DecomposedExistential traced,
							Void parameter) {
						
						LOGGER_.trace("Unary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(traced.getExistential(),
								new UnaryInference(context, traced));
						return null;
					}

					@Override
					public Void visit(TracedPropagation traced, Void parameter) {
						
						LOGGER_.trace("Unary inference added: {} in {}", traced, context);
						
						inferenceMap_.add(traced.getPremise(),
								new UnaryInference(context, traced));
						return null;
					}

				});

	}

}
