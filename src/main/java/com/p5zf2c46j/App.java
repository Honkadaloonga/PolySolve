package com.p5zf2c46j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

public class App {
    final static int prec = 32;
    final static MathContext ctx = new MathContext(prec);

    public static void main(String[] args) {
        //String[] scoeff = {"1", "0", "-15", "-10", "24"};
        String[] scoeff = {"1","-4","-496","1600","38400","0"};
        for (String s : scoeff) {
            System.out.print(s + " ");
        }
        System.out.print("\n");
        BigDecimal[] coeff = new BigDecimal[scoeff.length];
        for (int i = 0; i < coeff.length; i++) {
            coeff[i] = new BigDecimal(scoeff[coeff.length-i-1]);
        }

        BigDecimal[] out = polySolve(coeff);
        for (BigDecimal bigDecimal : out) {
            System.out.println(bigDecimal.setScale(prec-2, ctx.getRoundingMode()).stripTrailingZeros().toPlainString());
        }
    }

    private static BigDecimal[] polySolve(BigDecimal[] coeff) {
        ArrayList<BigDecimal> roots = new ArrayList<>();
        BigDecimal[] newcoeff = coeff.clone();
        for (int i = 1; i < coeff.length; i++) {
            BigDecimal[] der = derivative(newcoeff);
            BigDecimal nt = BigDecimal.TEN;
            for (int j = 0; j < 1024; j++) {
                nt = nt.subtract(eval(newcoeff, nt).divide(eval(der, nt), ctx), ctx);
            }
            if (nt.abs().compareTo(new BigDecimal("1E-" + (prec-2))) <= 0) {
                nt = BigDecimal.ZERO;
            }
            BigDecimal[] nextcoeff = polyDiv(newcoeff, nt);
            if (nextcoeff == null) {
                nt = BigDecimal.TEN.negate();
                for (int j = 0; j < 1024; j++) {
                    nt = nt.subtract(eval(newcoeff, nt).divide(eval(der, nt), ctx), ctx);
                }
                if (nt.abs().compareTo(new BigDecimal("1E-" + (prec-2))) <= 0) {
                    nt = BigDecimal.ZERO;
                }
                nextcoeff = polyDiv(newcoeff, nt);
                if (nextcoeff == null) {
                    break;
                }
            }
            roots.add(nt);
            newcoeff = nextcoeff;
        }
        BigDecimal[] out = new BigDecimal[roots.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = roots.get(i);
        }
        return out;
    }

    private static BigDecimal eval(BigDecimal[] coeff, BigDecimal x) {
        BigDecimal out = coeff[0];
        for (int i = 1; i < coeff.length; i++) {
            out = out.add(x.pow(i, ctx).multiply(coeff[i], ctx), ctx);
        }
        return out;
    }

    private static BigDecimal[] derivative(BigDecimal[] coeff) {
        BigDecimal[] out = new BigDecimal[coeff.length - 1];
        for (int i = 0; i < out.length; i++) {
            out[i] = coeff[i + 1].multiply(new BigDecimal(i + 1), ctx);
        }
        return out;
    }

    private static BigDecimal[] polyDiv(BigDecimal[] coeff, BigDecimal div) {
        int cl = coeff.length;
        int ol = cl - 1;
        BigDecimal[] out = new BigDecimal[ol];
        BigDecimal t = BigDecimal.ZERO;
        for (int i = 0; i < ol; i++) {
            out[ol-i-1] = coeff[cl-i-1].add(t, ctx);
            t = out[ol-i-1].multiply(div, ctx);
            if (t.abs().compareTo(new BigDecimal("1E-" + (prec-2))) <= 0) {
                t = BigDecimal.ZERO;
            }
        }
        t = coeff[0].add(t, ctx);
        return t.abs().compareTo(new BigDecimal("1E-" + (prec-2))) <= 0 ? out : null;
    }
}
