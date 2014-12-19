package org.semanticweb.elk.reasoner.indexing.conversion;

import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;

public interface ElkEntityConverter extends
		ElkEntityVisitor<ModifiableIndexedEntity> {

	// combined interface
}
