package org.semanticweb.elk.reasoner.incremental;

import java.util.Vector;

/**
 * A {@code Vector} whose elements can be turned "on" and "off". The status of
 * newly added elements is "on" and this status can be given the index of the
 * element. Removal and more advance operations are not supported.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 */
public class OnOffVector<T> extends Vector<T> {

	private static final long serialVersionUID = -7521093590463405223L;

	/**
	 * A {@link Vector} containing on-off values of the elements with the
	 * corresponding indices.
	 */
	private final Vector<Boolean> onOffValues_;

	public OnOffVector(int size) {
		super(size);
		this.onOffValues_ = new Vector<Boolean>(size);
	}

	@Override
	public boolean add(T element) {
		boolean result = super.add(element);
		if (result)
			onOffValues_.add(true);
		return result;
	}

	/**
	 * @param index
	 * @return {@code true} if the value of the element with the given index is
	 *         "on"
	 */
	public boolean isOn(int index) {
		return onOffValues_.get(index);
	}

	/**
	 * Changes the on-off value of the element with the given index to the
	 * complementary one.
	 * 
	 * @param index
	 * @return {@code true} if the status was "on" before the change
	 */
	public boolean flipOnOff(int index) {
		boolean result = onOffValues_.get(index);
		onOffValues_.set(index, !result);
		return result;
	}

}
