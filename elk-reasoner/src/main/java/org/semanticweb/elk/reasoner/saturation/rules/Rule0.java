/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;

/**
 * A rule that doesn't have a premise and can be applied to elements of a particular type modifiable by a
 * {@link BasicSaturationStateWriter}.
 * 
 * @author Pavel Klinov
 * 
 * @param <E>
 *            the type of elements to which the rule can be applied
 */
public interface Rule0<E> {

	/**
	 * Applying the rule to an element modifiable by
	 * {@link BasicSaturationStateWriter}
	 * 
	 * @param writer
	 *            a {@link BasicSaturationStateWriter} which could change the
	 *            element as a result of this rule's application
	 * @param element
	 *            the element to which the rule is applied
	 */
	public void apply(BasicSaturationStateWriter writer, E element);

	/**
	 * @return the name of this rule
	 */
	public String getName();

}
