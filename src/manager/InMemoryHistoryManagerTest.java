package manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest extends HistoryManagerTest{
    @BeforeEach
    public void initManager() {
        manager = new InMemoryHistoryManager();
    }
}
