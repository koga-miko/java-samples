# my-jni-project

JavaとC++（JNI）を組み合わせたサンプルプロジェクトです。  
Mac / Linux / Windows (x64) で動作します。

---

## プロジェクト構成

```
my-jni-project/
├── pom.xml                          # Maven設定
├── src/
│   └── main/
│       ├── java/com/example/
│       │   ├── JniSample.java       # JNIネイティブメソッド宣言
│       │   ├── LogCallback.java     # コールバックインターフェース
│       │   └── Main.java            # デモ用メインクラス
│       └── native/
│           ├── CMakeLists.txt       # C++ビルド設定
│           └── jni_sample.cpp       # JNI実装（4関数）
└── README.md
```

---

## JNI実装の概要

| 関数 | 説明 |
|------|------|
| `printString(String str)` | C++側でstrを標準出力に表示する |
| `callWithCallback(LogCallback cb)` | JNI内からJavaのコールバック（onLog）を呼び出す |
| `myThreadStart(Runnable task)` | pthread/スレッドを生成してJava Runnableを実行する |
| `myThreadJoin()` | 起動したスレッドをjoinして終了を待つ |

---

## 前提条件

| ツール | バージョン目安 |
|--------|--------------|
| JDK | 11 以上 |
| Maven | 3.6 以上 |
| CMake | 3.10 以上 |
| C++コンパイラ | Mac: Xcode CLT / Linux: GCC / Windows: Visual Studio 2019+ または MinGW |

---

## ビルド・実行手順

### 共通：JAVA_HOMEの確認

```sh
java -version
echo $JAVA_HOME   # Mac/Linux
echo %JAVA_HOME%  # Windows
```

`JAVA_HOME` が設定されていない場合、CMakeがJNIヘッダを見つけられません。  
設定方法は各OSのセクションを参照してください。

---

### Mac

#### 1. 必要ツールのインストール

```sh
# Xcode コマンドラインツール
xcode-select --install

# Homebrew 経由で CMake・Maven をインストール
brew install cmake maven
```

#### 2. JAVA_HOMEの設定

```sh
# インストール済みのJDKを確認
/usr/libexec/java_home -V

# 例: JDK 17 を使う場合
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# シェル起動時に自動設定したい場合は ~/.zshrc に追記
echo 'export JAVA_HOME=$(/usr/libexec/java_home)' >> ~/.zshrc
source ~/.zshrc
```

#### 3. Javaコンパイル

```sh
cd my-jni-project
mvn compile
```

#### 4. C++ライブラリのビルド

```sh
mkdir -p build
cd build
cmake ../src/main/native
cmake --build .
cd ..
```

ビルド成功後、`build/libjnisample.dylib` が生成されます。

#### 5. 実行

```sh
java -Djava.library.path=build -cp target/classes com.example.Main
```

---

### Linux

#### 1. 必要ツールのインストール（Ubuntu/Debian系）

```sh
sudo apt update
sudo apt install -y openjdk-17-jdk cmake build-essential maven
```

#### 2. JAVA_HOMEの設定

```sh
# インストール先を確認
update-alternatives --list java

# 例
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# ~/.bashrc に追記して永続化
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

#### 3. Javaコンパイル

```sh
cd my-jni-project
mvn compile
```

#### 4. C++ライブラリのビルド

```sh
mkdir -p build
cd build
cmake ../src/main/native
cmake --build .
cd ..
```

ビルド成功後、`build/libjnisample.so` が生成されます。

#### 5. 実行

```sh
java -Djava.library.path=build -cp target/classes com.example.Main
```

---

### Windows (x64)

#### 1. 必要ツールのインストール

- **JDK 17+**: [Adoptium](https://adoptium.net/) などからインストール
- **Visual Studio 2019/2022**: 「C++によるデスクトップ開発」ワークロードを選択
- **CMake**: [cmake.org](https://cmake.org/download/) からインストール（PATHに追加）
- **Maven**: [maven.apache.org](https://maven.apache.org/download.cgi) からインストール（PATHに追加）

#### 2. JAVA_HOMEの設定（コマンドプロンプト）

```bat
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot
```

永続化するには「システム環境変数」に追加してください。

#### 3. Javaコンパイル

```bat
cd my-jni-project
mvn compile
```

#### 4. C++ライブラリのビルド（Visual Studio Developer Command Prompt）

```bat
mkdir build
cd build
cmake -G "Visual Studio 17 2022" -A x64 ..\src\main\native
cmake --build . --config Release
cd ..
```

ビルド成功後、`build\Release\jnisample.dll` が生成されます。

#### 5. 実行

```bat
java -Djava.library.path=build\Release -cp target\classes com.example.Main
```

---

## 期待される出力

```
=== 1. printString ===
[C++] Hello from Java via JNI!

=== 2. callWithCallback ===
[Java Callback] [C++ JNI] callback invoked from native code

=== 3. myThreadStart / myThreadJoin ===
[Main] Thread started. Waiting for join...
[Thread] Log 1
[Thread] Log 2
[Thread] Log 3
[Main] Thread joined.
```

> `[Thread] Log 1〜3` は1秒間隔で出力されます（合計約3秒かかります）。

### JDK 17+ のネイティブアクセス警告を抑制する場合

JDK 17以降では `System.loadLibrary` に関する警告が出ることがあります。  
`--enable-native-access=ALL-UNNAMED` を追加すると抑制できます。

```sh
java --enable-native-access=ALL-UNNAMED \
     -Djava.library.path=build \
     -cp target/classes com.example.Main
```

---

## Maven なしでビルドする場合（javac 直接利用）

MavenがインストールされていなくてもJDKだけで手順3のJavaコンパイルを代替できます。

```sh
# Mac / Linux
mkdir -p target/classes
javac -d target/classes \
  src/main/java/com/example/LogCallback.java \
  src/main/java/com/example/JniSample.java \
  src/main/java/com/example/Main.java
```

```bat
:: Windows
mkdir target\classes
javac -d target\classes ^
  src\main\java\com\example\LogCallback.java ^
  src\main\java\com\example\JniSample.java ^
  src\main\java\com\example\Main.java
```

---

## トラブルシューティング

### `JAVA_HOME` が見つからない

CMakeが `find_package(JNI REQUIRED)` に失敗した場合は `JAVA_HOME` を明示的に渡してください。

```sh
cmake -DJAVA_HOME=/path/to/jdk ../src/main/native
```

### `UnsatisfiedLinkError` が発生する

`-Djava.library.path` に指定したパスにライブラリが存在するか確認してください。

```sh
# Mac/Linux
ls build/libjnisample.*

# Windows
dir build\Release\jnisample.dll
```

### macOSでライブラリが読み込めない（セキュリティ警告）

```sh
xattr -d com.apple.quarantine build/libjnisample.dylib
```
