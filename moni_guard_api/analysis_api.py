import datetime
from typing import Any

from aiohttp import ClientSession, StreamReader
from mylog import *
import base64

from moni_guard_api.face import Face
from moni_guard_api.photo import Photo


class AnalysisApi:
    def __init__(self, main_url: str):
        self._access_token = None
        self._main_url = main_url

    def _get_url(self) -> str:
        return f'{self._main_url}/Analysis/GetFaces'

    def set_access_token(self, access_token: str):
        self._access_token = access_token

    async def put_photo(self, camera_id: int, frame_name: str, image: bytes) -> Any | None:
        """
        Put a photo to the server
        [Warning]因为登authorization的问题，该方法还没测试
        :param camera_id: 相机id
        :param frame_name: frame 名字
        :param image: 图片的bytes
        :return: 200成功返回photoId，否则返回None
        """
        url = f'{self._main_url}/Analysis/PutPhoto{camera_id}'
        image = base64.b64encode(image).decode('utf-8')
        data = {
            'name': frame_name,
            'base64Image': image
        }
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            async with session.put(url, json=data) as response:
                if response.status != 200:
                    error(f'Failed to put photo. Status code: {response.status}', 'AnalysisApi')
                    return None
                else:
                    return await response.json()["photoId"]

    async def post_guest_to_photo(self, guest_id: int, photo_id: int) -> bool:
        """
        Post a guest to a photo
        [Warning]因为登authorization的问题，该方法还没测试
        :param guest_id: guest id
        :param photo_id: photo id
        :return: True if success, False if failed
        """
        url = f'{self._main_url}/Analysis/PostGuestToPhoto'
        data = {
            'guestId': guest_id,
            'photoId': photo_id
        }
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            async with session.post(url, json=data) as response:
                if response.status != 200:
                    error(f'Failed to connect guest to photo. Status code: {response.status}', 'AnalysisApi')
                    return False
                else:
                    return True

    async def post_guest(self, scene_id: int, name: str, is_white_listed: bool):
        """
        new a guest to the server
        [Warning]因为登authorization的问题，该方法还没测试
        :param scene_id: scene id
        :param name: guest name
        :param is_white_listed: is the guest white listed
        :return: guest_Id if success, None if failed
        """
        url = f'{self._main_url}/Analysis/PostGuest/{scene_id}'
        data = {
            'name': name,
            'isWhiteListed': is_white_listed
        }
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            async with session.post(url, json=data) as response:
                if response.status != 200:
                    error(f'Failed to new a guest. Status code: {response.status}', 'AnalysisApi')
                    return None
                else:
                    return await response.json()["guestId"]

    async def post_guest_face_encoding(self, guest_id: int, encoding_data: bytes) -> bool:
        """
        post a face encoding to the server
        [Warning]因为登authorization的问题，该方法还没测试
        :param guest_id: guest id
        :param encoding_data: face encoding data
        :return: True if success, False if failed
        """
        url = f'{self._main_url}/Analysis/PostGuestFaceEncoding/{guest_id}'
        encoding_data = base64.b64encode(encoding_data).decode('utf-8')
        data = {
            'encodingData': encoding_data
        }
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            async with session.post(url, json=data) as response:
                if response.status != 200:
                    error(f'Failed to post face encoding. Status code: {response.status}', 'AnalysisApi')
                    return False
                else:
                    return True

    async def post_face(self, guest_id: int, face_image_data: bytes):
        """
        post a face to the server
        [Warning]因为登authorization的问题，该方法还没测试
        :param guest_id: guest id
        :param face_image_data: face image data
        :return: True if success, False if failed
        """
        url = f'{self._main_url}/Analysis/PostFace/{guest_id}'
        face_image_data = base64.b64encode(face_image_data).decode('utf-8')
        data = {
            'base64FaceImage': face_image_data
        }
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            async with session.post(url, json=data) as response:
                if response.status != 200:
                    error(f'Failed to post face. Status code: {response.status}', 'AnalysisApi')
                    return False
                else:
                    return True
