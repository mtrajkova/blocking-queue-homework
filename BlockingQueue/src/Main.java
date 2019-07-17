import java.util.concurrent.TimeUnit;

public class Main {

    public static class MyRunnable implements Runnable {

        private ThreadLocal<Integer> random = new ThreadLocal<>();
        private ThreadLocal<Integer> element = new ThreadLocal<>();
        private ThreadLocal<Long> timeout = new ThreadLocal<>();
        private ThreadLocal<TimeUnit> unit = new ThreadLocal<>();

        @Override
        public void run() {
            random.set((int) (Math.random() * 8D));

            switch (random.get()) {
                case 0:
            }

            threadLocal.set((int) (Math.random() * 100D));


        }
    }

    public static void main(String[] args) {


    }
}
