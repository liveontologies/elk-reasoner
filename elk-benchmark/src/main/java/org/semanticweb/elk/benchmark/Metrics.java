/**
 * 
 */
package org.semanticweb.elk.benchmark;

import java.util.Map;

import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Metrics {

	private int runCount = 0;
	private final Map<String, String> metricMap_ = new ArrayHashMap<String, String>();
	
	public void incrementRunCount() {
		runCount++;
	}
	
	public int getRunsNumber() {
		return runCount;
	}

	public Map<String, String> getMetricsMap() {
		return metricMap_;
	}
	
	public int updateIntegerMetric(String metric, int delta) {
		String value = metricMap_.get(metric);
		int intValue = value == null ? 0 : Integer.valueOf(value);
		
		intValue += delta;
		metricMap_.put(metric, String.valueOf(intValue));
		
		return intValue;
	}
	
	public double updateDoubleMetric(String metric, double delta) {
		String value = metricMap_.get(metric);
		double doubleValue = value == null ? 0 : Double.valueOf(value);
		
		doubleValue += delta;
		metricMap_.put(metric, String.valueOf(doubleValue));
		
		return doubleValue;
	}
	
	public void reset() {
		runCount = 0;
		metricMap_.clear();
	}
	
	public String getMetric(String name) {
		return metricMap_.get(name);
	}
	
	public void setMetric(String name, String value) {
		metricMap_.put(name, value);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		for (Map.Entry<String, String> entry : metricMap_.entrySet()) {
			buffer.append(entry.getKey()).append(" : ").append(entry.getValue()).append('\n');
		}
		
		return buffer.toString();
	}
	
	
}
