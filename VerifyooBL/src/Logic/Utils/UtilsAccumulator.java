package Logic.Utils;

public class UtilsAccumulator {
	public int N = 0;          // number of data values
	public double Sum = 0.0;   // sample variance * (n-1)
    public double Mu = 0.0;    // sample mean

    /**
     * Initializes an accumulator.
     */
    public UtilsAccumulator() {
    }

    /**
     * Adds the specified data value to the accumulator.
     * @param  x the data value
     */
    public void AddDataValue(double x) {
        N++;
        double delta = x - Mu;
        Mu  += delta / N;
        Sum += (double) (N - 1) / N * delta * delta;
    }

    /**
     * Returns the mean of the data values.
     * @return the mean of the data values
     */
    public double Mean() {
        return Mu;
    }

    /**
     * Returns the sample variance of the data values.
     * @return the sample variance of the data values
     */
    public double Var() {
        return Sum / (N - 1);
    }

    /**
     * Returns the sample standard deviation of the data values.
     * @return the sample standard deviation of the data values
     */
    public double Stddev() {
        return Math.sqrt(this.Var());
    }

    /**
     * Returns the number of data values.
     * @return the number of data values
     */
    public int Count() {
        return N;
    }
}
