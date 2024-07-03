import json
import os

from moni_guard_api.scenes_api import ScenesApi  # This needs to be an existing module with an appropriate method


class Service:
    def __init__(self, scene_id: int):
        self.scene_id = scene_id

    @staticmethod
    def from_json(json_str: str):
        return Service(**json.loads(json_str))

    def to_json(self) -> str:
        return json.dumps(self.__dict__, indent=4)


_service: Service | None = None


def load_service_data():
    global _service
    with open('service.json', 'r') as f:
        _service = Service.from_json(f.read())


def save_service_data():
    global _service
    with open('service.json', 'w') as f:
        f.write(_service.to_json())


async def get_service(sapi: ScenesApi):
    global _service
    if _service is not None:
        return _service
    if os.path.exists('service.json'):
        load_service_data()
        return _service
    scenes = await sapi.get_scenes()
    for scene in sorted(scenes, key=lambda x: x.scene_id):
        print("Which scene do you want to use?")
        print(f"Scene ID: {scene.scene_id}, Scene Name: {scene.name}")
    scene_id = int(input("Enter the scene ID: "))
    if scene_id not in [scene.scene_id for scene in scenes]:
        print("Invalid scene ID.")
        exit(1)
    _service = Service(scene_id)
    save_service_data()
    return _service


if __name__ == '__main__':
    import asyncio
    from moni_guard_api.scenes_api import ScenesApi  # This needs to be properly configured to work


    async def main():
        sapi = ScenesApi("https://mgapi.bitterorange.cn")  # Instance of ScenesApi
        service = await get_service(sapi)
        print(f'Service with scene ID {service.scene_id} is loaded.')


    asyncio.run(main())
    print("Success")
