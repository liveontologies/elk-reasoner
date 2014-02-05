package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SaturationState} backed by a map from {@link IndexedClassExpression}
 * s to {@link Context}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class MapSaturationState extends AbstractSaturationState {

	// logger for events
	//private static final Logger LOGGER_ = LoggerFactory
	//		.getLogger(MapSaturationState.class);

	private final ConcurrentHashMap<IndexedClassExpression, Context> contextAssignment_;

	public MapSaturationState(OntologyIndex index, int expectedSize) {
		super(index);
		this.contextAssignment_ = new ConcurrentHashMap<IndexedClassExpression, Context>(
				expectedSize);
	}

	public MapSaturationState(OntologyIndex index) {
		super(index);
		this.contextAssignment_ = new ConcurrentHashMap<IndexedClassExpression, Context>(
				index.getIndexedClassExpressions().size());
	}

	@Override
	public Collection<Context> getContexts() {
		return contextAssignment_.values();
	}

	@Override
	public Context getContext(IndexedClassExpression ice) {
		return contextAssignment_.get(ice);
	}

	@Override
	void resetContexts() {
		contextAssignment_.clear();
	}

	@Override
	Context setIfAbsent(Context context) {
		return contextAssignment_.putIfAbsent(context.getRoot(), context);
	}

}
