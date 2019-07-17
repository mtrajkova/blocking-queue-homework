import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static class MyRunnable implements Runnable {

        private ThreadLocal<Integer> random = new ThreadLocal<>();
        private ThreadLocal<Integer> element = new ThreadLocal<>();
        private ThreadLocal<Long> timeout = new ThreadLocal<>();
        private ThreadLocal<TimeUnit> unit = new ThreadLocal<>();
        private static BlockingQueue blockingQueue = new BlockingQueue(10);

        @Override
        public void run() {
            random.set((int) (Math.random() * 8D));
            element.set((int) (Math.random() * 8));
            timeout.set((long) (Math.random() * 2));
            unit.set(TimeUnit.SECONDS);

            switch (random.get()) {
                case 0:
                    System.out.println("ADDING ELEMENT *" + element.get() + "* WITH SUCCESS " + blockingQueue.add(element.get()));
                    break;
                case 1:
                    System.out.println("OFFERING ELEMENT *" + element.get() + "* WITH SUCCESS " + blockingQueue.offer(element.get()));
                    break;
                case 2:
                    try {
                        blockingQueue.put(element.get());
                        System.out.println("PUTTING ELEMENT *" + element.get() + "*");
                    } catch (InterruptedException e) {
                        System.out.println("BLOCKING QUEUE IS FULL");
                    }
                    break;
                case 3:
                    System.out.println("REMAINING CAPACITY " + blockingQueue.remainingCapacity() + "\n");
                    break;
                case 4:
                    System.out.println("CONTAINS ELEMENT " + element.get() + " " + blockingQueue.contains(element.get()));
                    break;
                case 5:
                    System.out.println("REMOVING ELEMENT *" + element.get() + "* WITH SUCCESS " + blockingQueue.remove(element.get()));
                    break;
                case 6:
                    try {
                        System.out.println("REMOVING FIRST ELEMENT *" + blockingQueue.take() + "* WITH TAKE");
                    } catch (InterruptedException e) {
                        System.out.println("BLOCKING QUEUE IS EMPTY");
                    }
                    break;
                case 7:
                    try {
                        System.out.println("REMOVING FIRST ELEMENT *" + blockingQueue.poll(timeout.get(), unit.get()) + "* WITH POLL");
                    } catch (InterruptedException e) {
                        System.out.println("NO CAN DO WITH POLLING");
                    }
                    break;
                case 8:
                    System.out.println();
                    try {
                        System.out.println("OFFERING ELEMENT WITH TIMEOUT *" + element.get() + "* WITH SUCCESS " + blockingQueue.offer(element.get(), timeout.get(), unit.get()));
                    } catch (InterruptedException e) {
                        System.out.println("NO CAN DO WITH OFFERING ELEMENT" + element.get());
                    }
                    break;
                default:
                    System.out.println("DOBI BROJCHE OD RANDOM SHO NE CHINI");
            }

        }

        public LinkedList getBlockingQueueContent() {
            return blockingQueue.getList();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyRunnable sharedRunnableInstance = new MyRunnable();

        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(sharedRunnableInstance));
            threads.get(i).start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for(Object element : sharedRunnableInstance.getBlockingQueueContent()){
            System.out.println(element);
        }
    }
}
