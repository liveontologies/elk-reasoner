package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatingCachedIndexedObjectFactory extends
		DelegatingCachedIndexedObjectFactory {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(UpdatingCachedIndexedObjectFactory.class);

	private final ModifiableOntologyIndex index_;

	private final int increment_, positiveIncrement_, negativeIncrement_;

	public UpdatingCachedIndexedObjectFactory(
			CachedIndexedObjectFactory baseFactory,
			ModifiableOntologyIndex index, int increment,
			int positiveIncerement, int negativeIncrement) {
		super(baseFactory);
		this.index_ = index;
		this.increment_ = increment;
		this.positiveIncrement_ = positiveIncerement;
		this.negativeIncrement_ = negativeIncrement;
	}

	@Override
	<T extends CachedIndexedObject<T>> T filter(T input) {
		T result = index_.resolve(input);
		if (result == null) {
			result = input;
		}
		if (!result.occurs()) {
			index_.add(result);
		}
		result.updateOccurrenceNumbers(index_, increment_, positiveIncrement_,
				negativeIncrement_);
		if (!result.occurs()) {
			index_.remove(result);
		}
		return result;
	}

}
