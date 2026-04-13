class LambdaSample {
    public static void main(String[] args) {
        // ラムダ式を使用して、Runnableインターフェースのrunメソッドを実装
        Runnable runnable = () -> System.out.println("Hello, Lambda!");

        // Runnableを実行
        runnable.run();
    }
}