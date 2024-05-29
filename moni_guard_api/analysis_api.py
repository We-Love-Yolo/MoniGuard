from aiohttp import ClientSession, StreamReader

from moni_guard_api.face import Face


class AnalysisApi:
    def __init__(self, main_url: str):
        self._access_token = None
        self._main_url = main_url

    def _get_url(self) -> str:
        return f'{self._main_url}/Analysis/GetFaces'

    def set_access_token(self, access_token: str):
        self._access_token = access_token

    async def get_faces(self, scene_id: int) -> list[Face]:
        try:
            async with ClientSession(headers={'Authorization': self._access_token}) as session:
                async with session.get(f'{self._get_url()}/{scene_id}') as response:
                    return [Face(face_id=face['faceId'], guest_id=face['guestId'], name=face['name'],
                                 captured_at=face['capturedAt'], hash=face['hash']) for face in await response.json()]
        except Exception as e:
            raise Exception(f'Error occurred while getting faces: {e}')

    async def post_face(self, scene_id: int, face: Face) -> Face:
        try:
            async with ClientSession(headers={'Authorization': self._access_token}) as session:
                async with session.post(f'{self._get_url()}/{scene_id}', json=face) as response:
                    return await response.json()
        except Exception as e:
            raise Exception(f'Error occurred while posting face: {e}')

    async def get_face_image(self, scene_id: int, face_id: int) -> StreamReader:
        try:
            async with ClientSession(headers={'Authorization': self._access_token}) as session:
                async with session.get(f'{self._get_url()}/{scene_id}/{face_id}') as response:
                    return await response.content
        except Exception as e:
            raise Exception(f'Error occurred while getting face image: {e}')

    async def post_face_image(self, scene_id: int, face_id: int, image: bytes) -> None:
        try:
            async with ClientSession(headers={'Authorization': self._access_token}) as session:
                async with session.post(f'{self._get_url()}/{scene_id}/{face_id}', data=image) as response:
                    if response.status % 100 != 2:
                        raise Exception(f'Error occurred while uploading face image: response status {response.status}')
        except Exception as e:
            raise Exception(f'Error occurred while uploading face image: {e}')
