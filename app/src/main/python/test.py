# JANOME 속도가 너무 느리다...
# tinysegmenter 에서 이름 사전 등록할 수 있으면 속도 쌉가능인데..
# 시간 없어서 만들기도 뭐하고..
# ▂∮


from tinysegmenter import tinysegmenter
from _requirement_func import *
import time
from pprint import pprint
from janome.tokenizer import Tokenizer
from janome.analyzer import Analyzer
from janome.charfilter import *
from janome.tokenfilter import *


def logging_time(original_fn):
    def wrapper_fn(*args, **kwargs):
        start_time = time.time()
        result = original_fn(*args, **kwargs)
        end_time = time.time()
        print("WorkingTime[{}]: {} sec".format(original_fn.__name__, end_time-start_time))
        return result
    return wrapper_fn


text = ReadFile("pre.txt")



from janome.tokenizer import Tokenizer
from janome.analyzer import Analyzer
from janome.charfilter import *
from janome.tokenfilter import *

# ▂∮

@logging_time
def test():
    tokenizer = Tokenizer('temp_user_dict.csv', udic_type='simpledic', mmap=False)
    f = tokenizer.tokenize(text, wakati=True)
    ''.join(list(f))
    

test()

