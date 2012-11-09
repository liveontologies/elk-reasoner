package org.semanticweb.elk.annotations;

/*
 * #%L
 * ELK Annotations
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

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

@SupportedAnnotationTypes("org.semanticweb.elk.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ProfiledProcessor extends AbstractProcessor {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ProfiledProcessor.class);

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (Element profiled : roundEnv
				.getElementsAnnotatedWith(Profiled.class)) {

			if (profiled.getKind() != ElementKind.CLASS)
				continue;

			TypeElement classElement = (TypeElement) profiled;

			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
					"profiled class: " + classElement.getQualifiedName(),
					classElement);

			// collect all measured methods for this annotated class
			List<ExecutableElement> methods = new LinkedList<ExecutableElement>();

			for (Element measured : classElement.getEnclosedElements()) {

				if (measured.getKind() != ElementKind.METHOD
						|| measured.getAnnotation(Measured.class) == null)
					continue;

				ExecutableElement exeElement = (ExecutableElement) measured;

				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
						"measured method: " + exeElement.getSimpleName(),
						measured);

				methods.add(exeElement);

			}

			// find the package element; this class can be the inner class, so
			// it might be not a direct enclosing element
			Element enclosing = classElement;

			for (;;) {
				enclosing = enclosing.getEnclosingElement();
				if (enclosing.getKind() == ElementKind.PACKAGE)
					break;
			}
			PackageElement packageElement = (PackageElement) enclosing;

			// creating the profiled super classes
			Properties props = new Properties();
			URL url = this.getClass().getClassLoader()
					.getResource("velocity.properties");
			try {
				props.load(url.openStream());

				VelocityEngine ve = new VelocityEngine(props);
				ve.setProperty("runtime.log.logsystem.log4j.logger", LOGGER_);
				ve.init();

				VelocityContext vc = new VelocityContext();

				vc.put("measuredAnnot", Measured.class);
				vc.put("classElement", classElement);
				vc.put("packageElement", packageElement);
				vc.put("methods", methods);

				Template vt = ve.getTemplate("profiled.vm");

				JavaFileObject jfo;
				jfo = processingEnv.getFiler().createSourceFile(
						classElement.getQualifiedName() + "Profiled");

				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
						"creating source file: " + jfo.toUri());

				Writer writer;
				writer = jfo.openWriter();

				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
						"applying velocity template: " + vt.getName());

				vt.merge(vc, writer);

				writer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		return true;

	}
}
