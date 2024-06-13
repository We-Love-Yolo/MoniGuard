import numpy
import os
import gol
faces_data = '../faces_data'


class LoadFacesData:
    def __init__(self, scene_id: int, guest_id: int, guest_name: str, faces_encoding: numpy.ndarray):
        self.scene_id = scene_id
        self.guest_id = guest_id
        self.guest_name = guest_name
        self.faces_encoding = faces_encoding


def _local_data_load(scene_id: int):
    res = []
    faces_data_files = os.listdir(faces_data)
    # faces_data_file -> 'guestId_guestName.npy'
    for faces_data_file in faces_data_files:
        if not faces_data_file.endswith('.npy'):
            continue
        guest_id, guest_name = faces_data_file.split('_')
        faces_data_path = os.path.join(faces_data, faces_data_file)
        faces_encoding = numpy.load(faces_data_path)
        res.append(LoadFacesData(scene_id, guest_id, guest_name, faces_encoding))
    return res


def _API_data_load(scene_id: int) -> list[LoadFacesData]:
    get_url = gol.get_value('get_url')
    oauth = gol.get_value('oauth')
    url, headers, body = oauth.prepare_token_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token',
        authorization_response=get_url)
    # todo: get face data from API
    pass


def init_load(scene_id: int):
    if not os.path.exists(faces_data):
        os.mkdir(faces_data)
    else:
        faces_datas = _local_data_load(scene_id)
        faces_datas.append(_API_data_load(scene_id))
