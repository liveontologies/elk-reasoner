package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * A sequence of elements that can be accessed one after another and also can be
 * searched, removed and added using an instance of {@link Matcher} for
 * identifying elements. Essentially, this is just a link list with some
 * enhancements.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the types of the elements in the sequence
 * @see Matcher
 */
public interface Chain<T extends Reference<T>> extends Reference<T> {

	public <S extends T> S find(Matcher<T, S> matcher);

	public <S extends T> S getCreate(Matcher<T, S> matcher);

	public <S extends T> S remove(Matcher<T, S> matcher);

}
