package org.semanticweb.elk.reasoner.saturation;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.CountingClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A {@link SaturationStateWriter} that mirrors all operations of the provided
 * internal {@link SaturationStateWriter} and additionally counts the number of
 * produced {@link ClassConclusion}s using a provided {@link ClassConclusionCounter}
 * 
 * @see ClassConclusionProducer#produce(ClassConclusion)
 * 
 * @author "Yevgeny Kazakov"
 */
public class CountingSaturationStateWriter<C extends Context> extends
		SaturationStateWriterWrap<C> {

	private final ClassConclusion.Visitor<Boolean> countingVisitor_;

	public CountingSaturationStateWriter(SaturationStateWriter<C> writer,
			ClassConclusionCounter counter) {
		super(writer);
		this.countingVisitor_ = new CountingClassConclusionVisitor(counter);
	}

	@Override
	public void produce(ClassConclusion conclusion) {
		mainWriter.produce(conclusion);
		conclusion.accept(countingVisitor_);
	}

}