/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meicompany.pi.realtime.ode;

/**
 *
 * @author mpopescu
 */
public class RK78 extends ODE {
        //https://en.wikipedia.org/wiki/Bogacki–Shampine_method
    
    private static final double[] C =  new double[] { 1/18, 1/12, 1/8, 5/16, 3/8, 59/400, 93/200, 0.564865451382260, 13/20, 0.924656277640504, 1, 1};

    private static final double[][] A = new double[][] { {1/18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1/48, 1/16, 0.0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
            { 1/32, 0, 3/32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} ,
            { 5/16, 0, -75/64, 75/64, 0, 0, 0, 0, 0, 0, 0, 0, 0} ,
            { 3/80, 0, 0, 3/16, 3/20, 0, 0, 0, 0, 0, 0, 0, 0} ,
            { 29443841/614563906, 0, 0, 77736538/692538347, -28693883/1125000000, 23124283/1800000000, 0, 0, 0, 0, 0, 0, 0},
            { 16016141/946692911, 0, 0, 61564180/158732637, 22789713/633445777, 0.196970214215666, -180193667/1043307555, 0, 0, 0, 0, 0, 0},
            { 39632708/573591083, 0, 0, -433636366/683701615, -0.161197575224604, 100302831/723423059, 790204164/839813087, 0.211636326481944, 0, 0, 0, 0, 0},
            { 246121993/1340847787, 0, 0, -2.468768084315593, -309121744/1061227803, -12992083/490766935, 2.847838764192801, 393006217/1396673457, 123872331/1001029789, 0, 0, 0, 0},
            {-1028468189/846180014, 0, 0, 16.672608665945774, 1311729495/1432422823, -6.056605804357471, -16.003573594156180, 14.849303086297663, -13.371575735289849, 5.134182648179638, 0, 0, 0},
            { 185892177/718116043, 0, 0, -4.774485785489205, -477755414/1098053517, -703635378/230739211, 5.577920039936100, 6.155831589861040, -5.062104586736939, 2.193926173180679, 65686358/487910083, 0, 0},
            {403863854/491063109, 0, 0, -11.658673257277664, -411421997/543043805, 652783627/914296604, 12.075774986890057, -2.127659113920403, 1.990166207048955, -160528059/685178525, 248638103/1413531060, 0, 0}};

    private static final double[] B8 = new double[] { 14005451/335480064, 0, 0, 0, 0, -59238493/1068277825, 181606767/758867731,   561292985/797845732,   -1041891430/1371343529,  760417239/1151165299, 118820643/751138087, -0.238109538752863,  1/4};

    private static final double[] B7 = new double[] { 13451932/455176623, 0, 0, 0, 0, -808719846/976000145, 0.311240900051118, 656045339/265891186,   -2.546941651841909,   465885868/322736535,  53011238/667516719,                  2/45,    0};

    
    final double[] sol1;
    final double[] sol2;
    final double[][] fevals;
    final double[] e;
    final double[] e_rel;
    final int n;
    
    public RK78(Dynamics dynamics, double[] x, ODEOptions options) {
        super(dynamics,x,options);
        n = x.length;
        sol1 = new double[n];
        sol2 = new double[n];
        fevals = new double[13][n]; 
        e = new double[n];
        e_rel = new double[n];
        System.arraycopy(dynamics.calc(x, time), 0, fevals[1], 0, n);
    }
    
    @Override
    void step() {
        for(int iter = 0; iter < 10; iter++) {
            System.arraycopy(dynamics.calc(sol1, time), 0, fevals[1], 0, n);
            for(int i = 1; i < 13; i++){
                double h = dt*C[i];
                for(int j = 0; j < i; j++) {
                    if (A[i][j] < 1e-16) {
                        double coef = dt*A[i][j];
                        for(int k = 0; k < n; k++) {
                            sol1[k] = x[k] + fevals[j][k]*coef;
                        }
                    }
                }
                System.arraycopy(dynamics.calc(sol1, time+h), 0, fevals[i], 0, n);
            }
            for(int j = 0; j < 13; j++) {
                for(int k = 0; k < n; k++) {
                    sol1[k] = x[k] + dt*fevals[j][k]*B7[j];
                }
            }
            for(int j = 0; j < 13; j++) {
                for(int k = 0; k < n; k++) {
                    sol2[k] = x[k] + dt*fevals[j][k]*B8[j];
                }
            }
            double max_err = 0;
            for(int i = 0; i < n; i++){
                e[i] = sol2[i] - sol1[i];
                e_rel[i] = Math.abs(e[i]/x[i]);
                if(e_rel[i] > max_err) {
                    max_err = e_rel[i];
                }
            }
            if (max_err > options.getRelativeTolerance()) {
                dt *= 0.91*Math.pow(options.getRelativeTolerance()*dt/max_err,0.125);
            } else {
                System.arraycopy(sol1, 0, x, 0, n);
                time +=dt;
                break;
            }
        }
    }
}
