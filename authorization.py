import asyncio
import json
import os

import aiohttp
from oauthlib.oauth2 import WebApplicationClient


# MGAPI_API_CLIENT_ID = '6e7fcbc1-b51f-4111-ad44-2cf0baee8597'
# MGAPI_API_CLIENT_SECRET = 'uy68Q~NPPZXDeBDfNlQBUDcB8MREXpXfpjcxJbCk'
# MGAPI_API_SCOPE = ['api://6e7fcbc1-b51f-4111-ad44-2cf0baee8597/MoniGuard.Read']
# MGAPI_API_REDIRECT_URL = 'https://localhost/callback'


class OAuthParams:
    def __init__(self, client_id: str, client_secret: str, scope: list[str], redirect_url: str,
                 authorized_redirect_url: str = None, access_token: str = None):
        self.client_id = client_id
        self.client_secret = client_secret
        self.scope = scope
        self.redirect_url = redirect_url
        self.authorized_redirect_url = authorized_redirect_url
        self.access_token = access_token

    @staticmethod
    def from_json(json_str: str):
        return OAuthParams(**json.loads(json_str))

    def to_json(self) -> str:
        return json.dumps(self.__dict__, indent=4)


oauth_params = OAuthParams(
    client_id='6e7fcbc1-b51f-4111-ad44-2cf0baee8597',
    client_secret='uy68Q~NPPZXDeBDfNlQBUDcB8MREXpXfpjcxJbCk',
    scope=['api://6e7fcbc1-b51f-4111-ad44-2cf0baee8597/MoniGuard.Read'],
    redirect_url='https://localhost/callback'
)

oauth: WebApplicationClient


def load_login_info():
    global oauth_params
    with open('oauth.json', 'r') as f:
        oauth_params = OAuthParams.from_json(f.read())


def save_login_info():
    global oauth_params
    with open('oauth.json', 'w') as f:
        f.write(oauth_params.to_json())


def authorize_for_authorization_url() -> str | None:
    global oauth
    global oauth_params
    url, _, _ = oauth.prepare_authorization_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/authorize',
        redirect_url=oauth_params.redirect_url,
        scope=oauth_params.scope)
    print("Please open the following URL in your browser and authorize the application:")
    print(url)
    oauth_params.authorized_redirect_url = input("Enter redirect URL: ")
    save_login_info()
    return url


async def acquire_access_token() -> str | None:
    global oauth
    global oauth_params
    url, headers, body = oauth.prepare_token_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token',
        authorization_response=oauth_params.authorized_redirect_url)
    async with aiohttp.ClientSession() as session:
        async with session.post(url, headers=headers, data=body) as response:
            if response.status / 100 != 2:
                return None
            oauth.parse_request_body_response(await response.text())
    oauth_params.access_token = oauth.access_token
    save_login_info()
    return oauth_params.access_token


async def refresh_access_token() -> str | None:
    global oauth
    global oauth_params
    url, headers, body = oauth.prepare_refresh_token_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token',
        client_id=oauth_params.client_id,
        client_secret=oauth_params.client_secret)
    async with aiohttp.ClientSession() as session:
        async with session.post(url, headers=headers, data=body) as response:
            if response.status / 100 != 2:
                return None
            oauth.parse_request_body_response(await response.text())
    oauth_params.access_token = oauth.access_token
    save_login_info()
    return oauth_params.access_token


async def test_access_token() -> bool:
    global oauth_params
    url = 'https://mgapi.bitterorange.cn/WeatherForecast'
    headers = {
        'Authorization': f'Bearer {oauth_params.access_token}'
    }
    async with aiohttp.ClientSession() as session:
        async with session.get(url, headers=headers) as response:
            return response.status / 100 == 2


async def main():
    global oauth
    global oauth_params

    if not os.path.exists('oauth.json'):
        save_login_info()
    load_login_info()
    oauth = WebApplicationClient(oauth_params.client_id)

    if oauth_params.access_token is not None:
        if await test_access_token():
            print('Access token is valid.')
            return 0

    if oauth_params.authorized_redirect_url is not None:
        if await refresh_access_token() is not None:
            print('Access token refreshed.')
            if await test_access_token():
                print('Access token is valid.')
                return 0

    if authorize_for_authorization_url() is not None:
        print('Authorization successful.')
        print('Now acquiring access token...')
        if await acquire_access_token() is not None:
            print('Access token acquired.')
            if await test_access_token():
                print('Access token is valid.')
                return 0

    print('Failed to acquire access token or access token is invalid.')
    return -1


def get_access_token() -> str:
    global oauth_params
    asyncio.run(main())
    return oauth_params.authorized_redirect_url


if __name__ == '__main__':
    exit(asyncio.run(main()))
