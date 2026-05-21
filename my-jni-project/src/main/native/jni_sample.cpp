#include <jni.h>
#include <iostream>
#include <string>

#ifdef _WIN32
  #include <windows.h>
#else
  #include <pthread.h>
#endif

// ---------------------------------------------------------------------------
// グローバル状態（サンプル用に簡略化）
// ---------------------------------------------------------------------------
static JavaVM*  g_jvm      = nullptr;
static jobject  g_task_ref = nullptr;

#ifdef _WIN32
static HANDLE   g_thread   = nullptr;
#else
static pthread_t g_thread;
#endif

// ---------------------------------------------------------------------------
// スレッド関数: JNIにアタッチしてJava Runnableを実行する
// ---------------------------------------------------------------------------
#ifdef _WIN32
static DWORD WINAPI thread_func(LPVOID) {
#else
static void* thread_func(void*) {
#endif
    JNIEnv* env = nullptr;
    g_jvm->AttachCurrentThread(reinterpret_cast<void**>(&env), nullptr);

    jclass    cls = env->GetObjectClass(g_task_ref);
    jmethodID run = env->GetMethodID(cls, "run", "()V");
    env->CallVoidMethod(g_task_ref, run);

    g_jvm->DetachCurrentThread();
#ifdef _WIN32
    return 0;
#else
    return nullptr;
#endif
}

// ---------------------------------------------------------------------------
// JNI 実装
// ---------------------------------------------------------------------------
extern "C" {

/**
 * 1. 文字列をC++側で標準出力に表示する
 */
JNIEXPORT void JNICALL Java_com_example_JniSample_printString(
        JNIEnv* env, jobject /*obj*/, jstring str) {
    const char* chars = env->GetStringUTFChars(str, nullptr);
    std::cout << "[C++] " << chars << std::endl;
    env->ReleaseStringUTFChars(str, chars);
}

/**
 * 2. コールバック関数を受け取り、JNI内からonLog()を呼び出す
 */
JNIEXPORT void JNICALL Java_com_example_JniSample_callWithCallback(
        JNIEnv* env, jobject /*obj*/, jobject callback) {
    jclass    cls   = env->GetObjectClass(callback);
    jmethodID onLog = env->GetMethodID(cls, "onLog", "(Ljava/lang/String;)V");
    jstring   msg   = env->NewStringUTF("[C++ JNI] callback invoked from native code");
    env->CallVoidMethod(callback, onLog, msg);
    env->DeleteLocalRef(msg);
}

/**
 * 3. スレッドを生成してtaskを実行する（my_thread_start相当）
 */
JNIEXPORT void JNICALL Java_com_example_JniSample_myThreadStart(
        JNIEnv* env, jobject /*obj*/, jobject task) {
    env->GetJavaVM(&g_jvm);

    if (g_task_ref != nullptr) {
        env->DeleteGlobalRef(g_task_ref);
    }
    g_task_ref = env->NewGlobalRef(task);

#ifdef _WIN32
    g_thread = CreateThread(nullptr, 0, thread_func, nullptr, 0, nullptr);
#else
    pthread_create(&g_thread, nullptr, thread_func, nullptr);
#endif
}

/**
 * 4. 起動したスレッドをjoinして終了を待つ
 */
JNIEXPORT void JNICALL Java_com_example_JniSample_myThreadJoin(
        JNIEnv* env, jobject /*obj*/) {
#ifdef _WIN32
    if (g_thread != nullptr) {
        WaitForSingleObject(g_thread, INFINITE);
        CloseHandle(g_thread);
        g_thread = nullptr;
    }
#else
    pthread_join(g_thread, nullptr);
#endif
    if (g_task_ref != nullptr) {
        env->DeleteGlobalRef(g_task_ref);
        g_task_ref = nullptr;
    }
}

} // extern "C"
