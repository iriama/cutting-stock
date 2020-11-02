import org.gnu.glpk.*;
import sun.plugin2.util.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class CuttingStock {

    public static Problem problem;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: java -jar cutting_stock.jar [path to .txt problem instance]");
            System.exit(1);
        }

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                System.loadLibrary("glpk_4_65_java");
            } catch (UnsatisfiedLinkError e) {
                System.err.println(e);
                System.err.println("Dependency error : can't load GLPK lib, run file 'INSTALL_GLPK_JAVA.bat' to solve.");
                System.exit(1);
            }
        } else {
            try {
                System.loadLibrary("glpk_java");
            } catch (UnsatisfiedLinkError e) {
                System.err.println(e);
                System.err.println("Dependency error : can't load GLPK lib, run command 'sudo apt install libglpk-java' to solve.");
                System.exit(1);
            }
        }

        problem = Parser.parse(args[0]);

        cuttingStock();

    }

    private static SolverOutput solveSimplex(float[][] patterns, boolean dual) {

        System.out.println("-- resolveSimplex " + (dual ? "(dual)" : ""));

        float[] result = new float[dual ? problem.orderCount() : patterns.length];
        Arrays.fill(result, 0);

        glp_prob lp = GLPK.glp_create_prob();
        GLPK.glp_add_cols(lp, patterns.length);
        GLPK.glp_add_rows(lp, problem.orderCount());

        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);

        for (int i = 0; i < problem.orderCount(); i++) {
            Order order = problem.getOrder(i);
            GLPK.glp_set_row_bnds(lp, i + 1, GLPKConstants.GLP_FX, order.getCount(), order.getCount()); // = requested
        }

        for (int i = 0; i < patterns.length; i++) {
            GLPK.glp_set_col_bnds(lp, i + 1, GLPKConstants.GLP_LO, 0, 0); // >= 0
            GLPK.glp_set_obj_coef(lp, i + 1, 1); // all orders equal priority
        }

        int matSize = problem.orderCount() * patterns.length + 1;
        SWIGTYPE_p_int ia = GLPK.new_intArray(matSize);
        SWIGTYPE_p_int ja = GLPK.new_intArray(matSize);
        SWIGTYPE_p_double ar = GLPK.new_doubleArray(matSize);
        int ne = 0;

        for (int i = 0; i < patterns.length; i++) {
            for (int j = 0; j < problem.orderCount(); j++) {
                ne++;
                GLPK.intArray_setitem(ia, ne, j + 1); // a[1, 1] a[1, 2] a[1, 3]... a[2, 1] a[2, 2]...
                GLPK.intArray_setitem(ja, ne, i + 1);
                GLPK.doubleArray_setitem(ar, ne, patterns[i][j]);
            }
        }

        for (int i = 0; i < matSize; i++) {
            System.out.println("  ia[" + i + "]=" + GLPK.intArray_getitem(ia, i) + " ; ja[" + i + "]=" + GLPK.intArray_getitem(ja, i) + " ; ar[" + i + "]=" + GLPK.doubleArray_getitem(ar, i));
        }

        GLPK.glp_load_matrix(lp, ne, ia, ja, ar);
        GLPK.glp_simplex(lp, null);
        float objVal = (float) GLPK.glp_get_obj_val(lp);

        for (int i = 0; i < result.length; i++) {
            result[i] = dual ? (float) GLPK.glp_get_row_dual(lp, i + 1) : (float) GLPK.glp_get_col_prim(lp, i + 1);
            System.out.println("  (" + i + ") = " + result[i]);
        }

        // cleanup
        GLPK.delete_intArray(ia);
        GLPK.delete_intArray(ja);
        GLPK.delete_doubleArray(ar);
        GLPK.glp_delete_prob(lp);
        GLPK.glp_free_env();

        return new SolverOutput(objVal, result);
    }

    public static void cuttingStock() {

        long startTime = System.currentTimeMillis();

        float[][] patterns = new float[problem.orderCount()][problem.orderCount()];

        // intial pattern (cut as much of an order size as you can)
        for (int i = 0; i < problem.orderCount(); i++) {
            for (int j = 0; j < problem.orderCount(); j++) {
                patterns[i][j] = 0;
            }
            patterns[i][i] = (int) (problem.getPieceSize() / problem.getOrder(i).getSize());
        }

        int it = 0;
        while (true) {
            it++;
            System.out.println("++++++++++ iteration " + it);

            SolverOutput dual = solveSimplex(patterns, true);
            System.out.println("dual: " + dual);
            SolverOutput knp = Knapsack.solve(problem, dual.result);
            System.out.println("knp: " + knp);

            if (knp.objVal <= 1) {
                break;
            }

            float[][] temp = new float[patterns.length + 1][problem.orderCount()];

            for (int i = 0; i < patterns.length; i++) {
                for (int j = 0; j < problem.orderCount(); j++) {
                    temp[i][j] = patterns[i][j];
                }
            }

            temp[patterns.length] = knp.result;
            patterns = temp;
        }

        SolverOutput solverOutput = solveSimplex(patterns, false);

        System.out.println("-------- Cutting solution");

        for (int i = 0; i < patterns.length; i++) {
            float patternCount = solverOutput.result[i];

            //System.out.println(i + " = " + patternCount);

            if (patternCount == 0)
                continue;

            float[] pattern = patterns[i];
            System.out.print(patternCount + " x (");

            int p = 0;
            for (int j = 0; j < problem.orderCount(); j++) {
                int cuts = (int) pattern[j];

                if (cuts == 0)
                    continue;

                Order order = problem.getOrder(j);
                if (p > 0)
                    System.out.print(" + ");

                System.out.print(cuts + "x" + order.getSize() + "cm");
                p++;
            }

            System.out.println(")");
        }

        float pieces = solverOutput.objVal;
        System.out.println("> pieces needed : " + (int) Math.ceil(pieces) + " (ceil of calculated value : " + pieces + ").");
        System.out.println("finished in " + (System.currentTimeMillis() - startTime) + " ms (I/O included).");
    }

}
