package pe.mobytes.examplemvvm1;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppExecutors {

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainTheread;

    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainTheread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainTheread = mainTheread;
    }

    @Inject
    public AppExecutors(){
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThereadExecutor());
    }

    public Executor diskIO(){
        return diskIO;
    }

    public Executor network(){
        return networkIO;
    }

    public Executor mainTheread(){
        return mainTheread;
    }

    private static class MainThereadExecutor implements Executor{
        private Handler mainThereadHandler = new Handler(Looper.myLooper());

        @Override
        public void execute(Runnable command) {
            mainThereadHandler.post(command);
        }
    }
}
