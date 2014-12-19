package org.semanticweb.elk.reasoner.indexing.conversion;

import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;

public interface ElkPolarityExpressionConverter extends
		ElkClassExpressionVisitor<ModifiableIndexedClassExpression>,
		ElkIndividualVisitor<ModifiableIndexedIndividual>,
		ElkObjectPropertyExpressionVisitor<ModifiableIndexedObjectProperty> {

	ElkPolarityExpressionConverter getComplementaryConverter();

}
