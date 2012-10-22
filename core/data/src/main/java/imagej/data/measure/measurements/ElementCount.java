package imagej.data.measure.measurements;

import imagej.data.measure.SamplingMeasurement;


public class ElementCount implements SamplingMeasurement {

	private boolean calculated = false;
	private long numElems;
	
	public ElementCount() {}
	
	@Override
	public void preprocess(long[] origin) {
		numElems = 0;
	}

	@Override
	public void dataValue(long[] position, double value) {
		numElems++;
	}

	@Override
	public void postprocess() {
		calculated = true;
	}

	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return numElems;
	}

}

