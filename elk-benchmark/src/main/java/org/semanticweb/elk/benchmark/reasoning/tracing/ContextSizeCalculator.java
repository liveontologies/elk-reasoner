/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning.tracing;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.MutableReference;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.tracing.BaseInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Reader;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ContextSizeCalculator {
	
	public static final String PARTITION_NUMBER = "partitions";
	public static final String MAX_PARTITION_SIZE = "max-partition-size";
	public static final String AVG_PARTITION_SIZE = "avg-partition-size";
	public static final String INFERENCE_NUMBER = "inferences";

	public static void calculate(TraceStore traceStore, Metrics metrics) {
		final Map<IndexedClassExpression, Integer> countMap = new HashMap<IndexedClassExpression, Integer>();
		final MutableInteger totalNumberOfInferences = new MutableInteger();
		final MutableReference<IndexedClassExpression> largestPartition = new MutableReference<IndexedClassExpression>();
		final MutableInteger largestPartitionSize = new MutableInteger();
		Reader reader = traceStore.getReader();
		
		for (final IndexedClassExpression partition : reader.getContextRoots()) {
			reader.visitConclusions(partition, new BaseConclusionVisitor<Void, Void>() {

				@Override
				protected Void defaultVisit(Conclusion conclusion, Void cxt) {
					IndexedClassExpression conclusionPartition = conclusion.getSourceContext(partition.getContext()).getRoot();
					
					Integer counter = countMap.get(conclusionPartition);
					
					if (counter == null) {
						counter = Integer.valueOf(1);
					}
					else {
						counter++;
					}
					
					if (counter > largestPartitionSize.get()) {
						largestPartitionSize.set(counter);
						largestPartition.set(conclusionPartition);
					}
					
					countMap.put(conclusionPartition, counter);
					totalNumberOfInferences.increment();
					
					return null;
				}

				/*@Override
				public Void visit(ForwardLink link, Void context) {
					//ignore this
					return null;
				}*/

				@Override
				public Void visit(Propagation propagation, Void context) {
					//ignore this
					return null;
				}
				
				
				
			});
		}
		
		/*for (final IndexedClassExpression partition : reader.getContextRoots()) {
			reader.visitInferences(partition, new BaseInferenceVisitor<Void, Void>() {

				@Override
				protected Void defaultTracedVisit(Inference inference, Void parameter) {
					IndexedClassExpression inferencePartition = inference.getSourceContext(partition.getContext()).getRoot();
					
					Integer counter = countMap.get(inferencePartition);
					
					if (counter == null) {
						counter = Integer.valueOf(1);
					}
					else {
						counter++;
					}
					
					if (counter > largestPartitionSize.get()) {
						largestPartitionSize.set(counter);
						largestPartition.set(inferencePartition);
					}
					
					countMap.put(inferencePartition, counter);
					totalNumberOfInferences.increment();
					
					return null;
				}
				
			});
		}*/
		
		metrics.updateLongMetric(PARTITION_NUMBER, countMap.keySet().size());
		metrics.updateLongMetric(MAX_PARTITION_SIZE, largestPartitionSize.get());
		metrics.updateDoubleMetric(AVG_PARTITION_SIZE, totalNumberOfInferences.get() * 1.0 / countMap.keySet().size());
		metrics.updateLongMetric(INFERENCE_NUMBER, totalNumberOfInferences.get());
		
	}
}
