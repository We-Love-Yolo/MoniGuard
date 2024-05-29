import asyncio
import os
import urllib.request

import aiohttp
import face_recognition
from oauthlib.oauth2 import WebApplicationClient

from moni_guard_api.moni_guard_api import MoniGuardApi

MGAPI_API_CLIENT_ID = '6e7fcbc1-b51f-4111-ad44-2cf0baee8597'
MGAPI_API_CLIENT_SECRET = 'uy68Q~NPPZXDeBDfNlQBUDcB8MREXpXfpjcxJbCk'
MGAPI_API_SCOPE = ['api://6e7fcbc1-b51f-4111-ad44-2cf0baee8597/MoniGuard.Read']
MGAPI_API_REDIRECT_URL = 'https://localhost/callback'

# The program we will be finding faces on the example below
# 更改工作目录
os.chdir('/mnt/c/Users/ab123/Pictures/DeepFace')


async def main():
    oauth = WebApplicationClient(MGAPI_API_CLIENT_ID)
    url, headers, body = oauth.prepare_authorization_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/authorize',

        redirect_url=MGAPI_API_REDIRECT_URL,
        scope=MGAPI_API_SCOPE)
    print(url, headers, body)

    # firefox_bin = "/snap/firefox/current/usr/lib/firefox/firefox"
    # firefoxdriver_bin = "/snap/firefox/current/usr/lib/firefox/geckodriver"
    #
    # options = selenium.webdriver.firefox.options.Options()
    # options.binary_location = firefox_bin
    #
    # service = Service(executable_path=firefoxdriver_bin)
    # browser = Firefox(service=service, options=options)
    # browser.get(url)
    #
    # while True:
    #     get_url = browser.current_url
    #     if get_url.startswith(MGAPI_API_REDIRECT_URL):
    #         break
    #     time.sleep(1)
    # browser.close()
    # print(get_url)
    get_url = input("Enter redirect URL: ")

    # # 解析URL
    # parsed_url = urlparse(get_url)
    #
    # # 获取查询参数
    # query_params = parse_qs(parsed_url.query)
    #
    # # 获取特定参数的值
    # code = query_params.get('code', [''])[0]
    # state = query_params.get('state', [''])[0]
    #
    # print("Code:", code)
    # print("State:", state)

    url, headers, body = oauth.prepare_token_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token',
        authorization_response=get_url)

    req = urllib.request.Request(url, body.encode(), headers=headers)
    with urllib.request.urlopen(req) as res:
        oauth.parse_request_body_response(res.read())

    print(oauth.access_token)

    url = 'https://mgapi.bitterorange.cn/Residents/GetResident'
    url, headers, body = oauth.add_token(url)
    async with aiohttp.ClientSession() as session:
        async with session.get(url, headers=headers) as response:
            print(await response.json())

    exit()
    api = MoniGuardApi('https://mgapi.bitterorange.cn')
    api.set_access_token('123')
    await api.get_analysis_api().get_faces(1)


if __name__ == '__main__':
    asyncio.run(main())
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
