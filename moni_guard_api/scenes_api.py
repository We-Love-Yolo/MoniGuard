import json




class ScenesApi:
    def __init__(self, main_url: str):
        self._access_token = None
        self._main_url = main_url

    def set_access_token(self, access_token: str):
        self._access_token = access_token

    async def
