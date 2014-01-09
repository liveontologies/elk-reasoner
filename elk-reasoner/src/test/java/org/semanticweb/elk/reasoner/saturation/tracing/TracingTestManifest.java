/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.net.URL;

import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.testing.VoidTestOutput;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingTestManifest extends ReasoningTestManifest<VoidTestOutput, VoidTestOutput> {

	public TracingTestManifest(URL input) {
		super(input, null);
	}

}
