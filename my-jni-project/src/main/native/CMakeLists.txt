cmake_minimum_required(VERSION 3.10)
project(jnisample CXX)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Java/JNI ヘッダを検索
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# 共有ライブラリとしてビルド
add_library(jnisample SHARED jni_sample.cpp)

target_link_libraries(jnisample ${JNI_LIBRARIES})

# プラットフォーム別設定
if(APPLE)
    # macOS: .dylib
    set_target_properties(jnisample PROPERTIES SUFFIX ".dylib")
elseif(UNIX)
    # Linux: pthread をリンク
    target_link_libraries(jnisample pthread)
elseif(WIN32)
    # Windows: DLLのエクスポート設定
    set_target_properties(jnisample PROPERTIES WINDOWS_EXPORT_ALL_SYMBOLS ON)
endif()
