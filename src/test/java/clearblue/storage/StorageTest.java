package clearblue.storage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Storage}.
 */
public class StorageTest {
    @Test
    public void containsFieldSeparator_textWithoutSeparator_returnsFalse() {
        assertFalse(Storage.containsFieldSeparator("read book"));
    }

    @Test
    public void containsFieldSeparator_textWithSeparator_returnsTrue() {
        assertTrue(Storage.containsFieldSeparator("read book | urgent"));
    }
}
