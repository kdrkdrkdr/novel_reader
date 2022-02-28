#-*- coding:utf-8 -*-


import shutil
import jaconv
from url_normalize import url_normalize
from bs4 import BeautifulSoup
import re
import requests
import codecs
from time import sleep
import json
import asyncio
from re import sub
from papagopy.papagopy import Papagopy
from papagopy import constants
from pixivpy3 import *

from janome.tokenizer import Tokenizer
from jaconv import alphabet2kata
from _requirement_func import *
import json
import ko_pron

import sqlite3


findJpn = re.compile('[\u3000-\u303f\u3040-\u309f\u30a0-\u30ff\uff00-\uff9f\u4e00-\u9faf\u3400-\u4dbf]')
p = Papagopy()


temp_user_dict_location = "/data/data/com.kdr.novel_reader/databases/temp_userdict.csv"
user_dict_db = "/data/data/com.kdr.novel_reader/databases/userdict.db"




def WriteFile(text: str, filename: str):
    f = codecs.open(filename, mode='w', encoding='utf-8')
    f.write(u'{}'.format(text))
    f.close()


def ReadFile(filename: str):
    f = codecs.open(filename, mode='r', encoding='utf-8')
    return f.read()





# ン 처리를 해야함. 
# https://namu.wiki/w/%E3%82%93 참고해서 고쳐야한다... 유키농!!
def ko2kata(string):
    r = ko_pron.romanise(string, 'rr')
    r = ReplacingText(r, {
        'si':'shi',
        'cheu':'tsu',
        'seu':'su',
        'ja':'za',
        'jeu':'zu',
        'je':'ze',
        'jo':'zo',
        'nxtsu':'n',
        '-':'',
    }) # 영어 표기에 잘못된 표기 커버 침.
    a = alphabet2kata(r)
    print(f"{string}->{r}->{a}")
    return a



def GetDictionary():
    user_dict = {}

    try:
        conn = sqlite3.connect(user_dict_db)
    except sqlite3.OperationalError:
        return {}

    cur = conn.cursor()
    cur.execute("SELECT * FROM user_dict")
    data = cur.fetchall()
    
    for d in data:
        try:
            print(d)
            user_dict[d[1]] = d[2]
        except: continue
    conn.close()
    return user_dict


def RestoreCSV():
    user_dict = GetDictionary()
    WriteFile("\n".join([f"{k},名詞,{ko2kata(v)}" for k, v in user_dict.items() if len(k)>1]), temp_user_dict_location)





def LoadNewDatabase(dict_string):
    try:
        conn = sqlite3.connect(user_dict_db)
    except sqlite3.OperationalError:
        return
    cur = conn.cursor()
    
    cur.execute('DELETE FROM user_dict;')
    conn.commit()

    r = dict_string.split('\n')

    count = 0
    for i in r:
        a = i.replace(' ', '')
        if not a.startswith('//'):
            b = a.split(',')
            try:
                ja_name = b[0]
                ko_name = b[1]
                cur.execute(f"insert into user_dict values ('{count}', '{ja_name}', '{ko_name}')")
                count += 1
                conn.commit()
            except IndexError:
                print("IDXERROR")

    
    conn.close()
    RestoreCSV()
    



# 얘만 잘 되고, 파파고가 뜻대로 읽어주기만 하면 상관없는데..
def TextPreProcessing(text: str):

    user_dict = GetDictionary()
    custom_noun_list = set(user_dict.keys())

    if len(user_dict) == 0:
        return text

    t = Tokenizer(udic=temp_user_dict_location, udic_type="simpledic", mmap=False)

    # TODO: 연속된 사전 단어의 경우 가타카나가 겹쳐서 띄어쓰기 안 되는 부분 있을 수 있으므로 처리해야함.
    ifCon = False

    content = ''
    for token in t.tokenize(text):
        base = str(token).split('\t')

        word = base[0]
        setting = base[1].split(',')

        if (word in custom_noun_list) and ('名詞' in setting):
            content += f"{ko2kata(user_dict[word])}"
            continue

        content += word

    return content                   





def translate_content(content: str, lang_code: str):
    if lang_code == 'ja':
        return content

    else:
        content = TextPreProcessing(content)

        if 'zh' in lang_code:
            return p.translate(content, 'zh-CN')

        elif not lang_code in constants.codes['all']:
            return p.translate(content, 'en')

        else:
            return p.translate(content, lang_code)







def async_loop(func, *args):
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    loop.run_until_complete(func(*args))
    loop.close()




def GetSoup(url, referer):
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






def ReplacingText(text:str, repl_dict: dict):
    for key, value in repl_dict.items():
        text = str(text).replace(key, value)
    return text



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
    

def get_pixiv_token():
    soup = GetSoup('https://github.com/kdrkdrkdr/public_key/blob/main/key.txt', 'https:/github.com')
    return soup.find('td', {'id': 'LC1'}).text