package org.semanticweb.elk.reasoner.saturation.markers;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.reasoner.saturation.classes.SaturatedClassExpression;

public class ConnectedComponent implements Marker {
	protected final Set<SaturatedClassExpression> members;

	public ConnectedComponent(Set<SaturatedClassExpression> members) {
		this.members = Collections.unmodifiableSet(members);
	}

	public Set<SaturatedClassExpression> getMembers() {
		return members;
	}
}
