/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * Interface for engine factories which trace all inferences in the given
 * context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ContextTracingFactory extends InputProcessorFactory<ContextTracingJob, InputProcessor<ContextTracingJob>> {

	public SaturationStatistics getStatistics();
}
