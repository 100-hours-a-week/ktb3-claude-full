package ktb.util;

public class Out {
    private static final Out instance = new Out();

    private Out() {}

    public static Out getInstance() {
        return instance;
    }

    public synchronized void print(String message) {
        System.out.print(message);
    }

    public synchronized void println(String message) {
        System.out.println(message);
    }

    public synchronized void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
}
