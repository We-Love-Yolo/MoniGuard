// Uncomment the following line to use the CUDA version of the Haar cascade classifier
//#define USE_CUDA

#define ANALYZE_SCALE_1_N 2
#define ANALYZE_SCALE (1.0 / ANALYZE_SCALE_1_N)
#define RTSP_URL "rtsp://admin:WUsan53408@192.168.239.109"
#define CAMERA_ID 1

#include <iostream>
#include <opencv2/opencv.hpp>
#include <ctime>
#include <filesystem>

namespace fs = std::filesystem;
#ifdef USE_CUDA
#include <opencv2/cudacodec.hpp>
#include <opencv2/cudaobjdetect.hpp>
#endif

#define CV_ORANGE cv::Scalar(0, 165, 255)

int main()
{
#ifdef USE_CUDA
    auto haar_cascade_path = "assets/haarcascades_cuda/haarcascade_frontalface_alt.xml";
    auto face_cascade = cv::cuda::CascadeClassifier::create(haar_cascade_path);
#else
    auto haar_cascade_path = "assets/haarcascades/haarcascade_frontalface_alt.xml";
    auto face_cascade = cv::CascadeClassifier(haar_cascade_path);
#endif
    fs::path save_faces_path = "faces";
    fs::path save_frames_path = "frames";
    if (!fs::exists(save_faces_path))
    {
        fs::create_directory(save_faces_path);
    }
    if (!fs::exists(save_frames_path))
    {
        fs::create_directory(save_frames_path);
    }

    if (face_cascade.empty())
    {
        std::cerr << "Error: Cannot load the cascade classifier." << std::endl;
        return -1;
    }

    auto cap = cv::VideoCapture(RTSP_URL);

    if (!cap.isOpened())
    {
        std::cerr << "Error: Cannot open the camera." << std::endl;
        return -1;
    }

    auto frameCount = 0;
    auto lastFacesCount = 0;
    auto lastTime = time(nullptr);
    while (true)
    {
        cv::Mat frame, gray;
        cap >> frame;
        if (frame.empty())
        {
            std::cerr << "Error: Cannot grab the frame." << std::endl;
            break;
        }

        // Download the detected faces
        std::vector<cv::Rect> faces_rect;

        if (frameCount % 2 == 0)
        {
            // Convert to grayscale
            cv::cvtColor(frame, gray, cv::COLOR_BGR2GRAY);

            // Resize the frame to half
            cv::resize(gray, gray, cv::Size(), ANALYZE_SCALE, ANALYZE_SCALE);

#ifdef USE_CUDA
            // Upload the frame to the GPU
            cv::cuda::GpuMat gpu_gray;
            gpu_gray.upload(gray);

            // Detect faces
            cv::cuda::GpuMat gpu_faces;
            face_cascade->detectMultiScale(gpu_gray, gpu_faces);

            if (!gpu_faces.empty())
            {
                face_cascade->convert(gpu_faces, faces_rect);
            }
#else
            // Detect faces
            face_cascade.detectMultiScale(gray, faces_rect);
#endif
        }

        bool isNewFaceDetected = false;
//        std::cout << lastFacesCount << "\t" << faces_rect.size() << "\t";

        if (time(nullptr) - lastTime < 10 || lastFacesCount == faces_rect.size())
        {
            isNewFaceDetected = false;
//            continue;
        }
        else
        {
            isNewFaceDetected = true;
            lastFacesCount = faces_rect.size();
        }
//        std::cout << isNewFaceDetected << std::endl;

        // Draw rectangles around detected faces
        for (int i = 0; i < faces_rect.size(); i++)
        {
            auto rect = faces_rect[i];
            // Remake the rect to the original size
            rect.x *= ANALYZE_SCALE_1_N;
            rect.y *= ANALYZE_SCALE_1_N;
            rect.width *= ANALYZE_SCALE_1_N;
            rect.height *= ANALYZE_SCALE_1_N;
            // cut face
            if (isNewFaceDetected)
            {
                lastTime = time(nullptr);
                auto face = frame(rect);
                auto face_name = save_faces_path /
                                 ("camera_" + std::to_string(CAMERA_ID) + "_" + std::to_string(lastTime) + "_face_" +
                                  std::to_string(i) + ".jpg");
                cv::imwrite(face_name, face);

            }


            cv::rectangle(frame, rect, CV_ORANGE, 2);

            // Add name tag
            cv::putText(frame, "Person", cv::Point(rect.x, rect.y - 10), cv::FONT_HERSHEY_SIMPLEX, 1.0, CV_ORANGE, 2);
//            std::cout<<"last face count: "<<lastFacesCount<<"\t";
//            std::cout<<"Detected face: "<<faces_rect.size()<<std::endl;

            // save frame
            if (isNewFaceDetected)
            {
                auto frame_name = save_frames_path /
                                  ("camera_" + std::to_string(CAMERA_ID) + "_" + std::to_string(lastTime) +
                                   "_frame.jpg");
                cv::imwrite(frame_name, frame);
            }


        }

        // Display the frame
        cv::imshow("Frame", frame);
        if (cv::waitKey(1) == 27)
        {
            break;
        }
    }
    return 0;
}