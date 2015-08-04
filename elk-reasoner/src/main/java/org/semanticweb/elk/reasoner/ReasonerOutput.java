package org.semanticweb.elk.reasoner;

/**
 * A class for objects that represent outputs reasoner computations.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <O>
 *            the type of the output
 */
public class ReasonerOutput<O> {

	private O output = null;

	/**
	 * @return the output stored in this object or {@code null} if no output is
	 *         stored yet
	 */
	public O getOutput() {
		return this.output;
	}

	/**
	 * Set the output to the given value. After that this value is returned by
	 * {@link #getOutput()}
	 * 
	 * @param output
	 *            the output to be set
	 */
	protected void setOutput(O output) {
		this.output = output;
	}

}
