import hashlib
import json
import logging
import os
import threading
import typing

from moni_guard_api.face import Face
from moni_guard_api.moni_guard_api import MoniGuardApi


class FaceManager:
    def __init__(self, server_url, scene_id: int):
        self._server_url = server_url
        self._scene_id = scene_id
        self._faces: dict[int, set[Face]] = {}
        self._faces_lock = threading.Lock()
        self._face_path = f'faces/{scene_id}'
        self._api = MoniGuardApi()
        if not os.path.exists(self._face_path):
            os.makedirs(self._face_path)

    def _load_faces_data(self) -> None:
        try:
            with open('faces/faces.json', 'r') as f:
                self._faces_lock.acquire()
                self._faces = json.loads(f.read())
                self._faces_lock.release()
        except Exception as e:
            raise Exception(f'Error occurred while loading faces data: {e}')

    def _save_faces_data(self) -> None:
        self._faces_lock.acquire()
        serialized_data = json.dumps(self._faces)
        self._faces_lock.release()
        try:
            with open('faces/faces.json', 'w') as f:
                f.write(serialized_data)
        except Exception as e:
            raise Exception(f'Error occurred while saving faces data: {e}')

    async def _update_faces_data_from_server(self) -> None:
        face_list = await self._api.get_analysis_api().get_faces(self._scene_id)
        for face in face_list:
            self._faces_lock.acquire()
            if self._faces.get(face.guest_id) is None:
                self._faces[face.guest_id] = set()
            self._faces[face.guest_id].add(face)
            self._faces_lock.release()

    async def _get_face_images_from_server(self) -> None:
        face_list = []
        self._faces_lock.acquire()
        for faces in self._faces.values():
            for face in faces:
                face_list.append(face)
        self._faces_lock.release()
        for face in face_list:
            face_image_file_name = f'{self._face_path}/{face.guest_id}/{face.hash}'
            if (os.path.exists(face_image_file_name)
                    and os.path.isfile(face_image_file_name)):
                hash = hashlib.sha1()
                with open(face_image_file_name, 'rb') as f:
                    while f.readable():
                        buffer = f.read(4096)
                        hash.update(buffer)
                if hash.digest() == face.hash:
                    logging.debug(f'Found face image file: {face_image_file_name}')
                    continue
            logging.info(f'Fetching face image file: {face_image_file_name}')
            stream_reader = await self._api.get_analysis_api().get_face_image(self._scene_id, face.face_id)
            hash = hashlib.sha1()
            with open(face_image_file_name, 'wb') as f:
                while stream_reader.readable():
                    buffer = await stream_reader.read(4096)
                    hash.update(buffer)
                    f.write(buffer)
            if hash.digest() != face.hash:
                logging.error(f'Hash mismatch for face image file: {face_image_file_name}')
                os.remove(face_image_file_name)
                continue
            logging.info(f'Face image file saved: {face_image_file_name}')

    def add_face(self, face: Face, save_function: typing.Callable[[str], bool]) -> None:
        if any(f.hash == face.hash for f in _faces[self._faces.guest_id]):
            return
        face_image_file_name = f'{self._face_path}/{face.guest_id}/{face.hash}'
        if not save_function(face_image_file_name):
            return
        self._faces_lock.acquire()
        if self._faces.get(face.guest_id) is None:
            self._faces[face.guest_id] = set()
        self._faces[face.guest_id].add(face)
        self._faces_lock.release()

    def get_faces_by_guest(self, guest_id: int) -> set[Face]:
        self._faces_lock.acquire()
        if self._faces.get(guest_id) is None:
            return set()
        result = self._faces[guest_id]
        self._faces_lock.release()
        return result

    def get_face_file_list(self) -> list[str]:
        result: list[str] = []
        self._faces_lock.acquire()
        for faces in self._faces.values():
            for face in faces:
                result.append(f'faces/{scene_id}/{face.name}')
        self._faces_lock.release()
        return result

    async def update_from_server(self):
        await self._update_faces_data_from_server()
        await self._get_face_images_from_server()
