package Glitch.Lib.utilities;

import Glitch.Lib.NetworkTableLogger;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NetworkTableLoggerTest {

    private static class TestSendable implements Sendable {
        private double value;

        private TestSendable(double initialValue) {
            value = initialValue;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public void initSendable(SendableBuilder builder) {
            builder.setSmartDashboardType("TestSendable");
            builder.addDoubleProperty("value", () -> value, null);
        }
    }

    private NetworkTableLogger logger;

    private NetworkTableEntry getTableEntry(String key) {
        return logger.getNetworkTable().getEntry(key);
    }

    @BeforeEach
    public void setUp() {
        logger = new NetworkTableLogger("TestLogger", 50.0);
    }

    @Test
    public void testChangedValuePublishes() {
        String key = "doubleValue";

        logger.log(key, 1.0);
        assertEquals(1.0, getTableEntry(key).getDouble(-1));

        logger.log(key, 1.0);
        assertEquals(1.0, getTableEntry(key).getDouble(-1));
    }

    @Test
    public void testRateLimitDefersChangedPublish() throws InterruptedException {
        String key = "rateLimited";

        logger.log(key, 1.0);
        assertEquals(1.0, getTableEntry(key).getDouble(-1));

        logger.log(key, 2.0);
        assertEquals(1.0, getTableEntry(key).getDouble(-1));

        Thread.sleep(25);
        logger.log(key, 2.0);
        assertEquals(2.0, getTableEntry(key).getDouble(-1));
    }

    @Test
    public void testIntArraySnapshotDetectsInPlaceMutation() throws InterruptedException {
        String key = "intArray";
        int[] value = new int[]{1, 2, 3};

        logger.log(key, value);
        assertEquals(1, getTableEntry(key).getIntegerArray(new long[0])[0]);

        value[0] = 9;
        Thread.sleep(25);
        logger.log(key, value);
        assertEquals(9, getTableEntry(key).getIntegerArray(new long[0])[0]);
    }

    @Test
    public void testSetLoggingRateHzValidation() {
        assertThrows(IllegalArgumentException.class, () -> logger.setLoggingRateHz(0.0));
        assertThrows(IllegalArgumentException.class, () -> logger.setLoggingRateHz(50.1));

        logger.setLoggingRateHz(25.0);
        assertEquals(25.0, logger.getLoggingRateHz());
    }

    @Test
    public void testLogSendablePublishesAndUpdates() throws InterruptedException {
        String key = "sendable";
        TestSendable sendable = new TestSendable(2.5);

        logger.logSendable(key, sendable);
        assertEquals(2.5, logger.getNetworkTable().getSubTable(key).getEntry("value").getDouble(Double.NaN), 1e-9);

        sendable.setValue(7.0);
        Thread.sleep(25);
        logger.logSendable(key, sendable);
        assertEquals(7.0, logger.getNetworkTable().getSubTable(key).getEntry("value").getDouble(Double.NaN), 1e-9);
    }
}