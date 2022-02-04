from _requirement_func import *
from novel_reader import *


def get_novel_title(novel_url):
    try:
        nr = NovelReader(novel_url)
        return nr.get_small_titles()
    except Exception as e:
        return [str(e)]


def get_content(novel_url, novel_round):
    try:
        nr = NovelReader(novel_url)
        return p.translate(nr.get_content(novel_round), 'ko')
    except Exception as e:
        return str(e)


def get_novel_big_title(novel_url):
    try:
        nr = NovelReader(novel_url)
        return nr.get_big_title()
    except Exception as e:
        return str(e)
