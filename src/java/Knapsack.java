import org.gnu.glpk.*;

import java.util.Arrays;

public class Knapsack {

    public static SolverOutput solve(Problem problem, float[] cols) {

        float[] result = new float[problem.orderCount()];
        Arrays.fill(result, 0);

        glp_prob lp = GLPK.glp_create_prob();
        GLPK.glp_add_cols(lp, cols.length);
        GLPK.glp_add_rows(lp, problem.orderCount());
        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);

        GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_UP, 0, problem.getPieceSize());

        for (int i = 0; i < cols.length; i++) {
            GLPK.glp_set_col_kind(lp, i + 1, GLPKConstants.GLP_IV); // integer
            GLPK.glp_set_col_bnds(lp, i + 1, GLPKConstants.GLP_DB, 0, problem.getOrder(i).getCount()); // >= 01
            GLPK.glp_set_obj_coef(lp, i + 1, cols[i]);
        }

        SWIGTYPE_p_int ia = GLPK.new_intArray(cols.length + 1);
        SWIGTYPE_p_int ja = GLPK.new_intArray(cols.length + 1);
        SWIGTYPE_p_double ra = GLPK.new_doubleArray(cols.length + 1);
        int ne = 0;

        for (int i = 0; i < cols.length; i++) {
            ne++;
            GLPK.intArray_setitem(ia, i + 1, 1); // a[1, 1] a[1, 2] a[1, 3]...
            GLPK.intArray_setitem(ja, i + 1, i + 1);
            GLPK.doubleArray_setitem(ra, i + 1, problem.getOrder(i).getSize());
        }

        GLPK.glp_load_matrix(lp, ne, ia, ja, ra);
        glp_iocp param = new glp_iocp();
        GLPK.glp_init_iocp(param);
        param.setPresolve(GLPKConstants.GLP_ON);

        GLPK.glp_intopt(lp, param);
        float objVal = (float) GLPK.glp_mip_obj_val(lp);

        for (int i = 0; i < cols.length; i++) {
            float r = (float) GLPK.glp_mip_col_val(lp, i + 1);
            result[i] = r;
        }

        // cleanup
        GLPK.delete_intArray(ia);
        GLPK.delete_intArray(ja);
        GLPK.delete_doubleArray(ra);
        GLPK.glp_delete_prob(lp);
        GLPK.glp_free_env();

        return new SolverOutput(objVal, result);
    }

}
