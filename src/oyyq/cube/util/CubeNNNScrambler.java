package oyyq.cube.util;

import java.util.Arrays;
import java.util.Random;

import oyyq.cube.cubical.cube222.Cube222;
import oyyq.cube.cubical.cube333.CubieCube;
import oyyq.cube.cubical.cube333.TwoPhaseSolver;
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
            return new TwoPhaseSolver(CubieCube.randomCube()).generate(21);
        }
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        int scrambleLength = (size - 2) * 20;
        int count = 0;
        int lastAxis = -1;
        boolean[][] turned = new boolean[3][size - 1];
        while (count < scrambleLength) {
            int axis = r.nextInt(3);
            int shift = r.nextInt(size - 1);
            if (axis != lastAxis) {
                if (lastAxis != -1) {
                    Arrays.fill(turned[lastAxis], false);
                }
            } else {
                if (turned[axis][shift]) {
                    continue;
                }
            }
            int turns = r.nextInt(3);
            turned[axis][shift] = true;
            lastAxis = axis;
            if (shift >= size >> 1) {
                axis += 3;
                shift -= size >> 1;
            }
            shift++;
            if (shift > 2) {
                sb.append(shift);
            }
            sb.append("URFDLB".charAt(axis));
            if (shift >= 2) {
                sb.append("w");
            }
            sb.append(" 2'".charAt(turns));
            if (turns > 0) {
                sb.append(" ");
            }
            count++;
        }
        return sb.toString().trim();
    }
}
