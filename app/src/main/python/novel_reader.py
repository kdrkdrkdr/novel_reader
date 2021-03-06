# 소설 제목, 횟차, 링크 가져오는 파일

from shutil import ExecError
from _requirement_func import *

""" # 소설 사이트 추가 근황

syosetu.org             |   O
syosetu.com             |   O
kakuyomu.jp             |   O
alphapolis.co.jp        |   O
pixiv.net               |   O
novelist.jp             |   O
estar.jp                |   O

akatsuki-novels.com     |   X
silufenia.com           |   X
mai-net.net             |   X
tinami.com              |   X
"""



##### 이제 예외처리만 하면 됩니다.


class NovelReader(object):
    def __init__(self, novel_url = "", target_lang_code = "ko"):
        self.novel_url = url_normalize(novel_url)
        self.target_lang_code = target_lang_code
        
        try:
        
            if 'syosetu.org' in self.novel_url:
                self.base_url = 'https://syosetu.org'
                self.novel_id = sub('[\D]', '', self.novel_url.replace(' ', '').split('syosetu.org/novel/')[1].split('/')[0])
                self.novel_url = f'{self.base_url}/novel/?mode=r18_cs_end&nid={self.novel_id}'
                self.soup = GetSoup(self.novel_url, self.base_url)
                self.is_r18 = True if 'あなたは18歳以上ですか？' in str(self.soup) else False


            elif 'syosetu.com' in self.novel_url:
                self.is_r18 = True if 'novel18' in self.novel_url else False
                self.base_url = 'https://novel18.syosetu.com' if self.is_r18 else 'https://ncode.syosetu.com'
                self.novel_id = self.novel_url.split('syosetu.com/')[1].split('/')[0]
                self.novel_url = self.base_url + f'/{self.novel_id}'
                self.soup = GetSoup(self.novel_url, self.base_url)



            elif 'kakuyomu.jp' in self.novel_url:
                self.base_url = 'https://kakuyomu.jp/works'
                self.novel_id = self.novel_url.split('works/')[1].split('/episodes')[0]
                self.novel_url = self.base_url + f'/{self.novel_id}'
                self.soup = GetSoup(self.novel_url, self.base_url)
                self.info = self.soup.find_all('li', {'class':'widget-toc-episode'})
                self.episode_ids = [i.find('a')['href'].split('/episodes')[1] for i in self.info]
                


            elif 'alphapolis.co.jp' in self.novel_url:
                self.base_url = 'https://www.alphapolis.co.jp'
                self.novel_url = self.novel_url.split('/episode')[0]
                self.soup = GetSoup(self.novel_url, self.base_url)
                self.info = self.soup.find('div', {'class':'episodes'}).find_all('div', {'class':'episode'})
                self.episode_URLs = [self.base_url+i.find('a')['href'] for i in self.info]



            elif 'pixiv.net' in self.novel_url:
                self.base_url = 'https://pixiv.net'
                self.pixiv_api = AppPixivAPI()
                self.pixiv_api.set_auth(get_pixiv_token())
                self.novel_url = self.novel_url
                
                if '/series/' in self.novel_url:
                    self.series_id = self.novel_url.split('/series/')[1]
                    if '?' in self.series_id:
                        self.series_id = self.series_id.split('?')[0]
                    self.is_short = False
                    
                else:
                    self.novel_id = sub('[\D]', '', self.novel_url)
                    self.check_series = self.pixiv_api.novel_detail(self.novel_id)['novel']['series']

                    self.is_short = (self.check_series == {})
                    if not self.is_short: 
                        self.series_id = self.check_series['id']

                    else:
                        self.novel_id = sub('[\D]', '', self.novel_url)

                
                if not self.is_short:
                    self.series_info = []
                    qs = {'series_id': str(self.series_id)}
                    while qs:
                        json_result = self.pixiv_api.novel_series(**qs)
                        self.series_info.extend([novel['id'] for novel in json_result['novels']])
                        qs = self.pixiv_api.parse_qs(json_result['next_url'])



            elif 'novelist.jp' in self.novel_url:
                self.base_url = self.novel_url.replace(novel_url.split('.jp')[1], '')
                self.novel_id = self.novel_url.split('.jp/')[1].split('.')[0]
                self.novel_url = self.base_url + f'/{self.novel_id}.html'
                self.soup = GetSoup(self.novel_url, self.base_url)
                self.epiCount = int(sub('[\D]', '', str(self.soup.find('div', {'class':'work_right'}).find_all('p')[1]).split('<br/>')[1]))



            elif 'estar.jp' in self.novel_url:
                self.base_url = 'https://estar.jp'
                self.novel_id = self.novel_url.split('novels/')[1].split('/')[0]
                self.novel_url = f'{self.base_url}/novels/{self.novel_id}'

                self.titleList = []
                self.epi_page = []

                self._epi_idx_page = int(sub('[\D]', '', GetSoup(f"{self.novel_url}/episodes", self.base_url).find('p', {'class':'currentPage'}).text))
                for i in range(self._epi_idx_page):
                    self._soup = GetSoup(f'https://estar.jp/novels/{self.novel_id}/episodes?page={i+1}', self.base_url).find('div', {'class':'episodeList'})
                    self.titleList.extend([j.text for j in self._soup.find_all('div', {'class':'label'})])
                    self.epi_page.extend([int(sub('[\D]', '', j.text)) for j in self._soup.find_all('p', {'class':'meta'})])

                self.soup = GetSoup(self.novel_url, self.base_url)
                self.entire_pages = int(GetSoup(self.novel_url+'/viewer?page=1', self.base_url).find('input', {'type':'number'})['max'])

            else:
                return None
        
        
        except :
            return translate_content("잘못된 요청입니다.", self.target_lang_code)





    def is_short_story(self):
        try:
            if 'syosetu.org' in self.novel_url:
                try: self.soup.find('span', {'itemprop':'name'}).text; return False
                except AttributeError: return True
            
            elif 'syosetu.com' in self.novel_url:
                try: self.soup.find('div', {'class':'index_box'}).text; return False
                except AttributeError: return True

            elif 'kakuyomu.jp' in self.novel_url:
                return False

            elif 'alphapolis.co.jp' in self.novel_url:
                return False

            elif 'pixiv.net' in self.novel_url:
                return self.is_short

            elif 'novelist.jp' in self.novel_url:
                return not self.soup.select_one('body > div.main_box > div.container > div.center_box > h2:nth-child(5)').text == '目次'

            elif 'estar.jp' in self.novel_url:
                return False

            else:
                return False
        
        except Exception as e:
            return None









    def get_big_title(self):
        try:
            if 'syosetu.org' in self.novel_url:
                if self.is_short_story():
                    bigTitle = self.soup.find('div', {'class':'ss'}).find('a').text
                else:
                    bigTitle = self.soup.find('span', {'itemprop':'name'}).text

            
            elif 'syosetu.com' in self.novel_url:
                bigTitle = self.soup.find('p', {'class':'novel_title'}).text


            elif 'kakuyomu.jp' in self.novel_url:
                bigTitle = self.soup.find('span', {'id':'catchphrase-body'}).text


            elif 'alphapolis.co.jp' in self.novel_url:
                bigTitle = self.soup.find('h2', {'class':'title'}).text.replace('\n', '')


            elif 'pixiv.net' in self.novel_url:
                self.pixiv_api.set_auth(get_pixiv_token())
                if self.is_short_story():
                    bigTitle = self.pixiv_api.novel_detail(self.novel_id)['novel']['title']
                else:
                    bigTitle = self.pixiv_api.novel_series(self.series_id)['novel_series_detail']['title']


            elif 'novelist.jp' in self.novel_url:
                bigTitle = self.soup.find('h2').text


            elif 'estar.jp' in self.novel_url:
                bigTitle = self.soup.find('h1', {'class':'title'}).text

            
            else:
                return translate_content("잘못된 주소입니다.", self.target_lang_code)

        
            return translate_content(bigTitle, self.target_lang_code)

        except Exception as e:
            return translate_content("잘못된 요청입니다.", self.target_lang_code)







    def get_small_titles(self):
        try:
            if 'syosetu.org' in self.novel_url:
                if self.is_short_story(): 
                    return ["[단편] " + translate_content(self.get_big_title(), self.target_lang_code)]
                else:
                    titleList = []
                    c = self.soup.find('div', {'id':'maind'}).find_all('div', {'class':'ss'})[2].find_all('tr')
                    
                    for i in c:
                        l = i.find_all('a', {'style':'text-decoration:none;'})
                        if len(l) !=0:
                            titleList.append(l[0].text)

                    return translate_content('\n'.join(titleList), self.target_lang_code).split('\n')



            elif 'syosetu.com' in self.novel_url:
                if self.is_short_story(): 
                    return ["[단편] " + translate_content(self.get_big_title(), self.target_lang_code)]
                else:
                    titleList = [i.text.replace('\n', '') for i in self.soup.find('div', {'class':'index_box'}).find_all('dd', {'class':'subtitle'})]
                    return translate_content('\n'.join(titleList), self.target_lang_code).split('\n')



            elif 'kakuyomu.jp' in self.novel_url:
                titleList = [i.find('span', {'class':'widget-toc-episode-titleLabel js-vertical-composition-item'}).text for i in self.info]
                return translate_content('\n'.join(titleList), self.target_lang_code).split('\n')



            elif 'alphapolis.co.jp' in self.novel_url:
                titleList = [i.find('span', {'class':'title'}).text for i in self.info]
                return translate_content('\n'.join(titleList), self.target_lang_code).split('\n') 



            elif 'pixiv.net' in self.novel_url:
                if self.is_short_story():
                    return ["단편 " + translate_content(self.get_big_title(), self.target_lang_code)]
                else:
                    titleList = []
                    self.pixiv_api.set_auth(get_pixiv_token())
                    qs = {'series_id': str(self.series_id)}
                    while qs:
                        json_result = self.pixiv_api.novel_series(**qs)
                        titleList.extend([novel.title for novel in json_result['novels']])
                        qs = self.pixiv_api.parse_qs(json_result['next_url'])

                    return translate_content('\n'.join(titleList), self.target_lang_code).split('\n')



            elif 'novelist.jp' in self.novel_url:
                titleList = [f'{i+1} 페이지' for i in range(self.epiCount)]
                return translate_content('\n'.join(titleList), self.target_lang_code).split('\n') 

            

            elif 'estar.jp' in self.novel_url:
                return translate_content('\n'.join(self.titleList), self.target_lang_code).split('\n') 


            else:
                return [translate_content("잘못된 주소입니다.", self.target_lang_code)]

        except Exception as e:
            return [str(e)]





    def get_content(self, novel_round):
        try:
            if 'syosetu.org' in self.novel_url:
                if self.is_short_story():
                    epiURL = self.novel_url
                else:
                    epiURL = f'{self.novel_url}&volume={novel_round+1}'

                content = '\n'.join([n.text for n in GetSoup(epiURL, self.base_url).find('div', {'id':'honbun'}).find_all('p')])



            elif 'syosetu.com' in self.novel_url:
                if self.is_short_story():
                    epiURL = self.novel_url
                else:
                    epiURL = url_normalize(f'{self.novel_url}/{novel_round+1}')

                content = '\n'.join([n.text for n in GetSoup(epiURL, self.base_url).find('div', {'id':'novel_honbun'}).find_all('p')])



            elif 'kakuyomu.jp' in self.novel_url:
                epiURL = url_normalize(f'{self.novel_url}/episodes/{self.episode_ids[novel_round]}')
                content = '\n'.join([n.text for n in GetSoup(epiURL, self.base_url).find('div', {'class':'widget-episodeBody js-episode-body'}).find_all('p')])



            elif 'alphapolis.co.jp' in self.novel_url:
                epiURL = self.episode_URLs[novel_round]
                content = GetSoup(epiURL, self.base_url).find('div', {'id':'novelBoby'}).text.replace('\t', '')


            
            elif 'pixiv.net' in self.novel_url:
                self.pixiv_api.set_auth(get_pixiv_token())
                if not self.is_short_story():
                    self.novel_id = self.series_info[novel_round]
                content = self.pixiv_api.novel_text(self.novel_id)['novel_text'].replace('[newpage]', '\n\n\n')


            elif 'novelist.jp' in self.novel_url:
                epiURL = f'{self.base_url}/{self.novel_id}_p{novel_round+1}.html'
                nSoup = GetSoup(epiURL, self.base_url).find('div', {'class':'work_read'})
                nSoup.find('div', {'class':'work_read_header'}).extract()
                content = nSoup.text




            elif 'estar.jp' in self.novel_url:
                if novel_round+1 == len(self.epi_page):
                    ran = range(self.epi_page[novel_round], self.entire_pages+1)
                else:
                    ran = range(self.epi_page[novel_round], self.epi_page[novel_round+1])
                

                content = '\n\n\n'.join([GetSoup(f'https://estar.jp/novels/{self.novel_id}/viewer?page={i}', self.base_url).find('div', {'lang':'ja'}).text for i in ran])

            

            else:
                return translate_content("잘못된 주소입니다.", self.target_lang_code)


        
            return translate_content(content, self.target_lang_code)

        except Exception as e:
            return str(e)