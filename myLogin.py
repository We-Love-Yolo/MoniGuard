
import urllib.request
import aiohttp
from oauthlib.oauth2 import WebApplicationClient
from moni_guard_api.moni_guard_api import MoniGuardApi
import gol
MGAPI_API_CLIENT_ID = '6e7fcbc1-b51f-4111-ad44-2cf0baee8597'
MGAPI_API_CLIENT_SECRET = 'uy68Q~NPPZXDeBDfNlQBUDcB8MREXpXfpjcxJbCk'
MGAPI_API_SCOPE = ['api://6e7fcbc1-b51f-4111-ad44-2cf0baee8597/MoniGuard.Read']
MGAPI_API_REDIRECT_URL = 'https://localhost/callback'

async def main():
    oauth = WebApplicationClient(MGAPI_API_CLIENT_ID)
    url, headers, body = oauth.prepare_authorization_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/authorize',

        redirect_url=MGAPI_API_REDIRECT_URL,
        scope=MGAPI_API_SCOPE)
    print(url, headers, body)

    # firefox_bin = "/snap/firefox/current/usr/lib/firefox/firefox"
    # firefoxdriver_bin = "/snap/firefox/current/usr/lib/firefox/geckodriver"
    #
    # options = selenium.webdriver.firefox.options.Options()
    # options.binary_location = firefox_bin
    #
    # service = Service(executable_path=firefoxdriver_bin)
    # browser = Firefox(service=service, options=options)
    # browser.get(url)
    #
    # while True:
    #     get_url = browser.current_url
    #     if get_url.startswith(MGAPI_API_REDIRECT_URL):
    #         break
    #     time.sleep(1)
    # browser.close()
    # print(get_url)
    get_url = input("Enter redirect URL: ")

    # # 解析URL
    # parsed_url = urlparse(get_url)
    #
    # # 获取查询参数
    # query_params = parse_qs(parsed_url.query)
    #
    # # 获取特定参数的值
    # code = query_params.get('code', [''])[0]
    # state = query_params.get('state', [''])[0]
    #
    # print("Code:", code)
    # print("State:", state)

    url, headers, body = oauth.prepare_token_request(
        'https://login.microsoftonline.com/28fe96eb-e8dc-47a0-8b84-f7af6525ec71/oauth2/v2.0/token',
        authorization_response=get_url)

    req = urllib.request.Request(url, body.encode(), headers=headers)
    with urllib.request.urlopen(req) as res:
        oauth.parse_request_body_response(res.read())

    print(oauth.access_token)

    gol.set_value("oauth", oauth)
    oauth1 = gol.get_value("oauth")

    url = 'https://mgapi.bitterorange.cn/Residents/GetResident'
    url, headers, body = oauth1.add_token(url)
    async with aiohttp.ClientSession() as session:
        async with session.get(url, headers=headers) as response:
            print(await response.json())
    print("done")
    exit(0)
    api = MoniGuardApi('https://mgapi.bitterorange.cn')
    api.set_access_token('123')
    await api.get_analysis_api().get_faces(1)