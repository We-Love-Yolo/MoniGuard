import os
import face_recognition
import cv2
import numpy as np
from moni_guard_api.loadFacesData import LoadFacesData

# faces_data = [LoadFacesData]


faces = '../faces'
frames = '../frames'
_default_name = "Unknown"


def get_known_face_encodings(faces_data: list[LoadFacesData]) -> list:
    _encoding_res = []
    for tmp in faces_data:
        _encoding_res.append(tmp.faces_encoding)
    return _encoding_res


def start_analyze(faces_datas: list[LoadFacesData]):
    faces_img = sorted(os.listdir(faces))
    # print(faces_img)
    for face_img in faces_img:
        _name = _default_name
        _image = cv2.imread(os.path.join(faces, face_img))
        _image_rgb = cv2.cvtColor(_image, cv2.COLOR_BGR2RGB)
        _face_encoding = face_recognition.face_encodings(_image_rgb)[0]
        known_face_encodings = get_known_face_encodings(faces_datas)
        matches = face_recognition.compare_faces(known_face_encodings, _face_encoding)
        _face_distances = face_recognition.face_distance(known_face_encodings, _face_encoding)
        best_match_index = np.argmin(_face_distances)
        if matches[best_match_index]:
            # means have match the face load
            _id = faces_datas[best_match_index].guest_id
            # todo: updateMessage, PostFace(可选), putFrame, postGuestToPhoto


    pass
