from moni_guard_api.analysis_api import AnalysisApi
from moni_guard_api.scenes_api import ScenesApi


class MoniGuardApi:
    def __init__(self, url: str):
        self._access_token = ''
        self._url = url
        self._analysis_api = AnalysisApi(self._url)
        self._scenes_api = ScenesApi(self._url)

    def set_access_token(self, access_token: str):
        self._access_token = access_token
        self._analysis_api.set_access_token(access_token)

    def get_analysis_api(self) -> AnalysisApi:
        return self._analysis_api

    def get_scenes_api(self) -> ScenesApi:
        return self._scenes_api
