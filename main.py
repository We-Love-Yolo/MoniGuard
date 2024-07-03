import asyncio
import os
import threading

import authorization
import local_server
import service
from moni_guard_api import analyze
from moni_guard_api.analysis_api import AnalysisApi


def showMenu():
    print("Menu:")
    print("1. Add Camera")
    print("2. List Cameras")
    print("3. Remove Cameras")
    print("4. Exit")
    return int(input("Enter your choice: "))


def run_analyze(analyze11: analyze.Analyze):
    # print('start')
    asyncio.run(analyze11.analysis())


def list_cameras(camera_list: list[analyze.Analyze]):
    print("----------------------------------------------------")
    print("Camera list:")
    for i in range(len(camera_list)):
        print(f"[running]{i} : {camera_list[i].RTSP_URL}")
    print("----------------------------------------------------")


if __name__ == "__main__":

    camera_list = []
    camera_thread_list = []

    # face_encodings = []

    print("Welcome to the camera management system!")
    print("Is time to log in")
    access_token = asyncio.run(authorization.get_access_token())
    aapi = AnalysisApi("https://mgapi.bitterorange.cn")
    aapi.set_access_token(access_token)

    sapi = service.ScenesApi("https://mgapi.bitterorange.cn")
    sapi.set_access_token(access_token)

    serv = asyncio.run(service.get_service(sapi))

    print("Updating face encodings...")
    face_encodings = asyncio.run(sapi.get_guests(serv.scene_id))


    local_server.run()
    # os.system('clear')
    exit()
    while True:
        choice = showMenu()
        if choice == 1:
            # os.system('clear')
            analyze1 = analyze.Analyze(f"camera{len(camera_list) + 1}_home", 10, 4,
                                       "rtsp://admin:WUsan53408@192.168.239.109",
                                       aapi)
            camera_list.append(analyze1)
            thread = threading.Thread(target=run_analyze, args=(analyze1,))
            thread.start()
            camera_thread_list.append(thread)
            print("Camera added successfully!")
        elif choice == 2:
            # os.system('clear')
            list_cameras(camera_list)
        elif choice == 3:
            while True:
                num = input("Enter the camera number to remove: ")
                num = int(num)
                if num < len(camera_list):
                    camera_list[num].kill()
                    camera_list.pop(num)
                    camera_thread_list[num].join()
                    camera_thread_list.pop(num)
                    print("Camera removed successfully!")
                    break
                else:
                    print("Invalid camera number")

        elif choice == 4:
            break
        else:
            print("Invalid choice")
            input("Press Enter to continue...")
            os.system('clear')
    print("Goodbye!")
# import numpy as np
# import os

# from moni_guard_api import analysis_api, analyze
# import authorization
#
#
# async def test():
#     # aapi = analysis_api.AnalysisApi("https://mgapi.bitterorange.cn")
#
#     accessToken = await authorization.get_access_token()
#     aapi = analysis_api.AnalysisApi("http://192.168.239.101")
#     aapi.set_access_token(accessToken)
#     # with open("head.jpg", "rb") as f:
#     #     image = f.read()
#     #
#     # # guestId = await aapi.post_guest(10,"testName",False)
#     # # print(guestId)
#     # guestId = 6
#     # # with open("my_face_encoding_data.npy", "rb") as f:
#     # #     encodingData = f.read()
#     # # print(await aapi.post_guest_face_encoding(guestId, encodingData))
#     # # print(await aapi.post_face(guestId, image))
#     # print(await aapi.post_guest_to_photo(guestId, 5))
#     camera_index = 1
#     analyze1 = analyze.Analyze(f"camera{camera_index}_home", 10, 4, "rtsp://admin:WUsan53408@192.168.239.109", aapi)
#     await analyze1.analysis()
#     print('have start')
#     input("Press Enter to kill...")
#     analyze1.kill()
#
#
# if __name__ == "__main__":
#     import asyncio
#     asyncio.run(test())
