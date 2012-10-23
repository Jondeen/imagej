/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.data.measure;


import net.imglib2.img.Img;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.real.RealAlphaTrimmedMeanFunction;
import net.imglib2.ops.function.real.RealContraharmonicMeanFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealMedianFunction;
import net.imglib2.ops.function.real.RealSumOfSquaredDeviationsFunction;
import net.imglib2.ops.function.real.RealWeightedAverageFunction;
import net.imglib2.ops.function.real.RealWeightedSumFunction;
import net.imglib2.ops.measure.Measurement;
import net.imglib2.ops.measure.NewMeasurementSet;
import net.imglib2.ops.measure.measurements.GeometricMean;
import net.imglib2.ops.measure.measurements.HarmonicMean;
import net.imglib2.ops.measure.measurements.Maximum;
import net.imglib2.ops.measure.measurements.Mean;
import net.imglib2.ops.measure.measurements.Midpoint;
import net.imglib2.ops.measure.measurements.Minimum;
import net.imglib2.ops.measure.measurements.PopulationKurtosis;
import net.imglib2.ops.measure.measurements.PopulationKurtosisExcess;
import net.imglib2.ops.measure.measurements.PopulationSkew;
import net.imglib2.ops.measure.measurements.PopulationStdDev;
import net.imglib2.ops.measure.measurements.PopulationVariance;
import net.imglib2.ops.measure.measurements.Product;
import net.imglib2.ops.measure.measurements.SampleKurtosis;
import net.imglib2.ops.measure.measurements.SampleKurtosisExcess;
import net.imglib2.ops.measure.measurements.SampleSkew;
import net.imglib2.ops.measure.measurements.SampleStdDev;
import net.imglib2.ops.measure.measurements.SampleVariance;
import net.imglib2.ops.measure.measurements.Sum;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import imagej.data.Dataset;
import imagej.plugin.Plugin;
import imagej.service.AbstractService;
import imagej.service.Service;

// TODO - make MeasurementService smarter. Compute values without always
// revisiting the pixels. This current impl goes over pixels once for each
// measurement. I will mock something up soon. BDZ

// TODO - this service is limited to only the kinds of stats it knows. It can't
// measure a user provided statistic. This can be done by calling the
// MeasurementService more directly.
// Also this service calcs one measure at a time. Our future MeasurementService
// should be able to batch them together. It also might be nice to be able to
// specify to this service gather min,var,stdev,median in an array of output
// doubles.

// TODO - see what IJ1 calculates and provide such methods here and in OPS

// NOTE - this could be done down in OPS with less trouble maybe

/**
 * A service for computing some basic statistics upon regions of
 * {@link Dataset}s.
 * 
 * @author Barry DeZonia
 *
 */
@Plugin(type=Service.class)
public class StatisticsService extends AbstractService {

	// -- Parameters --
	
	// later
	//@Parameter
	//private MeasurementService mSrv;
	
	// -- StatisticsService methods --
	
	/**
	 * Returns an estimate of the trimmed mean of the values within a
	 * {@link PointSet} region of a {@link Dataset}.
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @param halfTrimSize
	 * The number of values to trim from each end of the set of samples collected
	 * @return
	 * The measured value
	 */
	public double alphaTrimmedMean(Dataset ds, PointSet region, int halfTrimSize)
	{
		Function<long[],DoubleType> imgFunc = imgFunc(ds);
		Function<PointSet,DoubleType> func =
				new RealAlphaTrimmedMeanFunction<DoubleType>(imgFunc, halfTrimSize);
		return measure(func, region);
	}

	/**
	 * Returns an estimate of the trimmed mean of the values of a {@link Dataset}.
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param halfTrimSize
	 * The number of values to trim from each end of the set of samples collected
	 * @return
	 * The measured value
	 */
	public double alphaTrimmedMean(Dataset ds, int halfTrimSize)
	{
		return alphaTrimmedMean(ds, allOf(ds), halfTrimSize);
	}

	/**
	 * Returns an estimate of the arithmetic mean of the values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double arithmeticMean(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, Mean.class);
	}
	
	/**
	 * Returns an estimate of the arithmetic mean of the values of a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double arithmeticMean(Dataset ds) {
		return arithmeticMean(ds, allOf(ds));
	}
	
	/**
	 * Returns an estimate of the contraharmonic mean of the values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @param order
	 * The desired order of the contraharmonic mean
	 * @return
	 * The measured value
	 */
	public double contraharmomicMean(Dataset ds, PointSet region, double order) {
		Function<long[],DoubleType> imgFunc = imgFunc(ds);
		Function<PointSet,DoubleType> func =
				new RealContraharmonicMeanFunction<DoubleType>(imgFunc, order);
		return measure(func, region);
	}

	/**
	 * Returns an estimate of the contraharmonic mean of the values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param order
	 * The desired order of the contraharmonic mean
	 * @return
	 * The measured value
	 */
	public double contraharmomicMean(Dataset ds, double order) {
		return contraharmomicMean(ds, allOf(ds), order);
	}

	/**
	 * Returns an estimate of the geometric mean of the values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double geometricMean(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, GeometricMean.class);
	}
	
	/**
	 * Returns an estimate of the geometric mean of the values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double geometricMean(Dataset ds) {
		return geometricMean(ds, allOf(ds));
	}
	
	/**
	 * Returns an estimate of the harmonic mean of the values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double harmonicMean(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, HarmonicMean.class);
	}

	/**
	 * Returns an estimate of the harmonic mean of the values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double harmonicMean(Dataset ds) {
		return harmonicMean(ds, allOf(ds));
	}

	/**
	 * Returns the maximum value from the set of values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double maximum(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, Maximum.class);
	}
	
	/**
	 * Returns the maximum value from the set of values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double maximum(Dataset ds) {
		return maximum(ds, allOf(ds));
	}
	
	/**
	 * Returns the median value from the set of values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double median(Dataset ds, PointSet region) {
		Function<long[],DoubleType> imgFunc = imgFunc(ds);
		Function<PointSet,DoubleType> func =
				new RealMedianFunction<DoubleType>(imgFunc);
		return measure(func, region);
	}

	/**
	 * Returns the median value from the set of values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double median(Dataset ds) {
		return median(ds, allOf(ds));
	}

	/**
	 * Returns the point midway between the minimum value and the maximum value
	 * from the set of values within a {@link PointSet} region of a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double midpoint(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, Midpoint.class);
	}
	
	/**
	 * Returns the point midway between the minimum value and the maximum value
	 * from the set of values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double midpoint(Dataset ds) {
		return midpoint(ds, allOf(ds));
	}
	
	/**
	 * Returns the minimum value from the set of values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double minimum(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, Minimum.class);
	}
	
	/**
	 * Returns the minimum value from the set of values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double minimum(Dataset ds) {
		return minimum(ds, allOf(ds));
	}
	

	/**
	 * Returns the (biased) kurtosis of all the values within a {@link PointSet}
	 * region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double populationKurtosis(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, PopulationKurtosis.class);
	}

	/**
	 * Returns the (biased) kurtosis of all the values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double populationKurtosis(Dataset ds) {
		return populationKurtosis(ds, allOf(ds));
	}

	/**
	 * Returns the (biased) kurtosis excess of all the values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double populationKurtosisExcess(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, PopulationKurtosisExcess.class);
	}

	/**
	 * Returns the (biased) kurtosis excess of all the values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double populationKurtosisExcess(Dataset ds) {
		return populationKurtosisExcess(ds, allOf(ds));
	}

	/**
	 * Returns the (biased) skew of all the values within a {@link PointSet}
	 * region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double populationSkew(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, PopulationSkew.class);
	}

	/**
	 * Returns the (biased) skew of all the values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double populationSkew(Dataset ds) {
		return populationSkew(ds, allOf(ds));
	}

	/**
	 * Returns the (biased) estimate of the sample standard deviation of the
	 * values within a {@link PointSet} region of a {@link Dataset}. It equals
	 * the square root of the (biased) estimate of the sample variance.
	 * 
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double populationStdDev(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, PopulationStdDev.class);
	}
	
	/**
	 * Returns the (biased) estimate of the sample standard deviation of the
	 * values within a {@link Dataset}. It equals
	 * the square root of the (biased) estimate of the sample variance.
	 * 
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double populationStdDev(Dataset ds) {
		return populationStdDev(ds, allOf(ds));
	}
	
	/**
	 * Returns the (biased) estimate of the sample variance of the values within
	 * a {@link PointSet} region of a {@link Dataset}. It divides the sum of
	 * squared deviations from the mean by N (N == number of samples).
	 * 
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double populationVariance(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, PopulationVariance.class);
	}
	
	/**
	 * Returns the (biased) estimate of the sample variance of the values within
	 * a {@link Dataset}. It divides the sum of
	 * squared deviations from the mean by N (N == number of samples).
	 * 
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double populationVariance(Dataset ds) {
		return populationVariance(ds, allOf(ds));
	}
	
	/**
	 * Returns the product of all the values within a {@link PointSet} region
	 * of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double product(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, Product.class);
	}
	
	/**
	 * Returns the product of all the values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double product(Dataset ds) {
		return product(ds, allOf(ds));
	}
	
	/**
	 * Returns the (unbiased) kurtosis of all the values within a {@link PointSet}
	 * region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sampleKurtosis(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, SampleKurtosis.class);
	}

	/**
	 * Returns the (unbiased) kurtosis of all the values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sampleKurtosis(Dataset ds) {
		return sampleKurtosis(ds, allOf(ds));
	}

	/**
	 * Returns the (unbiased) kurtosis excess of all the values within a
	 * {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sampleKurtosisExcess(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, SampleKurtosisExcess.class);
	}

	/**
	 * Returns the (unbiased) kurtosis excess of all the values within a
	 * {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sampleKurtosisExcess(Dataset ds) {
		return sampleKurtosisExcess(ds, allOf(ds));
	}

	/**
	 * Returns the (unbiased) skew of all the values within a {@link PointSet}
	 * region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sampleSkew(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, SampleSkew.class);
	}

	/**
	 * Returns the (unbiased) skew of all the values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sampleSkew(Dataset ds) {
		return sampleSkew(ds, allOf(ds));
	}

	/**
	 * Returns the (unbiased) estimate of the sample standard deviation of the
	 * values within a {@link PointSet} region of a {@link Dataset}. It equals
	 * the square root of the (unbiased) estimate of the sample variance.
	 * 
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sampleStdDev(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, SampleStdDev.class);
	}

	/**
	 * Returns the (unbiased) estimate of the sample standard deviation of the
	 * values within a {@link Dataset}. It equals
	 * the square root of the (unbiased) estimate of the sample variance.
	 * 
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sampleStdDev(Dataset ds) {
		return sampleStdDev(ds, allOf(ds));
	}

	/**
	 * Returns the (unbiased) estimate of the sample variance of the values within
	 * a {@link PointSet} region of a {@link Dataset}. It divides the sum of
	 * squared deviations from the mean by N-1 (N == number of samples).
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sampleVariance(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, SampleVariance.class);
	}

	/**
	 * Returns the (unbiased) estimate of the sample variance of the values within
	 * a {@link Dataset}. It divides the sum of
	 * squared deviations from the mean by N-1 (N == number of samples).
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sampleVariance(Dataset ds) {
		return sampleVariance(ds, allOf(ds));
	}

	/**
	 * Returns the sum of all the values within a {@link PointSet} region
	 * of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sum(Dataset ds, PointSet region) {
		return measure(imgFunc(ds), region, Sum.class);
	}
	
	/**
	 * Returns the sum of all the values within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sum(Dataset ds) {
		return sum(ds, allOf(ds));
	}
	
	/**
	 * Returns the sum of squared deviations from the mean for a set of values
	 * within a {@link PointSet} region of a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @return
	 * The measured value
	 */
	public double sumOfSquaredDeviations(Dataset ds, PointSet region) {
		Function<long[],DoubleType> imgFunc = imgFunc(ds);
		Function<PointSet,DoubleType> func =
				new RealSumOfSquaredDeviationsFunction<DoubleType>(imgFunc);
		return measure(func, region);
	}
	
	/**
	 * Returns the sum of squared deviations from the mean for a set of values
	 * within a {@link Dataset}
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @return
	 * The measured value
	 */
	public double sumOfSquaredDeviations(Dataset ds) {
		return sumOfSquaredDeviations(ds, allOf(ds));
	}
	
	/**
	 * Returns the weighted average of the values within a {@link PointSet}
	 * region of a {@link Dataset}. The weights must be provided and there must
	 * be the same number of weights as there are points in the region.
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @param weights
	 * The weights to apply to each value in the region
	 * @return
	 * The measured value
	 */
	public double weightedAverage(Dataset ds, PointSet region, double[] weights) {
		Function<long[],DoubleType> imgFunc = imgFunc(ds);
		Function<PointSet,DoubleType> func =
				new RealWeightedAverageFunction<DoubleType>(imgFunc,weights);
		return measure(func, region);
	}
	
	/**
	 * Returns the weighted sum of the values within a {@link PointSet}
	 * region of a {@link Dataset}. The weights must be provided and there must
	 * be the same number of weights as there are points in the region.
	 *  
	 * @param ds
	 * The Dataset to measure
	 * @param region
	 * The PointSet region upon which to calculate
	 * @param weights
	 * The weights to apply to each value in the region
	 * @return
	 * The measured value
	 */
	public double weightedSum(Dataset ds, PointSet region, double[] weights) {
		Function<long[],DoubleType> imgFunc = imgFunc(ds);
		Function<PointSet,DoubleType> func =
				new RealWeightedSumFunction<DoubleType>(imgFunc,weights);
		return measure(func, region);
	}

	/**
	 * A convenience function for defining a {@link PointSet} that encompasses
	 * all the points within a {@link Dataset}.
	 * 
	 * @param ds
	 * The Dataset of interest
	 * @return
	 * A PointSet that includes all points within the Dataset
	 */
	public PointSet allOf(Dataset ds) {
		return new HyperVolumePointSet(ds.getDims());
	}
	
	// -- private helpers --
	
	@SuppressWarnings({"unchecked","rawtypes"})
	private RealImageFunction<?,DoubleType> imgFunc(Dataset ds) {
		Img<? extends RealType<?>> imgPlus = ds.getImgPlus();
		return new RealImageFunction(imgPlus, new DoubleType());
	}
	
	private double measure(Function<PointSet,DoubleType> func, PointSet region) {
		DoubleType output = new DoubleType();
		func.compute(region, output);
		return output.getRealDouble();
	}
	
	private double measure(Function<long[],DoubleType> func, PointSet region,
		Class<? extends Measurement> measureClass)
	{
		NewMeasurementSet set = new NewMeasurementSet();
		set.addMeasure("tmp", measureClass);
		set.doMeasurements(func, region);
		return set.getValue("tmp");
	}
}
