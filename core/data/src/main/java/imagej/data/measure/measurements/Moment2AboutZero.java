package imagej.data.measure.measurements;

import imagej.data.measure.SamplingMeasurement;


public class Moment2AboutZero implements SamplingMeasurement {
	private ElementCount numElems;
	private double sum;
	private boolean calculated = false;

	public Moment2AboutZero(ElementCount numElems) {
		this.numElems = numElems;
	}
	
	@Override
	public void preprocess(long[] origin) {
		sum = 0;
	}
	
	@Override
	public void dataValue(long[] position, double value) {
		sum += value*value;
	}
	
	@Override
	public void postprocess() {
		calculated = true;
	}
	
	@Override
	public double getValue() {
		if (!calculated) return Double.NaN;
		return sum / numElems.getValue();
	}
	
}

