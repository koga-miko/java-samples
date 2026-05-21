package com.example;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        JniSample jni = new JniSample();

        // 1. 文字列をC++側で出力
        System.out.println("=== 1. printString ===");
        jni.printString("Hello from Java via JNI!");

        // 2. コールバック呼び出し
        System.out.println("\n=== 2. callWithCallback ===");
        jni.callWithCallback(message -> System.out.println("[Java Callback] " + message));

        // 3. スレッド起動 / join
        System.out.println("\n=== 3. myThreadStart / myThreadJoin ===");
        jni.myThreadStart(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("[Thread] Log " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        System.out.println("[Main] Thread started. Waiting for join...");
        jni.myThreadJoin();
        System.out.println("[Main] Thread joined.");
    }
}
