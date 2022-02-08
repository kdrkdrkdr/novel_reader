from _requirement_func import *
from pprint import pprint







# def refresh_pixiv_token():
#     response = requests.post(
#         "https://oauth.secure.pixiv.net/auth/token",
#         data={
#             "client_id": "MOBrBDS8blbauoSck0ZfDbtuzpyT",
#             "client_secret": "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj",
#             "grant_type": "refresh_token",
#             "include_policy": "true",
#             "refresh_token": "dm47_E5U48t53ShUwvZc26ZLh76SJ6bfdE4hhhcRCgA",
#         },
#         headers={"User-Agent": "PixivAndroidApp/5.0.234 (Android 11; Pixel 5)"},
#     )
#     data = response.json()

#     try:
#         access_token = data["access_token"]
#         refresh_token = data["refresh_token"]

#         return access_token

#     except KeyError:
#         print("error:")
#         exit(1)
    

# pixiv_api = AppPixivAPI()
# pixiv_api.set_auth(refresh_pixiv_token())

# episode_URLs = []
# qs = {'series_id': str(1082816)}
# while qs:
#     json_result = pixiv_api.novel_series(**qs)
#     episode_URLs.extend([novel.title for novel in json_result.novels])
#     qs = pixiv_api.parse_qs(json_result.next_url)

# c = len(episode_URLs)
# print(c)

# t='\n'.join(episode_URLs)

# b = p.translate(t, 'ko')

# print(b, len(b.split('\n')))

