package org.semanticweb.elk.matching.subsumers;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

public class SubsumerMatchPrinter extends SubsumerMatchDummyVisitor<String> {

	private static SubsumerMatchPrinter INSTANCE_ = new SubsumerMatchPrinter();

	private SubsumerMatchPrinter() {

	}

	public static String toString(SubsumerMatch match) {
		return match.accept(INSTANCE_);
	}

	@Override
	protected String defaultVisit(final SubsumerElkObjectMatch match) {
		return match.getValue().toString();
	}

	@Override
	public String visit(final IndexedObjectIntersectionOfMatch match) {
		return match.getFullValue() + "[-" + match.getPrefixLength() + "]";
	}

}
