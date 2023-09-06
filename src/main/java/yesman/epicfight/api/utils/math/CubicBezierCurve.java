package yesman.epicfight.api.utils.math;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.phys.Vec3;

public class CubicBezierCurve {
	
	/**
	 * Get more informations about cubic bezier curve here:
	 * https://towardsdatascience.com/b%C3%A9zier-interpolation-8033e9a262c2
	 */
	
	private static List<Double> MATRIX_CONSTANTS = Lists.newArrayList();
	
	static {
		MATRIX_CONSTANTS.add(0.5D);
	}
	
	private static void getBezierEquationCoefficients(List<Double> points, List<Double> aList, List<Double> bList) {
		List<Double> results = Lists.newArrayList();
		int size = points.size();
		
		results.add(points.get(0) + points.get(1) * 2);
		
		for (int idx = 1; idx < size - 2; idx++) {
			results.add(points.get(idx) * 4 + points.get(idx + 1) * 2);
		}
		
		results.add(points.get(size - 2) * 8 + points.get(size - 1));
		
		int storedConstsSize = MATRIX_CONSTANTS.size();
		int coordSize = results.size();
		
		if (storedConstsSize < coordSize - 1) {
			for (int i = 0; i < (coordSize - 1) - storedConstsSize; i++) {
				double lastConst = MATRIX_CONSTANTS.get(storedConstsSize - 1);
				MATRIX_CONSTANTS.add(1.0D / (4.0D - lastConst));
			}
		}
		
		List<Double> convertedResults = Lists.newArrayList();
		
		for (int idx = 0; idx < coordSize; idx++) {
			if (idx == 0) {
				convertedResults.add(results.get(idx) * 0.5D);
			} else if (idx == coordSize - 1) {
				convertedResults.add((results.get(idx) - 2 * convertedResults.get(idx - 1)) * (1.0D / (7.0D - MATRIX_CONSTANTS.get(idx - 1) * 2.0D)));
			} else {
				convertedResults.add((results.get(idx) - convertedResults.get(idx - 1)) * MATRIX_CONSTANTS.get(idx));
			}
		}
		
		for (int idx = coordSize - 1; idx >= 0; idx--) {
			if (idx == coordSize - 1) {
				aList.add(0, convertedResults.get(idx));
			} else {
				aList.add(0, convertedResults.get(idx) - convertedResults.get(idx + 1) * MATRIX_CONSTANTS.get(idx));
			}
		}
		
		for (int i = 0; i < coordSize; i++) {
			if (i == coordSize - 1) {
				bList.add((aList.get(i) + points.get(i + 1)) * 0.5D);
			} else {
				bList.add(2 * points.get(i + 1) - aList.get(i + 1));
			}
		}
	}
	
	private static double cubicBezier(double start, double end, double a, double b, double t) {
		return Math.pow((1 - t), 3) * start + 3 * t * Math.pow((1 - t), 2) * a + 3 * t * t * (1 - t) * b + t * t * t * end; 
	}
	
	public static List<Vec3> getBezierInterpolatedPoints(List<Vec3> points, int interpolatedResults) {
		return getBezierInterpolatedPoints(points, 0, points.size() - 1, interpolatedResults);
	}
	
	/**
	 * Requires at least 3 points
	 * @param points : control points of the bezier curve
	 * @param sliceBegin : first control point to calculate the interpolation
	 * @param sliceEnd : last control point to calculate the interpolation
	 * @param interpolatedResults : the number of interpolated vertices between control points
	 * @return
	 */
	public static List<Vec3> getBezierInterpolatedPoints(List<Vec3> points, int sliceBegin, int sliceEnd, int interpolatedResults) {
		if (points.size() < 3) {
			return null;
		}
		
		sliceBegin = Math.max(sliceBegin, 0);
		sliceEnd = Math.min(sliceEnd, points.size() - 1);
		
		int size = points.size();
		List<Vec3> interpolatedPoints = Lists.newArrayList();
		List<Double> x = Lists.newArrayList();
		List<Double> y = Lists.newArrayList();
		List<Double> z = Lists.newArrayList();
		
		for (int idx = 0; idx < size; idx++) {
			x.add(points.get(idx).x);
			y.add(points.get(idx).y);
			z.add(points.get(idx).z);
		}
		
		List<Double> x_a = Lists.newArrayList();
		List<Double> x_b = Lists.newArrayList();
		List<Double> y_a = Lists.newArrayList();
		List<Double> y_b = Lists.newArrayList();
		List<Double> z_a = Lists.newArrayList();
		List<Double> z_b = Lists.newArrayList();
		
		getBezierEquationCoefficients(x, x_a, x_b);
		getBezierEquationCoefficients(y, y_a, y_b);
		getBezierEquationCoefficients(z, z_a, z_b);
		
		for (int i = sliceBegin; i < sliceEnd; i++) {
			if (interpolatedPoints.size() > 0) {
				interpolatedPoints.remove(interpolatedPoints.size() - 1);
			}
			
			Vec3 start = points.get(i);
			Vec3 end = points.get(i + 1);
			double x_av = x_a.get(i);
			double x_bv = x_b.get(i);
			double y_av = y_a.get(i);
			double y_bv = y_b.get(i);
			double z_av = z_a.get(i);
			double z_bv = z_b.get(i);
			
			for (int j = 0; j < interpolatedResults + 1; j++) {
				double t = (double)j / (double)interpolatedResults;
				
				interpolatedPoints.add(new Vec3(cubicBezier(start.x, end.x, x_av, x_bv, t)
											  , cubicBezier(start.y, end.y, y_av, y_bv, t)
											  , cubicBezier(start.z, end.z, z_av, z_bv, t)));
			}
		}
		
		return interpolatedPoints;
	}
}