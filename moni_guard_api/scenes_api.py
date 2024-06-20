from datetime import datetime

from aiohttp import ClientSession

from moni_guard_api.guest import Guest
from moni_guard_api.scene import Scene


def _time_transform(time: str):
    return datetime.strptime(time, "%Y-%m-%dT%H:%M:%S.%f")


class ScenesApi:
    def __init__(self, main_url: str):
        self._access_token = None
        self._main_url = main_url

    def set_access_token(self, access_token: str):
        self._access_token = access_token

    def _get_scenes_url(self):
        return f'{self._main_url}/Scenes/GetScenes'

    def _get_guests_url(self, scene_id: int):
        return f'{self._main_url}/Scenes/GetGuest/{scene_id}'

    async def get_scenes(self) -> list[Scene]:
        try:
            async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
                async with session.get(f'{self._get_scenes_url()}') as response:
                    return [Scene(scene_id=scene['sceneId'], name=scene['name'], resident_id=scene['residentId']) for
                            scene in await response.json()]
        except Exception as e:
            raise Exception(f'Error occurred while getting scenes: {e}')

    async def get_guests(self, scene_id: int) -> list[Guest]:
        # try:
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            # print(self._get_guests_url(scene_id))
            async with session.get(self._get_guests_url(scene_id)) as response:
                print(response.status)
                print(response.content)
                # return [Guest(guest_id=guest['guestId'], scene_id=guest['sceneId'], name=guest['name'],
                #               created_at=guest['createdAt'], is_allowed=guest['isAllowed']) for guest in
                #         await response.json()]
                return Guest.from_json(await response.json())
        # except Exception as e:
        #     raise Exception(f'Error occurred while getting guests: {e}')

    # /// <summary>
    # /// 请求将摄像头添加到指定场景。该 API 应当由摄像头设备调用。
    # /// </summary>
    # /// <param name="sceneId">添加摄像头的场景ID。</param>
    # /// <returns>
    # /// 摄像头可能的 Unique ID。
    # /// </returns>
    async def request_camera_creation(self, scene_id: int) -> str:
        async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
            async with session.post(f'{self._main_url}/Scenes/RequestCameraCreation/{scene_id}') as response:
                return await response.text()
