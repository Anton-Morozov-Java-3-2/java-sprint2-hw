package task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void testEquals() {
        Task t1 = new Task("task 1", "test 1", Status.NEW);
        t1.setId(1);
        Task t2 = new Task("task 1", "test 1", Status.NEW);
        t2.setId(1);

        Assertions.assertEquals(
                t1.getId() == t2.getId() &&
                        t1.getTitle().equals(t2.getTitle()) &&
                        t1.getDescription().equals(t2.getDescription()) &&
                        t1.getStatus().equals(t2.getStatus()),
                        t1.equals(t2), "объекты должны быть равны"
                );

        Task t3 = new Task("task 2", "test 2", Status.NEW);
        t2.setId(1);

        Assertions.assertNotEquals(
                t1.getId() == t3.getId() &&
                        t1.getTitle().equals(t3.getTitle()) &&
                        t1.getDescription().equals(t3.getDescription()) &&
                        t1.getStatus().equals(t3.getStatus()),
                        t1.equals(t3),"объекты не должны быть равны");
    }

    @Test
    public void testToString() {
        Task t1 = new Task("task 1", "test 1", Status.NEW);
        t1.setId(1);
        Assertions.assertEquals("1,TASK,task 1,NEW,test 1,\n", t1.toString());

    }
}
