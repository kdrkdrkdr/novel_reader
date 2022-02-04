#-*- coding:utf-8 -*-


from bs4 import BeautifulSoup
import re
import requests
import codecs
from time import sleep
import json
import asyncio
from re import sub
from papagopy.papagopy import Papagopy
from url_normalize import url_normalize
from pixivpy3 import *




findJpn = re.compile('[\u3000-\u303f\u3040-\u309f\u30a0-\u30ff\uff00-\uff9f\u4e00-\u9faf\u3400-\u4dbf]')
p = Papagopy()


def t_j2k(japanese):
    return p.translate(japanese, 'ko', 'ja')



def async_loop(func, *args):
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    loop.run_until_complete(func(*args))
    loop.close()




def GetSoup(url, referer, is_render=False, is_xpath=False):
    headers = {
        'referer': referer,
        "User-Agent": "Mozilla/5.0",
    }
    while True:
        try:
            resp = requests.get(url, headers=headers, cookies={'over18':'yes'})
            html = resp.text
            
            result =  BeautifulSoup(html, 'html.parser')    
            return result

        except:
            sleep(2)



def ListChunk(lst, n):
    return [lst[i:i+n] for i in range(0, len(lst), n)]


def PrettyJson(msg):
    return json.dumps(msg, indent=4, sort_keys=True, ensure_ascii=False)


def WriteFile(text: str, filename: str):
    f = codecs.open(filename, mode='w', encoding='utf-8')
    f.write(u'{}'.format(text))
    f.close()




def ReplacingText(text:str, repl_dict: dict):
    for key, value in repl_dict.items():
        replaced_text = str(text).replace(key, value)

    return replaced_text



def PrettifyHtml(html:str):
    return BeautifulSoup(html, 'html.parser').prettify()





def refresh_pixiv_token():
    response = requests.post(
        "https://oauth.secure.pixiv.net/auth/token",
        data={
            "client_id": "MOBrBDS8blbauoSck0ZfDbtuzpyT",
            "client_secret": "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj",
            "grant_type": "refresh_token",
            "include_policy": "true",
            "refresh_token": "dm47_E5U48t53ShUwvZc26ZLh76SJ6bfdE4hhhcRCgA",
        },
        headers={"User-Agent": "PixivAndroidApp/5.0.234 (Android 11; Pixel 5)"},
    )
    data = response.json()

    try:
        access_token = data["access_token"]
        refresh_token = data["refresh_token"]

        return access_token

    except KeyError:
        print("error:")
        exit(1)
    

