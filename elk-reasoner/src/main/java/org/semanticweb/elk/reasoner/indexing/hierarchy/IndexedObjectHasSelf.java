package org.semanticweb.elk.reasoner.indexing.hierarchy;

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
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectHasSelfVisitor;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

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
	public IndexedObjectProperty getProperty();

	public <O> O accept(IndexedObjectHasSelfVisitor<O> visitor);

	class Helper {

		public static void produceDecomposedExistentialLink(
				ConclusionProducer producer, IndexedContextRoot root,
				IndexedObjectHasSelf subsumer) {
			SaturatedPropertyChain propertySaturation = subsumer.getProperty()
					.getSaturated();
			if (propertySaturation.getCompositionsByLeftSubProperty().isEmpty()) {
				producer.produce(new DecomposedReflexiveBackwardLink(root,
						subsumer));
			} else {
				producer.produce(new DecomposedReflexiveForwardLink(root,
						subsumer));
			}
		}

	}

}
