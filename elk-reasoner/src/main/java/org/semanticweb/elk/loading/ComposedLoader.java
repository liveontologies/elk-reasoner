package org.semanticweb.elk.loading;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
 * A {@link Loader} that consists of two given {@link Loader}s, which are used
 * one after another when {@link #load()} is called
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ComposedLoader implements Loader {

	private final Loader firstLoader_, secondLoader_;

	public ComposedLoader(Loader firstLoader, Loader secondLoader) {
		this.firstLoader_ = firstLoader;
		this.secondLoader_ = secondLoader;
	}

	@Override
	public void load() throws ElkLoadingException {
		firstLoader_.load();
		secondLoader_.load();

	}

	@Override
	public void dispose() {
		firstLoader_.dispose();
		secondLoader_.dispose();
	}

}
