package oyyq.cube.simulator.model;

import java.util.ArrayList;

import oyyq.cube.simulator.cube.CubeNNN;
import oyyq.cube.simulator.events.CommonDataListener;
import oyyq.cube.util.CubeTimer;

public class CommonData {

    public static final int                      DATA_ID_CUBE_SIZE   = 0;
    public static final int                      DATA_ID_TIMER       = 1;
    public static final int                      DATA_ID_BLINDFOLDED = 2;

    private static int                           cubeSize;
    private static CubeTimer                     timer               = new CubeTimer();
    private static boolean                       blindfolded         = false;

    private static ArrayList<CommonDataListener> listeners           = new ArrayList<>();

    public static void addListener(CommonDataListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public static void removeListener(CommonDataListener listener) {
        while (listeners.remove(listener));
    }

    public static synchronized void setCubeSize(int newSize) {
        if (newSize == cubeSize || newSize > CubeNNN.MAX_CUBE_SIZE
                || newSize < CubeNNN.MIN_CUBE_SIZE) {
            return;
        }
        cubeSize = newSize;
        timer.reset();
        dataChanged(DATA_ID_CUBE_SIZE);
    }

    public static synchronized int getCubeSize() {
        return cubeSize;
    }

    public static CubeTimer.State getTimerState() {
        return timer.getState();
    }

    public static String getTime() {
        return timer.getTime();
    }

    public static void startTimer() {
        timer.start();
        dataChanged(DATA_ID_TIMER);
    }

    public static void stopTimer() {
        timer.stop();
        dataChanged(DATA_ID_TIMER);
    }

    public static boolean timerIsRunning() {
        return timer.isRunning();
    }

    public static void setDNF() {
        timer.setDNF();
        dataChanged(DATA_ID_TIMER);
    }

    public static void resetTimer() {
        timer.reset();
        dataChanged(DATA_ID_TIMER);
    }

    public static void startInspection(boolean blindfolded) {
        timer.startInspection(blindfolded);
        dataChanged(DATA_ID_TIMER);
    }

    public static boolean isBlindfolded() {
        return blindfolded;
    }

    public static void setBlindfolded(boolean newValue) {
        blindfolded = newValue;
        dataChanged(DATA_ID_BLINDFOLDED);
    }

    private static void dataChanged(int dataId) {
        for (CommonDataListener listener : listeners) {
            listener.commonDataChanged(dataId);
        }
    }
}
