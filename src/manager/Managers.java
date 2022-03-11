package manager;

public class Managers {
    TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
}
