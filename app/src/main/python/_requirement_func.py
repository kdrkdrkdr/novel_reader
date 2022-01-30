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








def distance(x1, y1, x2, y2):
    result = sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2))
    return result


def vector_inner_cos_to_degree(x1, y1, x2, y2):
    try:
        cos = (x1*x2 + y1*y2) / ( sqrt(x1**2 + y1**2) * sqrt(x2**2 + y2**2) )
        rad = acos(cos)
        return degrees(rad)

    except ZeroDivisionError:
        return 0.0


def ImageDownload(filename, url):
    header = {
        'User-agent' : 'Mozilla/5.0',
        'Referer' : url
    }
    while True:
        try:
            with open(filename, 'wb') as f:
                resp = requests.get(url, headers=header)
                if resp.status_code == 404:
                    break
                f.write(resp.content)
                break
        except ( exceptions.ChunkedEncodingError, exceptions.Timeout, exceptions.ConnectionError ):
            continue




def clickAction(driver, element):
    action = ActionChains(driver)
    action.click(on_element=element)
    action.perform()


