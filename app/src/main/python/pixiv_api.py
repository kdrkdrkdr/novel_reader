# access_token: ZC26qLThJqkIaDQxd0wMvHWYV76AruCI6m3uzITeAnQ -> 가변
# refresh_token: dm47_E5U48t53ShUwvZc26ZLh76SJ6bfdE4hhhcRCgA -> 불변

from pixivpy3 import *
from _requirement_func import *
import pixiv_auth
from pprint import pprint

USER_AGENT = "PixivAndroidApp/5.0.234 (Android 11; Pixel 5)"
REDIRECT_URI = "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback"
LOGIN_URL = "https://app-api.pixiv.net/web/v1/login"
AUTH_TOKEN_URL = "https://oauth.secure.pixiv.net/auth/token"
CLIENT_ID = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
CLIENT_SECRET = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"

REFRESH_TOKEN = "dm47_E5U48t53ShUwvZc26ZLh76SJ6bfdE4hhhcRCgA"

def refresh_pixiv_token(refresh_token):
    response = requests.post(
        AUTH_TOKEN_URL,
        data={
            "client_id": CLIENT_ID,
            "client_secret": CLIENT_SECRET,
            "grant_type": "refresh_token",
            "include_policy": "true",
            "refresh_token": refresh_token,
        },
        headers={"User-Agent": USER_AGENT},
    )
    data = response.json()

    try:
        access_token = data["access_token"]
        refresh_token = data["refresh_token"]

        return access_token

    except KeyError:
        print("error:")
        pprint(data)
        exit(1)
    



