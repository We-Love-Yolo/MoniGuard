# import os
# import authorization
# from moni_guard_api.analysis_api import AnalysisApi
# def showMenu():
#     print("Menu:")
#     print("1. Add Camera")
#     print("2. List Cameras")
#     print("3. Remove Cameras")
#     print("5. Exit")
#     num = input("Enter your choice: ")
#     if isinstance(num, int):
#         return num
#     else:
#         return -1
#
#
# if __name__ == "__main__":
#     camera_list = []
#     print("Welcome to the camera management system!")
#     print("Is time to log in")
#     access_token = authorization.get_access_token()
#     aapi = AnalysisApi("https://mgapi.bitterorange.cn/")
#     aapi.set_access_token(access_token)
#     os.system('clear')
#     while True:
#         choice = showMenu()
#         if choice == 1:
#             os.system('clear')
#
#         elif choice == 5:
#             break
#         else:
#             print("Invalid choice")
#             input("Press Enter to continue...")
#             os.system('clear')
#     print("Goodbye!")
import numpy as np
import os

from moni_guard_api import analysis_api, analyze
import authorization


async def test():
    # aapi = analysis_api.AnalysisApi("https://mgapi.bitterorange.cn")

    accessToken = await authorization.get_access_token()
    aapi = analysis_api.AnalysisApi("http://192.168.239.101")
    aapi.set_access_token(accessToken)
    # with open("head.jpg", "rb") as f:
    #     image = f.read()
    #
    # # guestId = await aapi.post_guest(10,"testName",False)
    # # print(guestId)
    # guestId = 6
    # # with open("my_face_encoding_data.npy", "rb") as f:
    # #     encodingData = f.read()
    # # print(await aapi.post_guest_face_encoding(guestId, encodingData))
    # # print(await aapi.post_face(guestId, image))
    # print(await aapi.post_guest_to_photo(guestId, 5))
    camera_index = 1
    analyze1 = analyze.Analyze(f"camera{camera_index}_home", 10, 4, "rtsp://admin:WUsan53408@192.168.239.109", aapi)
    await analyze1.analysis()
    print('have start')
    input("Press Enter to kill...")
    analyze1.kill()


if __name__ == "__main__":
    import asyncio
    asyncio.run(test())

