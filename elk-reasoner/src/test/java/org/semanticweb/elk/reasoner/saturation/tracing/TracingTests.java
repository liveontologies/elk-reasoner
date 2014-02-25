package org.semanticweb.elk.reasoner.saturation.tracing;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface TracingTests {

	public void accept(TracingTestVisitor visitor) throws Exception;
}
