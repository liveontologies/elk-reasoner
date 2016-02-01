package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;

/**
 * Represents occurrences of an {@link ElkObjectHasSelf} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface IndexedObjectHasSelf extends IndexedClassExpression {

	/**
	 * @return The representation of the {@link ElkObjectProperty} that is a
	 *         property of the {@link ElkObjectHasSelf} represented by this
	 *         {@link IndexedObjectHasSelf}.
	 * 
	 * @see ElkObjectHasSelf#getProperty()
	 */
	IndexedObjectProperty getProperty();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(IndexedObjectHasSelf element);
		
	}	

	class Helper {

		public static void produceDecomposedExistentialLink(
				ClassInferenceProducer producer, IndexedContextRoot root,
				IndexedObjectHasSelf subsumer) {
			SaturatedPropertyChain propertySaturation = subsumer.getProperty()
					.getSaturated();
			if (propertySaturation.getCompositionsByLeftSubProperty().isEmpty()) {
				producer.produce(new BackwardLinkOfObjectHasSelf(root,
						subsumer));
			} else {
				producer.produce(new ForwardLinkOfObjectHasSelf(root,
						subsumer));
			}
		}

	}

}
