package com.espaneg.logic;

import java.util.List;

// in MathGen.java
public class MathGen {

    public enum Range { SMALL(1,20), MEDIUM(1,50), LARGE(1,100);
        public final int min, max;
        Range(int min,int max){ this.min=min; this.max=max; }
    }

    public static java.util.List<String> generate(String operator, Range range, int count) {
        java.util.List<String> out = new java.util.ArrayList<>(count);
        java.util.Random rnd = new java.util.Random();

        for (int i = 0; i < count; i++) {
            int a = rnd.nextInt(range.max - range.min + 1) + range.min;
            int b = rnd.nextInt(range.max - range.min + 1) + range.min;

            if (operator.equals("-") && b > a) { int t=a; a=b; b=t; }

            if (operator.equals("/")) {
                // make a divisible by b
                if (b == 0) b = 1;
                a = a * (rnd.nextInt(Math.max(1, range.max / Math.max(1,b))) + 1);
                // ensure a within range? (optional)
            }

            String symbol = operator;
            out.add(a + " " + symbol + " " + b + " = ");
        }

        return out;
    }
}
