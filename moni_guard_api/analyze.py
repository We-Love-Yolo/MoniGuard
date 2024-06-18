import os
import time

from analysis_api import AnalysisApi
import mylog
import face_recognition
import cv2
import numpy as np
import subprocess
import signal


def _get_face_encoding():
    """
    应该为私有方法
    得到本地保存的所有face的encoding
    :return:
    list[np.array], face的encoding列表,
    list[str], face对应的guest_id
    """
    faces_data_path = "../faces_data"
    encodingList = []
    nameList = []
    for face in os.listdir(faces_data_path):
        encodingList.append(np.load(os.path.join(faces_data_path, face)))
        nameList.append(int(face.split('.')[0]))
    return encodingList


def _save_new_face_encoding(face_encoding: np.array, guest_id: int) -> bytes:
    """
    应该为私有方法
    保存新的face的encoding
    :param face_encoding: np.array, face的encoding
    :param guest_id: int, face对应的guest_id
    """
    faces_data_path = "../faces_data"
    np.save(os.path.join(faces_data_path, f"{guest_id}.npy"), face_encoding)
    with open(os.path.join(faces_data_path, f"{guest_id}.npy"), "rb") as f:
        encoding_data = f.read()
    return encoding_data


class Analyze:
    def __init__(self, image_path: str, scene_id: int, camera_id: int, RTSP_URL: str, analysisApi: AnalysisApi):
        self.path = image_path
        self.scene_id = scene_id
        self.camera_id = camera_id
        self.RTSP_URL = RTSP_URL
        self.analysisApi = analysisApi
        self.status = True
        # todo: 开一个openCv的新进程，并且保存进程的pid,暂未测试
        executable_path = 'MoniGuardFaceCapturer'
        process = subprocess.Popen([executable_path, self.RTSP_URL, self.path2])
        self.pid = process.pid
        pass

    def kill(self):
        """
        Kill the analysis process
        """
        self.status = False
        # todo: test it is work
        os.kill(self.pid, signal.SIGTERM)
        pass

    def _get_frames(self) -> list[str]:
        """
        应该为私有方法
        得到目标目录下的所有frame的名字列表，按照时间顺序排序
        :return: list[str]，frame的名字列表
        """
        frames_path = f"../{self.path}/frames"
        frames = sorted(os.listdir(frames_path))
        return frames

    def _get_faces(self, frame_name: str) -> list[str]:
        """
        应该为私有方法
        得到目标faces下的所有face的名字列表
        :param frame_name: frame的名字
        :return: list[str]，face的名字列表
        """
        last_time = frame_name.split('_')[0]
        faces_path = f"../{self.path}/faces"
        faces = sorted([f for f in os.listdir(faces_path) if f.startswith(last_time)])
        return faces

    async def analysis(self):
        while self.status:
            # 可以设定一个时间间隔，比如1s，用于减少cpu占用
            # time.sleep(1)
            frames = self._get_frames()
            for frame in frames:
                frame_path = f"../{self.path}/frames/{frame}"
                with open(frame_path, "rb") as f:
                    frame_data = f.read()
                # 上传frame, 得到photo_id
                photo_id = await self.analysisApi.put_photo(self.camera_id, frame, frame_data)
                if photo_id is None:
                    # 网络错误，跳过这一帧，塞到队列的最后，虽然理论上来说，这个不应该发生
                    frames.append(frame)
                    # todo: 网络错误怎么处理
                    continue
                faces = self._get_faces(frame)
                known_face_encodings, faces_guestId = _get_face_encoding()
                for face in faces:
                    face_path = f"../{self.path}/faces/{face}"
                    # 使用cv2读取图片
                    image = cv2.imread(face_path)
                    # 转换图片格式
                    rgb_small_frame = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                    # _face_locations 理论上来说应该只有一个
                    _face_locations = face_recognition.face_locations(rgb_small_frame)
                    # _face_encodings 理论上来说也应该只有一个
                    _faces_encodings = face_recognition.face_encodings(rgb_small_frame, _face_locations)
                    if _faces_encodings:
                        _face_encoding = _faces_encodings[0]
                        matches = face_recognition.compare_faces(known_face_encodings, _face_encoding)
                        _guest_id = 0
                        _face_distances = face_recognition.face_distance(known_face_encodings, _face_encoding)
                        best_match_index = np.argmin(_face_distances)
                        if matches[best_match_index]:
                            # 找到了匹配的face
                            _guest_id = faces_guestId[best_match_index]
                            # 调用postGuestToPhoto

                        else:
                            # 没有找到匹配的face，应该新增guest
                            _guest_id = await self.analysisApi.post_guest(self.scene_id, "Unknown", False)
                            if _guest_id is None:
                                # 上传出错，理论上不发生
                                mylog.error(f"Failed to post guest. scene_id: {self.scene_id}", "Analyze")
                                continue
                            face_encoding_data = _save_new_face_encoding(_face_encoding, _guest_id)
                            # 上传新的face的encoding
                            if not await self.analysisApi.post_guest_face_encoding(_guest_id, face_encoding_data):
                                # 上传出错，理论上不发生
                                mylog.error(f"Failed to post face encoding. guest_id: {_guest_id}", "Analyze")
                                continue
                            # 上传face作为guest的头像
                            with open(face_path, "rb") as f:
                                face_data = f.read()
                            if not await self.analysisApi.post_face(_guest_id, face_data):
                                # 上传出错，理论上不发生
                                mylog.error(f"Failed to post face. guest_id: {_guest_id}", "Analyze")
                                continue
                        # 将guest与photo关联
                        if not await self.analysisApi.post_guest_to_photo(_guest_id, photo_id):
                            # 上传出错，理论上不发生
                            mylog.error(f"Failed to post guest to photo. guest_id: {_guest_id}, photo_id: {photo_id}",
                                        "Analyze")
                            continue

                    else:
                        # 与opencv的人脸检测不同，这里没有检测到人脸，应该记录到日志中
                        mylog.error(f"Can't find face in {face}", "Analyze")
                        continue
