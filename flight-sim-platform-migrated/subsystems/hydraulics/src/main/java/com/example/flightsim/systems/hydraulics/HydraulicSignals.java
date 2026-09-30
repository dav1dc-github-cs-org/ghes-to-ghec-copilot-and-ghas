package com.example.flightsim.systems.hydraulics;

/** Signal names owned by the hydraulic subsystem. */
public final class HydraulicSignals {

    /** Green reservoir quantity ratio. */
    public static final String GREEN_RESERVOIR_QTY = "hyd.green.reservoir.qty";
    /** Blue reservoir quantity ratio. */
    public static final String BLUE_RESERVOIR_QTY = "hyd.blue.reservoir.qty";
    /** Yellow reservoir quantity ratio. */
    public static final String YELLOW_RESERVOIR_QTY = "hyd.yellow.reservoir.qty";
    /** Power transfer unit active flag. */
    public static final String PTU_ACTIVE = "hyd.ptu.active";

    private HydraulicSignals() {
    }
}
