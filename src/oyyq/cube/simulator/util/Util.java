package oyyq.cube.simulator.util;

public final class Util {

    private Util() {}
    
    public static void rotate(int[] arr, int... indices) {
        int tmp = arr[indices[0]];
        for (int i = 0, len = indices.length; i < len - 1; i++) {
            arr[indices[i]] = arr[indices[i + 1]];
        }
        arr[indices[indices.length - 1]] = tmp;
    }
}
