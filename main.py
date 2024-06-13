import asyncio
import os
import urllib.request
import numpy as np

import aiohttp
import cv2
import face_recognition
from oauthlib.oauth2 import WebApplicationClient

MGAPI_API_CLIENT_ID = '6e7fcbc1-b51f-4111-ad44-2cf0baee8597'
MGAPI_API_CLIENT_SECRET = 'uy68Q~NPPZXDeBDfNlQBUDcB8MREXpXfpjcxJbCk'
MGAPI_API_SCOPE = ['api://6e7fcbc1-b51f-4111-ad44-2cf0baee8597/MoniGuard.Read']
MGAPI_API_REDIRECT_URL = 'https://localhost/callback'

CAMERA_RTSP_URL = 'rtsp://admin:WUsan53408@192.168.239.109'


# The program we will be finding faces on the example below
# 更改工作目录
# os.chdir('/mnt/c/Users/ab123/Pictures/DeepFace')


def process_image(image, known_encodings):
    # Detect face locations
    face_locations = face_recognition.face_locations(image)
    face_encodings = face_recognition.face_encodings(image, face_locations)

    face_indices = []
    for face_encoding in face_encodings:
        # See if the face is a match for the known face(s)
        matches = face_recognition.compare_faces(known_encodings, face_encoding)
        # name = "Unknown"

        # # If a match was found in known_face_encodings, just use the first one.
        # if True in matches:
        #     first_match_index = matches.index(True)
        #     name = known_face_names[first_match_index]

        # Or instead, use the known face with the smallest distance to the new face
        # best_match_index = -1
        # for i, match in enumerate(matches):
        #     if match and best_match_index == -1:
        #         best_match_index = i
        #         break
        face_distances = face_recognition.face_distance(known_encodings, face_encoding)
        best_match_index = np.argmin(face_distances)
        res = -1
        if matches[best_match_index]:
            res = best_match_index

        face_indices.append(res)

    return face_locations, face_indices
    # Display the results
    # for (top, right, bottom, left), name in zip(face_locations, face_indices):
    #     # Scale back up face locations since the frame we detected in was scaled to 1/4 size
    #     # top *= 4
    #     # right *= 4
    #     # bottom *= 4
    #     # left *= 4
    #
    #     # Draw a box around the face
    #     cv2.rectangle(image, (left, top), (right, bottom), (0, 0, 255), 2)
    #
    #     # Draw a label with a name below the face
    #     cv2.rectangle(image, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
    #     font = cv2.FONT_HERSHEY_DUPLEX
    #     cv2.putText(image, str(name), (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)
    #
    # # Display the resulting image
    # cv2.imshow('Video', image)
    # if len(face_locations) > 0:
    #     cv2.imwrite('res.jpg', image)
    #
    # # Hit 'q' on the keyboard to quit!
    # if cv2.waitKey(1) & 0xFF == ord('q'):
    #     exit()


def process_video(known_encodings, process_function):
    cap = cv2.VideoCapture(
        CAMERA_RTSP_URL,
        apiPreference=cv2.CAP_FFMPEG,
        params=[cv2.CAP_PROP_OPEN_TIMEOUT_MSEC, 5000, cv2.CAP_PROP_READ_TIMEOUT_MSEC, 5000],
    )

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Camera is disconnected!")
            cv2.destroyAllWindows()
            cap.release()
            cap = cv2.VideoCapture(
                CAMERA_RTSP_URL,
                apiPreference=cv2.CAP_FFMPEG,
                params=[cv2.CAP_PROP_OPEN_TIMEOUT_MSEC, 5000, cv2.CAP_PROP_READ_TIMEOUT_MSEC, 5000],
            )
            continue

        # # Resize frame of video to 1/4 size for faster face recognition processing
        small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
        #
        # # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
        # rgb_small_frame = small_frame[:, :, ::-1]
        rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)

        print(process_function(rgb_small_frame, known_encodings))
    return True


def main():
    photo = face_recognition.load_image_file('test/1.jpg')
    known_encodings = face_recognition.face_encodings(photo)
    process_video(known_encodings, process_image)
    exit(0)
    # oauth = WebApplicationClient(MGAPI_API_CLIENT_ID)
    # url, headers, body = oauth.prepare_authorization_request(
    #     'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/authorize',
    #
    #     redirect_url=MGAPI_API_REDIRECT_URL,
    #     scope=MGAPI_API_SCOPE)
    # print(url, headers, body)
    #
    # # # 解析URL
    # # parsed_url = urlparse(get_url)
    # #
    # # # 获取查询参数
    # # query_params = parse_qs(parsed_url.query)
    # #
    # # # 获取特定参数的值
    # # code = query_params.get('code', [''])[0]
    # # state = query_params.get('state', [''])[0]
    # #
    # # print("Code:", code)
    # # print("State:", state)
    #
    # url, headers, body = oauth.prepare_token_request(
    #     'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token',
    #     authorization_response=get_url)
    #
    # req = urllib.request.Request(url, body.encode(), headers=headers)
    # with urllib.request.urlopen(req) as res:
    #     oauth.parse_request_body_response(res.read())
    #
    # print(oauth.access_token)
    #
    # url = 'https://mgapi.bitterorange.cn/Residents/GetResident'
    # url, headers, body = oauth.add_token(url)
    # async with aiohttp.ClientSession() as session:
    #     async with session.get(url, headers=headers) as response:
    #         print(await response.json())


if __name__ == '__main__':
    main()
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
