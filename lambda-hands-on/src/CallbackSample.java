// Javaでコールバックを実現する伝統的な方法 = インターフェース
interface OnComplete {
    void done(String result);
}

class OnCompleteImpl implements OnComplete {
    @Override
    public void done(String result) {
        System.out.println("コールバックが呼び出されました: " + result);
    }
}

public class CallbackSample {
    static void doSomething(OnComplete callback) {
        // 処理...
        callback.done("完了しました");
    }

    public static void main(String[] args) {
        OnComplete callback = new OnCompleteImpl();
        // コールバックを渡して処理を実行
        doSomething(callback);
        
        // 匿名クラスを使ってコールバックを直接渡すことも可能
        doSomething(new OnComplete() {
            @Override
            public void done(String result) {
                System.out.println("匿名クラスのコールバック: " + result);
            }
        });

        // Java 8以降はラムダ式も使える（OnCompleteが関数型インターフェースであるため）
        doSomething(result -> System.out.println("ラムダ式のコールバック: " + result));
    }
}

 
