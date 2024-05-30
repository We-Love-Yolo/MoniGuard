import asyncio
import os

import face_recognition

import myLogin
import gol


# The program we will be finding faces on the example below
# 更改工作目录
isRemote = False

localPath = 'F:/RJGC/DeepFace'
remotePath = '/mnt/c/Users/ab123/Pictures/DeepFace'
osPath = ''
if isRemote:
    osPath = remotePath
else:
    osPath = localPath

os.chdir(osPath)



if __name__ == '__main__':
    gol._init()
    asyncio.run(myLogin.main())
    exit()
#
# def work_with_captured_video(camera):
#     while True:
#         print('reading frame from camera')
#         ret, frame = camera.read()
#         if not ret:
#             print("Camera is disconnected!")
#             cv2.destroyAllWindows()
#             return False
#         else:
#             cv2.imshow('frame', frame)
#
#         if cv2.waitKey(1) & 0xFF == ord('q'):
#             break
#     return True
#
#
# RTSP_STRING = 'rtsp://admin:WUsan53408@192.168.239.109'
# cap = cv2.VideoCapture(
#     RTSP_STRING,
#     apiPreference=cv2.CAP_FFMPEG,  # was previously cv2.CAP_ANY
#     params=[cv2.CAP_PROP_OPEN_TIMEOUT_MSEC, 3000],  # 1 second
# )
# exit(0)
#
# while True:
#
#     if camera.isOpened():
#         print('Camera is connected')
#         # call function
#         response = work_with_captured_video(camera)
#         if not response:
#             continue
#     else:
#         print('Camera not connected')
#         continue

photo_file_names = os.listdir()
for photo_file_name in photo_file_names:
    photo = face_recognition.load_image_file(photo_file_name)
    encodings = face_recognition.face_encodings(photo)[0]
    print(photo_file_name + ":", encodings)
    face_recognition.compare_faces(photo, encodings)
