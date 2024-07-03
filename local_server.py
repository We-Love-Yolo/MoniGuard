import asyncio
import os.path

import cv2
import face_recognition
import numpy as np
from quart import Quart, request, jsonify

import authorization
import service
from moni_guard_api.analysis_api import AnalysisApi
from service import Service

app = Quart(__name__)
serv: Service | None = None

aapi: AnalysisApi | None = None
guests = []
face_guest_id = []
face_encodings = []


def load_face_encodings():
    if not os.path.exists('face_encodings'):
        os.mkdir('face_encodings')
        return [], []

    guest_indices = []
    encodings = []
    for file_name in os.listdir('face_encodings'):
        if file_name.endswith('.npy'):
            file_path = os.path.join('face_encodings', file_name)
            encoding = np.load(file_path)
            guest_id = int(file_name.split('.')[0])
            guest_indices.append(guest_id)
            encodings.append(encoding)
    return guest_indices, encodings


def save_encoding(guest_id: int, data: np.ndarray):
    file_path = os.path.join('face_encodings', f'{guest_id}.npy')
    np.save(file_path, data)


@app.route('/face-captured/<int:camera_id>', methods=['POST'])
async def face_captured(camera_id):
    request_files = await request.files
    if 'file' not in request_files:
        return jsonify({"error": "No file part in the request"}), 400

    file = request_files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    file_data = file.read()
    np_arr = np.frombuffer(file_data, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    face_locations = face_recognition.face_locations(img)
    current_face_encodings = face_recognition.face_encodings(img, face_locations)

    detected_faces = []
    for current_encoding in current_face_encodings:
        matches = face_recognition.compare_faces(face_encodings, current_encoding)
        if True in matches:
            first_match_index = matches.index(True)
            guest_id = face_guest_id[first_match_index]
            detected_faces.append(guest_id)
            function_A(guest_id)  # Known face
        else:
            new_guest_id = await aapi.post_guest(serv.scene_id, "Unknown Guest", False)
            face_encodings.append(current_encoding)
            face_guest_id.append(new_guest_id)
            save_encoding(new_guest_id, current_encoding)
            await aapi.post_guest_face_encoding(new_guest_id, current_encoding.tobytes())
            detected_faces.append(new_guest_id)
            function_B(new_guest_id)  # New face

    return jsonify({"detected_faces": detected_faces}), 200


def function_A(guest_id):
    print(f"Function A executed for guest_id {guest_id}")


def function_B(guest_id):
    print(f"Function B executed for new guest_id {guest_id}")


async def run():
    global aapi, face_guest_id, face_encodings, serv

    print("Welcome to the camera management system!")
    access_token = await authorization.get_access_token()
    aapi = AnalysisApi("https://mgapi.bitterorange.cn")
    aapi.set_access_token(access_token)

    sapi = service.ScenesApi("https://mgapi.bitterorange.cn")
    sapi.set_access_token(access_token)

    serv = await service.get_service(sapi)
    guests = await sapi.get_guests(serv.scene_id)

    face_guest_id, face_encodings = load_face_encodings()


if __name__ == '__main__':
    asyncio.run(run())
    app.run(port=5864)
