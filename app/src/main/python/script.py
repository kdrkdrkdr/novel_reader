from _requirement_func import *
from novel_reader import *



def main(novel_url):
    try:
        nr = NovelReader(novel_url=novel_url)
        
        
        
    except Exception as e:
        return str(e)

    return nr.get_small_titles()