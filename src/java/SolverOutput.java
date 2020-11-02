import java.util.Arrays;

class SolverOutput {
    float objVal;
    float[] result;

    public SolverOutput(float objVal, float[] cols) {
        this.objVal = objVal;
        this.result = cols;
    }

    @Override
    public String toString() {
        return "ResolverResult{" +
                "objVal=" + objVal +
                ", result=" + Arrays.toString(result) +
                '}';
    }
}
