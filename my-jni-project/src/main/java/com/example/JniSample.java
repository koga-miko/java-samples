package com.example;

public class JniSample {

    static {
        System.loadLibrary("jnisample");
    }

    /** C++側でstrを標準出力に表示する */
    public native void printString(String str);

    /** C++側からcallbackのonLog()を呼び出す */
    public native void callWithCallback(LogCallback callback);

    /** JNI内でpthreadを生成しtaskを実行する */
    public native void myThreadStart(Runnable task);

    /** 起動したスレッドをjoinして終了を待つ */
    public native void myThreadJoin();
}
