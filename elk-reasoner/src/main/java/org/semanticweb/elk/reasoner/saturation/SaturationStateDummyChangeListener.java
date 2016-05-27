package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
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

import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An {@link SaturationState.ChangeListener} that does nothing; can be used as a
 * prototype to implement other listeners
 * 
 * @author Yevgeny Kazakov
 *
 * @param <C>
 */
public class SaturationStateDummyChangeListener<C extends Context>
		implements SaturationState.ChangeListener<C> {

	@Override
	public void contextAddition(C context) {
		// does nothing by default
	}

	@Override
	public void contextsClear() {
		// does nothing by default
	}

	@Override
	public void contextMarkSaturated(C context) {
		// does nothing by default
	}

	@Override
	public void contextMarkNonSaturated(C context) {
		// does nothing by default
	}

}
