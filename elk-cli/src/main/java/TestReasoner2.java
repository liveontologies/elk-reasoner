/*
 * #%L
 * ELK Command Line Interface
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.semanticweb.elk.cli.IOReasoner;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedNominal;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.reasoner.saturation.classes.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.saturation.classes.SaturatedClassExpression;
import org.semanticweb.elk.reasoner.saturation.markers.ConnectedComponent;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Pair;

public class TestReasoner2 {
	
	static ElkObjectFactory factory = new ElkObjectFactoryImpl();
	static ExecutorService executor = Executors.newCachedThreadPool();

	public static void main(String[] args) throws Exception {
		
		IOReasoner reasoner = new IOReasoner(executor, 16);
		
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/snomed.owl");
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/FMA_lite.owl");
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/el-galen.owl");
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/galen7_simplified.owl");
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/galen8_simplified.owl");
//		reasoner.loadOntologyFromFile("e:/krr/tests/test.owl");
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/ELO/OBI2.owl");
//		reasoner.loadOntologyFromFile("e:/krr/ontologies/ELO/FMA_cons.owl");
		reasoner.loadOntologyFromFile("e:/krr/ontologies/ELO/EL-GALEN-n1.owl");
		
		
		OntologyIndex ont = reasoner.getOntologyIndex();
		
		// property saturation
		final ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, 16, ont);
		objectPropertySaturation.compute();

//		Set<IndexedClass> nominals = lowerCaseClasses(ont);
//		Set<IndexedClass> nominals = readFromFile(ont, "e:/krr/ontologies/qualifiers.txt");
//		Set<IndexedClass> nominals = primitiveSubClasses(ont, "http://www.co-ode.org/ontologies/galen#"+"SymbolicValueType");
//		Set<IndexedClass> nominals = primitiveSubClasses(ont, "galen7.owl#"+"State");
//		Set<IndexedClass> nominals = toldSubClasses(ont, "http://www.opengalen.org/owl/opengalen.owl#"+"State");
//		Set<IndexedClass> nominals = random(ont, 50);

//		filterToldPrimitive(ont, nominals);
//		filterFullPrimitive(ont, nominals);
		
//		toNominals(reasoner, nominals, logFile);

		// first phase
		Calendar cal = Calendar.getInstance();
		long time10 = cal.getTimeInMillis();
		
		ClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new ClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, ont);
		classExpressionSaturation.start();
		
		for (IndexedClass ic : ont.getIndexedClasses())
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(ic, null));
		
		classExpressionSaturation.waitCompletion();
		
		cal = Calendar.getInstance();
		long time11 = cal.getTimeInMillis();
		RuleApplicationEngine.write();
		System.err.println("Time: " + (time11-time10));
		
		
		int possibleSubsumptions = 0;
		List<SaturatedClassExpression> secondPhase = new LinkedList<SaturatedClassExpression> ();
		for (IndexedClass ic : ont.getIndexedClasses()) {
			int k = ic.getSaturated().setSaturated();
			possibleSubsumptions += k;
			if (k > 0) {
				secondPhase.add(ic.getSaturated());
			}
		}
		
		System.err.println("Second phase: " + secondPhase.size());
		System.err.println("Possible subsumptions: " + possibleSubsumptions);

		// second phase
		RuleApplicationEngine.setSecondPhase();
		
		cal = Calendar.getInstance();
		long time20 = cal.getTimeInMillis();
		classExpressionSaturation = new ClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, ont);
		classExpressionSaturation.start();
		
		for (SaturatedClassExpression context : secondPhase) {
				classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(context.getRoot(), context));
			}
		classExpressionSaturation.waitCompletion();
		
		cal = Calendar.getInstance();
		long time21 = cal.getTimeInMillis();
		RuleApplicationEngine.write();
		System.err.println("Time: " + (time21-time20));
		reasoner.shutdown();
	}
	
	static IndexedClass getIndexedClass(OntologyIndex ontology, String name) throws Exception {
		IndexedClass root = (IndexedClass) ontology.getIndexed(factory.getClass(new ElkFullIri(name)));
		if (root == null)
			throw new Exception("IndexedClass not found.");
		return root;
	}
	
	static Set<IndexedClass> toldSubClasses(OntologyIndex ontology, Set<IndexedClass> roots) {
		Set<IndexedClass> classes = new HashSet<IndexedClass>();
		for (IndexedClass ic : ontology.getIndexedClasses()) {
			Set<IndexedClassExpression> sup = new ArrayHashSet<IndexedClassExpression>();
			ArrayDeque<IndexedClassExpression> queue = new ArrayDeque<IndexedClassExpression>();
			sup.add(ic);
			queue.addLast(ic);
			while (!queue.isEmpty()) {
				IndexedClassExpression x = queue.removeLast();
				if (roots.contains(x)) {
					classes.add(ic);
					break;
				}
				if (x.getToldSuperClassExpressions() != null)
					for (IndexedClassExpression y : x.getToldSuperClassExpressions())
						if (sup.add(y))
							queue.addLast(y);
			}
		}
		return classes;
		
	}
	
	static Set<IndexedClass> toldSubClasses(OntologyIndex ontology, String name) throws Exception {
		return toldSubClasses(ontology, Collections.singleton(getIndexedClass(ontology, name)));
	}
	
	static Set<IndexedClass> random(OntologyIndex ontology, int n) {
		int m = ontology.getIndexedClassCount() / n;
		if (m == 0)
			return Collections.emptySet();
		
		Set<IndexedClass> classes = new HashSet<IndexedClass>();
		Random random = new Random();
		for (IndexedClass ic : ontology.getIndexedClasses())
			if (random.nextInt(m) == 0)
				classes.add(ic);
		return classes;
	}
	
	static boolean isLowerCase(IndexedClass ic) {
		ElkClass ec = ic.getElkClass();
		ElkIri iri = ec.getIri();
		int i = iri.asString().indexOf('#');
		return Character.isLowerCase(iri.asString().charAt(i+1));
	}
	
	static Set<IndexedClass> lowerCaseClasses(OntologyIndex ontology) {
		Set<IndexedClass> classes = new HashSet<IndexedClass> ();
		for (IndexedClass ic : ontology.getIndexedClasses()) {
			if (isLowerCase(ic))
				classes.add(ic);
		}
		return classes;
	}
	
	static Set<IndexedClass> readFromFile(OntologyIndex ontology, String file) throws Exception {
		BufferedReader buff = new BufferedReader(new FileReader(file));
		Set<IndexedClass> classes = new HashSet<IndexedClass>();

		String line;
		while ((line = buff.readLine()) != null) {
			classes.add(getIndexedClass(ontology, line));
		}

		return classes;
	}
	
	static void filterToldPrimitive(OntologyIndex ontology, Set<IndexedClass> classes) {
		for (IndexedClassExpression ice : ontology.getIndexedClassExpressions())
			if (ice.getToldSuperClassExpressions() != null)
			for (IndexedClassExpression sce : ice.getToldSuperClassExpressions())
				classes.remove(sce);
	}
	
	static Set<IndexedClass> primitiveSubClasses(OntologyIndex ontology, String name) throws Exception {
		IndexedClass root = getIndexedClass(ontology, name);
		
		ClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new ClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
			executor, 16, ontology);
		classExpressionSaturation.start();

		for (IndexedClass ic : ontology.getIndexedClasses())
			classExpressionSaturation
			.submit(new SaturationJob<IndexedClassExpression>(ic, null));

		classExpressionSaturation.waitCompletion();
		
		Set<IndexedClass> result = new HashSet<IndexedClass>();
		for (IndexedClass ic : ontology.getIndexedClasses()) {
			if (ic.getSaturated().getSuperClasses().contains(root))
				result.add(ic);
		}
		for (IndexedClass ic : ontology.getIndexedClasses())
			for (IndexedClass sup : ic.getSaturated().getSuperClasses())
				if (sup != ic)
					result.remove(sup);
		RuleApplicationEngine.reset();
		return result;
	}
	
	static void filterFullPrimitive(OntologyIndex ontology, Set<IndexedClass> classes) throws Exception {
		ClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new ClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, ontology);
		classExpressionSaturation.start();

		for (IndexedClass ic : ontology.getIndexedClasses())
			classExpressionSaturation
			.submit(new SaturationJob<IndexedClassExpression>(ic, null));

		classExpressionSaturation.waitCompletion();
		
		for (IndexedClass ic : ontology.getIndexedClasses())
			for (IndexedClass sup : ic.getSaturated().getSuperClasses())
				if (sup != ic)
					classes.remove(sup);
		RuleApplicationEngine.reset();
	}
	
	static void toNominals(Reasoner reasoner, Collection<IndexedClass> classes, PrintWriter logFile) {
		List<ElkAxiom> axioms = new LinkedList<ElkAxiom> ();
//		logFile.println("Nominals:");
		for (IndexedClass ic : classes) {
			ElkClass ec = ic.getElkClass();
			ElkIri iri = ec.getIri();
			ElkNamedIndividual ind = factory.getNamedIndividual(iri);
			axioms.add(factory.getEquivalentClassesAxiom(ec, 
					factory.getObjectOneOf(ind)));
			logFile.println(iri.asString());
		}
		for (ElkAxiom axiom : axioms)
			reasoner.addAxiom(axiom);
	}
	
	static void graphToFile(OntologyIndex ontology, String file) throws Exception {
		Map<SaturatedClassExpression, Integer> id = new HashMap<SaturatedClassExpression, Integer> ();
		int n = 0;
		List<Integer> nominals = new LinkedList<Integer> ();
		List<Integer> classes = new LinkedList<Integer> ();
		
		for (IndexedClassExpression ice : ontology.getIndexedClassExpressions()) {
			SaturatedClassExpression context = ice.getSaturated();
			if (context != null) {
				id.put(context, n);
				if (ice instanceof IndexedNominal)
					nominals.add(n);
				if (ice instanceof IndexedClass)
					classes.add(n);
				n++;
			}
		}

		List<Pair<Integer, Integer>> edges = new LinkedList<Pair<Integer, Integer>> ();
		for (Map.Entry<SaturatedClassExpression, Integer> i : id.entrySet()) {
			SaturatedClassExpression context = i.getKey();
			for (IndexedObjectSomeValuesFrom e : context.getSuperObjectSomeValuesFroms())
				edges.add(new Pair<Integer, Integer> (i.getValue(), id.get(e.getFiller().getSaturated())));
		}
		
		PrintWriter out = new PrintWriter(new FileWriter(file));
		
		out.println("vertices: " + n);
		out.println();
		
		out.println("nominals: " + nominals.size());
		for (Integer i : nominals)
			out.println(i);
		out.println();
		
		out.println("classes: " + classes.size());
		for (Integer i : classes)
			out.println(i);
		out.println();
		
		out.println("edges: " + edges.size());
		for(Pair<Integer, Integer> p : edges) {
			out.println(p.getFirst() + " " + p.getSecond());
		}
		out.close();
		
	}
	
	static List<ConnectedComponent> computeComponents(List<SaturatedClassExpression> nodes) {
		
		Map<SaturatedClassExpression, Set<SaturatedClassExpression>> reachable = 
			new HashMap<SaturatedClassExpression, Set<SaturatedClassExpression>>();
		
		for (SaturatedClassExpression x : nodes) 
			reachable.put(x, findReachable(x));

		List<ConnectedComponent> result = new LinkedList<ConnectedComponent> ();
		Set<SaturatedClassExpression> done = new ArrayHashSet<SaturatedClassExpression>();
		for (SaturatedClassExpression x : nodes)
			if (!done.contains(x)) {
				Set<SaturatedClassExpression> comp = new ArrayHashSet<SaturatedClassExpression>();
				for (SaturatedClassExpression y : nodes) 
					if (!done.contains(y) && reachable.get(x).contains(y) && reachable.get(y).contains(x)) {
						comp.add(y);
						done.add(y);
					}
				result.add(new ConnectedComponent(comp));
			}
		return result;
	}
	
	static Set<SaturatedClassExpression> findReachable(SaturatedClassExpression root) {
		Set<SaturatedClassExpression> result = new ArrayHashSet<SaturatedClassExpression> ();
		ArrayDeque<SaturatedClassExpression> queue = new ArrayDeque<SaturatedClassExpression>();
		result.add(root);
		queue.addLast(root);
		while (!queue.isEmpty()) {
			SaturatedClassExpression x = queue.removeLast();
			for (IndexedObjectSomeValuesFrom e  : x.getSuperObjectSomeValuesFroms()) {
				SaturatedClassExpression y = e.getFiller().getSaturated();
				if (result.add(y))
					queue.addLast(y);
			}
		}
		return result;
	}
}