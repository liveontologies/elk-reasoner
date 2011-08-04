package org.semanticweb.elk.reasoner.saturation;

/**
 * @author Frantisek Simancik
 *
 */
public interface Queueable {
	<O> O accept(QueueableVisitor<O> visitor);
}
