package oyyq.cube.util;

import java.util.Random;

import oyyq.cube.cubical.Cube222;
import oyyq.cube.cubical.Cube333;
import oyyq.cube.simulator.cube.CubeNNN;

public class CubeNNNScrambler {

    public static String scramble(int size) {
        if (size > CubeNNN.MAX_CUBE_SIZE || size < CubeNNN.MIN_CUBE_SIZE) {
            return "";
        }
        if (size == 2) {
            Cube222 cube;
            String generator;
            do {
                cube = Cube222.randomCube();
                generator = cube.generate();
            } while (cube.getSolutionLength() < 4);
            return generator;
        }
        if (size == 3) {
            return Cube333.randomCube().generate(21);
        }
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        int scrambleLength = (size - 2) * 20;
        int count = 0;
        while (count < scrambleLength) {
            for (int axis = 0; axis < 6 && count < scrambleLength; axis++) {
                for (int shift = 1; shift <= (size >> 1) - axis / 3 * ((size + 1) & 1)
                        && count < scrambleLength; shift++) {
                    int turns = r.nextInt(4);
                    if (turns == 0) {
                        continue;
                    }
                    if (shift > 2) {
                        sb.append(shift);
                    }
                    sb.append("URFDLB".charAt(axis));
                    if (shift >= 2) {
                        sb.append("w");
                    }
                    sb.append(" 2'".charAt(turns - 1));
                    if (turns > 1) {
                        sb.append(" ");
                    }
                    count++;
                }
            }
        }
        return sb.toString().trim();
    }
}
