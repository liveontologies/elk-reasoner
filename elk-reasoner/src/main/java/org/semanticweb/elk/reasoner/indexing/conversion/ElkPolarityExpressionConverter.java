package org.semanticweb.elk.reasoner.indexing.conversion;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.predefined.ElkPolarity;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;

/**
 * Converts {@link ElkObject}s that can occur with different polarities in the
 * ontology to the corresponding {@link ModifiableIndexedObject}s. Such objects
 * can be either {@link ElkClassExpression}s, {@link ElkIndividual}s, and
 * {@link ElkObjectPropertyExpression}s. Each
 * {@link ElkPolarityExpressionConverter} is associated with one type of
 * polarity. The converter for the complementary polarity type can be also
 * returned using
 * {@link ElkPolarityExpressionConverter#getComplementaryConverter()}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ElkPolarity
 *
 */
public interface ElkPolarityExpressionConverter extends
		ElkClassExpressionVisitor<ModifiableIndexedClassExpression>,
		ElkIndividualVisitor<ModifiableIndexedIndividual>,
		ElkObjectPropertyExpressionVisitor<ModifiableIndexedObjectProperty> {

	/**
	 * @return the polarity type of this {@link ElkPolarityExpressionConverter},
	 *         i.e., the type of occurrences for which this converter should be
	 *         used
	 */
	ElkPolarity getPolarity();

	/**
	 * @return the corresponding converter for the complementary polarity.
	 * 
	 * @see ElkPolarity#getComplementary()
	 */
	ElkPolarityExpressionConverter getComplementaryConverter();

}
