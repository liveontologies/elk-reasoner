package org.semanticweb.elk.android;
/*
 * #%L
 * ELK Android App
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

import java.io.File;

import org.apache.log4j.Level;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Log4jHelper {

	public static void configure(Level level) {
		// set up log4j to be usable in android
		LogConfigurator logConfigurator = new LogConfigurator();
		logConfigurator.setFileName(Environment.getExternalStorageDirectory()
				+ File.separator + "MyApp" + File.separator + "logs"
				+ File.separator + "log4j.txt");
		logConfigurator.setRootLevel(level);
		logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);
		logConfigurator.configure();
	}

}
