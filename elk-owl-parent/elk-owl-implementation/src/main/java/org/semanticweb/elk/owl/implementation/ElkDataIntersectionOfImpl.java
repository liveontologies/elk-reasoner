/*
 * #%L
 * elk-reasoner
 * 
 * $Id: ElkDataIntersectionOf.java 68 2011-06-04 21:49:01Z mak@aifb.uni-karlsruhe.de $
 * $HeadURL: https://elk-reasoner.googlecode.com/svn/trunk/elk-reasoner/src/main/java/org/semanticweb/elk/syntax/ElkDataIntersectionOf.java $
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.owl.implementation;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkDataIntersectionOf;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.visitors.ElkDataIntersectionOfVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * ELK implementation of ElkDataIntersectionOf.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ElkDataIntersectionOfImpl extends ElkDataRangeListObject implements
		ElkDataIntersectionOf {

	ElkDataIntersectionOfImpl(List<? extends ElkDataRange> dataRanges) {
		super(dataRanges);
	}

	@Override
	public <O> O accept(ElkDataRangeVisitor<O> visitor) {
		return accept((ElkDataIntersectionOfVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkDataIntersectionOfVisitor<O>) visitor);
	}

	@Override
	public <O> O accept(ElkDataIntersectionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
