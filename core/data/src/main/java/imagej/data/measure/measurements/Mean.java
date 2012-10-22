package imagej.data.measure.measurements;

import imagej.data.measure.Measurement;


public class Mean implements Measurement {
	private Moment1AboutZero moment1;

	public Mean(Moment1AboutZero moment1) {
		this.moment1 = moment1;
	}

	@Override
	public double getValue() {
		return moment1.getValue();
	}
	
}

