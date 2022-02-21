from janome.tokenizer import Tokenizer
from _requirement_func import *
import papagopy


# t = """
# 아 이 우 에 오
# 카 키 쿠 케 코
# 사 시 스 세 소
# 타 치 츠 테 토
# 나 니 누 네 노
# 하 히 후 헤 호
# 마 미 무 메 모
# 야    유    요
# 라 리 루 레 로
# 와         오

# 가 기 구 게 고
# 자 지 즈 제 조
# 다       데 도
# 바 비 부 베 보
# 파 피 푸 페 포

# """.split('\n')
# t = "스이세이"
# ko2kata(t)


t=p.translate("hello", 'ko')
print(t)
# text = ReadFile('ex_novel.txt')


# user_dict = {
#         "白金":"시로카네",
#         "燐子":"린코",
#         "有咲":"아리사",
#         "彩":"아야",
#         "比企谷":"히키가야",
#         "八幡":"하치만",
#         "陽乃":"하루노",
#         "氷川":"히카와",
#         "紗夜":"사요",
#         "小町":"코마치",
#         "弦巻":"츠루마키",
#         "こころ":"코코로",
#         "城廻":"시로메구리",
#         "めぐり":"메구리",
#         "雪ノ下":"유키노시타",
#         "結衣":"유이"
#     }


# temp_user_dict_location = "temp_user_dict.csv"
# WriteFile("\n".join([f"{k},名詞,{ko2kata(v)}" for k, v in user_dict.items() if len(k)>1]), temp_user_dict_location)

# t = Tokenizer(udic=temp_user_dict_location, udic_type="simpledic")

# ifCon = False
# content = ''

# for token in t.tokenize(text):
#     base = str(token).split('\t')

#     word = base[0]
#     setting = base[1].split(',')

#     if (word in list(user_dict.keys())) and ('名詞' in setting):
#         if ifCon:
#             content += " "
#         content += f"{ko2kata(user_dict[word])}"
#         ifCon = True
#         continue

#     content += word
#     ifCon = False


# WriteFile(content, "pre.txt")

# trans = p.translate(content, 'ko')
# WriteFile(trans, 'transed.txt')