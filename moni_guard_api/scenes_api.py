import json
from datetime import datetime
from aiohttp import ClientSession, StreamReader
from moni_guard_api.scene import Scene
from moni_guard_api.guest import Guest


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
        try:
            async with ClientSession(headers={'Authorization': 'Bearer ' + self._access_token}) as session:
                # print(self._get_guests_url(scene_id))
                async with session.get(f'{self._get_guests_url(scene_id)}') as response:
                    print(response.status)
                    print(response.content)
                    return [Guest(guest_id=guest['guestId'], scene_id=guest['sceneId'], name=guest['name'],
                                  created_at=guest['createdAt'], is_allowed=guest['isAllowed']) for guest in
                            await response.json()]
        except Exception as e:
            raise Exception(f'Error occurred while getting guests: {e}')




