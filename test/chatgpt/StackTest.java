package chatgpt;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StackTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testPushPopPeekLifo() {
        Stack s = new Stack(3);
        assertTrue(s.isEmpty());
        s.push(1);
        s.push(2);
        s.push(3);
        assertTrue(s.isFull());
        // peek should not remove
        assertEquals(3, s.peek());
        assertEquals(3, s.peek(), "peek should be idempotent");
        // pop returns in LIFO order
        assertEquals(3, s.pop());
        assertEquals(2, s.pop());
        assertEquals(1, s.pop());
        assertTrue(s.isEmpty());
    }

    @Test
    void testIsEmptyIsFullSizeTransitions() {
        Stack s = new Stack(2);
        assertEquals(0, s.size());
        assertTrue(s.isEmpty());
        assertFalse(s.isFull());

        s.push(10);
        assertEquals(1, s.size());
        assertFalse(s.isEmpty());
        assertFalse(s.isFull());

        s.push(20);
        assertEquals(2, s.size());
        assertFalse(s.isEmpty());
        assertTrue(s.isFull());

        // pop one element
        int popped = s.pop();
        assertEquals(20, popped);
        assertEquals(1, s.size());
        assertFalse(s.isEmpty());
        assertFalse(s.isFull());
    }

    @Test
    void testPushOnFullThrowsWithMessage() {
        Stack s = new Stack(1);
        s.push(99);
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> s.push(100)
        );
        assertEquals("Stack is full", ex.getMessage());
    }

    @Test
    void testPopOnEmptyThrowsWithMessage() {
        Stack s = new Stack(1);
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            s::pop
        );
        assertEquals("Stack is empty", ex.getMessage());
    }

    @Test
    void testPeekOnEmptyThrowsWithMessage() {
        Stack s = new Stack(2);
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            s::peek
        );
        assertEquals("Stack is empty", ex.getMessage());
    }

    @Test
    void testZeroCapacityBehavior() {
        Stack s = new Stack(0);
        // top == -1, capacity - 1 == -1 so isFull returns true immediately (we assert that behavior)
        assertTrue(
            s.isFull(),
            "With capacity 0 the implementation reports isFull() == true"
        );
        assertTrue(s.isEmpty(), "And it's also empty (top == -1)");
        IllegalStateException ex = assertThrows(
            IllegalStateException.class,
            () -> s.push(1)
        );
        assertEquals("Stack is full", ex.getMessage());
        // pop and peek should still throw "Stack is empty"
        IllegalStateException exPop = assertThrows(
            IllegalStateException.class,
            s::pop
        );
        assertEquals("Stack is empty", exPop.getMessage());
        IllegalStateException exPeek = assertThrows(
            IllegalStateException.class,
            s::peek
        );
        assertEquals("Stack is empty", exPeek.getMessage());
    }

    @Test
    void testNegativeSizeConstructorThrowsNegativeArraySizeException() {
        assertThrows(NegativeArraySizeException.class, () -> new Stack(-1));
    }

    @Test
    void testMainPrintsExpectedOutput() {
        // capture stdout
        System.setOut(new PrintStream(outContent));
        Stack.main(new String[0]);
        String output = outContent.toString();
        // The exact line separators can vary by platform; assert the important substrings exist.
        assertTrue(
            output.contains("Top element: 30"),
            "Should print top element 30"
        );
        assertTrue(
            output.contains("Popped: 30"),
            "Should print popped value 30"
        );
        assertTrue(
            output.contains("Stack size: 2"),
            "Should print size 2 after one pop"
        );
        assertTrue(
            output.contains("Is empty: false"),
            "Should print isEmpty false"
        );
    }
}
