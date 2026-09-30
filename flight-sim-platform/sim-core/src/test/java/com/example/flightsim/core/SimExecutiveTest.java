package com.example.flightsim.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimExecutiveTest {

    @Test
    void stepsEachComponentAtItsOwnRate() {
        SimContext context = SimContext.create(1L);
        SimExecutive executive = new SimExecutive(context, 60);
        CountingComponent fast = new CountingComponent("fast");
        CountingComponent slow = new CountingComponent("slow");
        executive.register(fast, 60);
        executive.register(slow, 20);

        executive.runFor(1.0);

        assertEquals(60, fast.steps);
        assertEquals(20, slow.steps);
        assertEquals(3.0 / 60.0, slow.lastDt, 1e-12);
        assertEquals(1.0, context.time().elapsedSeconds(), 1e-9);
    }

    @Test
    void rejectsRatesThatDoNotDivideTheFrameRate() {
        SimExecutive executive = new SimExecutive(SimContext.create(1L), 60);
        assertThrows(IllegalArgumentException.class, () -> executive.register(new CountingComponent("odd"), 7));
    }

    @Test
    void componentsAreSteppedInRegistrationOrder() {
        SimContext context = SimContext.create(1L);
        SimExecutive executive = new SimExecutive(context, 10);
        List<String> order = new ArrayList<>();
        executive.register(new RecordingComponent("producer", order), 10);
        executive.register(new RecordingComponent("consumer", order), 10);
        executive.stepFrame();
        assertEquals(List.of("producer", "consumer"), order);
    }

    @Test
    void resetClearsSignalsButKeepsMalfunctions() {
        SimContext context = SimContext.create(1L);
        context.malfunctions().register("test.fail", "Test failure", 29);
        context.malfunctions().activate("test.fail");
        context.bus().publish("test.value", 3.0);
        SimExecutive executive = new SimExecutive(context);

        executive.reset();

        assertFalse(context.bus().has("test.value"));
        assertTrue(context.malfunctions().isActive("test.fail"));
    }

    @Test
    void busRejectsNaN() {
        SimBus bus = new SimBus();
        assertThrows(IllegalArgumentException.class, () -> bus.publish("x", Double.NaN));
    }

    private static final class CountingComponent implements SimComponent {
        private final String name;
        private int steps;
        private double lastDt;

        CountingComponent(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void step(SimContext context, double dtSeconds) {
            steps++;
            lastDt = dtSeconds;
        }
    }

    private static final class RecordingComponent implements SimComponent {
        private final String name;
        private final List<String> order;

        RecordingComponent(String name, List<String> order) {
            this.name = name;
            this.order = order;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public void step(SimContext context, double dtSeconds) {
            order.add(name);
        }
    }
}
