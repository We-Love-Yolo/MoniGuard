cmake_minimum_required(VERSION 3.21)
project(MoniGuardFaceCapturer)

set(CMAKE_CXX_STANDARD 17)

find_package(CUDA REQUIRED)
find_package(OpenCV REQUIRED)

include_directories(${OpenCV_INCLUDE_DIRS} ${CUDA_INCLUDE_DIRS})

add_executable(MoniGuardFaceCapturer main.cpp)

target_link_libraries(MoniGuardFaceCapturer ${OpenCV_LIBS} ${CUDA_LIBRARIES})

set(SOURCE_ASSETS_FOLDER "${CMAKE_SOURCE_DIR}/assets")
set(DESTINATION_ASSETS_FOLDER "${CMAKE_BINARY_DIR}")
file(COPY ${SOURCE_ASSETS_FOLDER} DESTINATION ${DESTINATION_ASSETS_FOLDER})
