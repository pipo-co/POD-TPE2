package queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class Query4Test extends AbstractQueryTest {

    @Test
    public void test_query_4() {
        final Map<String, String> map = client.getMap("hola");

        assertFalse(map.containsKey("test"));
        map.put("test", "1");

        assertTrue(map.containsKey("test"));
        assertEquals("1", map.get("test"));
    }
}
