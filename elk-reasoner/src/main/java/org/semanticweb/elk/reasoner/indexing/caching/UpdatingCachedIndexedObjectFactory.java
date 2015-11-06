package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingException;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.OccurrenceIncrement;

/**
 * A {@link CachedIndexedObjectFactory} that constructs objects using another
 * {@link CachedIndexedObjectFactory} and updates the occurrence counts for the
 * constructed objects using the provided {@link OccurrenceIncrement}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ModifiableIndexedObject#updateOccurrenceNumbers
 */
public class UpdatingCachedIndexedObjectFactory extends
		DelegatingCachedIndexedObjectFactory {

	private final OccurrenceIncrement increment_;

	private final ModifiableOntologyIndex index_;

	public UpdatingCachedIndexedObjectFactory(
			CachedIndexedObjectFactory baseFactory,
			ModifiableOntologyIndex index, OccurrenceIncrement increment) {
		super(baseFactory);
		this.index_ = index;
		this.increment_ = increment;
	}

	@Override
	<T extends CachedIndexedSubObject<T>> T filter(T input) {
		T result = resolve(input);
		update(result);
		if (!result.occurs()) {
			index_.remove(result);
		}
		return result;
	}

	<T extends CachedIndexedObject<T>> T resolve(T input) {
		T result = index_.resolve(input);
		if (result == null) {
			result = input;
		}
		if (!result.occurs()) {
			index_.add(result);
		}
		return result;
	}

	<T extends ModifiableIndexedSubObject> T update(T input) {
		if (!input.updateOccurrenceNumbers(index_, increment_))
			throw new ElkIndexingException(input.toString()
					+ ": cannot update in Index for " + increment_
					+ " occurrences!");
		return input;
	}

	<T extends ModifiableIndexedAxiom> T update(T input) {
		if (increment_.totalIncrement > 0) {
			for (int i = 0; i < increment_.totalIncrement; i++) {
				if (!input.addOccurrence(index_))
					throw new ElkIndexingException(input.toString()
							+ ": cannot be added to Index!");
			}
		}
		if (increment_.totalIncrement < 0) {
			for (int i = 0; i < -increment_.totalIncrement; i++) {
				if (!input.removeOccurrence(index_))
					throw new ElkIndexingException(input.toString()
							+ ": cannot be removed from Index!");
			}
		}
		return input;
	}

}
