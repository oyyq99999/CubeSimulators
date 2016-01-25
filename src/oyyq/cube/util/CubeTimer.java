package oyyq.cube.util;

public class CubeTimer {

    private static final long HOUR_IN_MILLIS   = 3600000L;
    private static final long MINUTE_IN_MILLIS = 60000L;
    private static final long SECOND_IN_MILLIS = 1000L;

    private static final long INSPECTION_TIME  = 15;

    public static enum State {
        NOT_STARTED, INSPECTING, RUNNING, STOPPED, DNF
    }

    private State   state;
    private long    startTime;
    private long    endTime;
    private boolean plus2;
    private boolean blindfolded;

    public CubeTimer() {
        reset();
    }

    public void startInspection(boolean blindfolded) {
        if (state == State.NOT_STARTED) {
            startTime = System.currentTimeMillis();
            state = State.INSPECTING;
            this.blindfolded = blindfolded;
        }
    }

    public void start() {
        if (state == State.INSPECTING) {
            getState();
        }
        if (state == State.INSPECTING || state == State.NOT_STARTED) {
            if (state == State.INSPECTING && !blindfolded) {
                startTime = System.currentTimeMillis();
            }
            state = State.RUNNING;
        }
    }

    public void stop() {
        if (state == State.RUNNING) {
            endTime = System.currentTimeMillis();
            state = State.STOPPED;
        }
    }

    public void reset() {
        state = State.NOT_STARTED;
        plus2 = false;
        blindfolded = false;
    }

    public void setDNF() {
        state = State.DNF;
    }

    private void setPlus2() {
        plus2 = true;
    }

    public String getTime() {
        switch (state) {
            case INSPECTING:
            case RUNNING:
                return formatTime(System.currentTimeMillis() - startTime);
            case STOPPED:
                return formatTime(endTime - startTime);
            case DNF:
                return "DNF";
            case NOT_STARTED:
            default:
                return formatTime(0L);
        }
    }

    public boolean isRunning() {
        return state == State.RUNNING || state == State.INSPECTING;
    }

    public State getState() {
        if (state == State.INSPECTING && !blindfolded) {
            long now = (System.currentTimeMillis() - startTime) / SECOND_IN_MILLIS;
            if (now >= INSPECTION_TIME + 2) {
                this.setDNF();
            } else if (now >= INSPECTION_TIME) {
                this.setPlus2();
            }
        }
        return this.state;
    }

    private String formatTime(long timeInMillis) {
        if (state == State.INSPECTING && !blindfolded) {
            long now = timeInMillis / SECOND_IN_MILLIS;
            if (now >= INSPECTION_TIME + 2) {
                this.setDNF();
                return "DNF";
            }
            if (now >= INSPECTION_TIME) {
                this.setPlus2();
                return "+2";
            }
            return Long.toString(INSPECTION_TIME - now);
        }
        StringBuilder sb = new StringBuilder();
        boolean hasHour = false;
        boolean hasMinute = false;
        if (plus2 && state == State.STOPPED) {
            timeInMillis += 2000;
        }
        if (timeInMillis > HOUR_IN_MILLIS) {
            sb.append(timeInMillis / HOUR_IN_MILLIS + ":");
            timeInMillis %= HOUR_IN_MILLIS;
            hasHour = true;
        }
        if (hasHour || timeInMillis > MINUTE_IN_MILLIS) {
            sb.append(String.format(hasHour ? "%02d:" : "%d:", timeInMillis / MINUTE_IN_MILLIS));
            timeInMillis %= MINUTE_IN_MILLIS;
            hasMinute = true;
        }
        sb.append(String.format(hasMinute ? "%02d.%03d" : "%d.%03d",
                timeInMillis / SECOND_IN_MILLIS, timeInMillis % SECOND_IN_MILLIS));
        if (plus2 && state == State.STOPPED) {
            sb.append("+");
        }
        return sb.toString();
    }
}
