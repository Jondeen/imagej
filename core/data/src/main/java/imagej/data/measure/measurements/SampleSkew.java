package imagej.data.measure.measurements;

import imagej.data.measure.Measurement;


public class SampleSkew implements Measurement {

	private PopulationSkew popSkew;
	private ElementCount numElems;
	
	public SampleSkew(PopulationSkew popSkew, ElementCount numElems) {
		this.popSkew = popSkew;
		this.numElems = numElems;
	}

	@Override
	public double getValue() {
		double n = numElems.getValue();
		return popSkew.getValue() * Math.sqrt(n * (n-1)) / (n-2);
	}
	
}

