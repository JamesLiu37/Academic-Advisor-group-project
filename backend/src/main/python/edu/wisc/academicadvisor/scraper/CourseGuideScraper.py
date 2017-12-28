from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException
from lxml import html
from lxml.etree import tostring
from selenium.webdriver.support.ui import Select
import bs4 as bs
import requests
import json
from selenium.webdriver.support import expected_conditions as EC

browser = webdriver.PhantomJS() #replace with .Firefox(), or with the browser of your choice
browser2 = webdriver.PhantomJS() #replace with .Firefox(), or with the browser of your choice
url = "https://public.my.wisc.edu/web/expanded"
browser.get(url)
delay = 10000 # seconds
myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'keywordSearch')))
innerHTML = browser.execute_script("return document.body.innerHTML")
htmlElem = html.document_fromstring(innerHTML)
tdElems = htmlElem.find_class('btn btn-default launch-app-button ng-scope md-uw-madison-theme') #list of all td elems
inner_html = tostring(tdElems[0])
soup = bs.BeautifulSoup(inner_html, 'lxml')
for a in soup.find_all('a', href=True):
    urlimp = "https://public.my.wisc.edu/"+a['href']
browser.get(urlimp)
myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'CG_browsePluto_29_u124l1n31_12_tw_')))
innerHTML = browser.execute_script("return document.body.innerHTML")
soup = bs.BeautifulSoup(innerHTML, 'lxml')
norespg = Select(browser.find_element_by_id("resultsPerPageSelectPluto_29_u124l1n31_12_tw_"))
norespg.select_by_index(3)
prevsubj = soup.find('nobr').get_text().strip()
d = []
for it in range(20, 40):
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    Spring = browser.find_element_by_id("termChoice3")
    Spring.click()
    mySelectElement = Select(browser.find_element_by_id("subjSelPluto_29_u124l1n31_12_tw_"))
    while True:
        try:
            mySelectElement.select_by_index(it)
            break
        except:
            mySelectElement = Select(browser.find_element_by_id("subjSelPluto_29_u124l1n31_12_tw_"))
            continue
    submitButton = browser.find_element_by_id("submitButtonPluto_29_u124l1n31_12_tw_")
    submitButton.click()
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    subz = soup.find_all('nobr')
    innerHTML = browser.execute_script("return document.body.innerHTML")
    soup = bs.BeautifulSoup(innerHTML, 'lxml')
    try:
        while(soup.find('nobr').get_text().strip()==prevsubj):
            innerHTML = browser.execute_script("return document.body.innerHTML")
            soup = bs.BeautifulSoup(innerHTML, 'lxml')
        prevsubj = soup.find('nobr').get_text().strip()
    except:
        continue
    courlis = soup.find_all("tr", class_="courseResult")
    seclis = soup.find_all("a", class_="sectionExpand hide collapsibleCriteria enabled")
    try:
        seclis[0]
    except:
        continue
    x=[]
    flag = False
    cnt=0
    if(it%2!=0):
        for i in courlis:
            courses = i.find_all('td')
            for info in courses:
                try:
                    if str(info.get_text().encode('utf-8').strip()) != '' and str(info.get_text().encode('utf-8').strip()) != 'sections' and str(info.get_text().encode('utf-8').strip()) != 'Loading section information...':
                        tempstr = str(info.get_text().encode('utf-8').strip())
                        if tempstr.find("CourseSearch Description") != -1:
                            flag=True
                            if(it%2!=0):
                                x.append(str(info.get_text().encode('utf-8').strip()))
                                x.append('|')
                            browser2.get("https://public.my.wisc.edu/"+seclis[cnt]['href'])
                            cnt = cnt+1
                            innerHTMLx = browser2.execute_script("return document.body.innerHTML")
                            soupx = bs.BeautifulSoup(innerHTMLx, 'lxml')
                            secrow = soupx.find_all('tr')
                            for row in secrow:
                                section = row.find_all('td');flagtr=False
                                for infox in section:
                                    try:
                                        if str(infox.get_text().encode('utf-8').strip()) != "" and str(infox.get_text().encode('utf- 8').strip()) != "\u00a0":
                                            tempstr = str(infox.get_text().encode('utf-8').strip())
                                            try:
                                                int(tempstr)
                                            except ValueError:
                                                if(it%2!=0):
                                                    x.append(str(infox.get_text().encode('utf-8').strip()));flagtr=True
                                            except Exception as e:
                                                ex = e
                                                #print ex
                                    except Exception as e:
                                        ex = e
                                        #print ex
                                if flagtr == True:
                                    x.append('|')
                        else:
                            if(it%2!=0):
                                x.append(str(info.get_text().encode('utf-8').strip()))
                except Exception as e:
                    ex = e
                    #print ex
            if flag==True:
                if x != []:
                    d.append(x)
                    x=[]
            flag = False

browser.get(urlimp)
myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'CG_browsePluto_29_u124l1n31_12_tw_')))
innerHTML = browser.execute_script("return document.body.innerHTML")
soup = bs.BeautifulSoup(innerHTML, 'lxml')
norespg = Select(browser.find_element_by_id("resultsPerPageSelectPluto_29_u124l1n31_12_tw_"))
norespg.select_by_index(3)
prevsubj = soup.find('nobr').get_text().strip()
for it in range(20, 40): # TODO 59, 60
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    Spring = browser.find_element_by_id("termChoice3")
    Spring.click()
    mySelectElement = Select(browser.find_element_by_id("subjSelPluto_29_u124l1n31_12_tw_"))
    while True:
        try:
            mySelectElement.select_by_index(it)
            break
        except:
            mySelectElement = Select(browser.find_element_by_id("subjSelPluto_29_u124l1n31_12_tw_"))
            continue
    submitButton = browser.find_element_by_id("submitButtonPluto_29_u124l1n31_12_tw_")
    submitButton.click()
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'subjSelPluto_29_u124l1n31_12_tw_')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'termChoice3')))
    myElem = WebDriverWait(browser, delay).until(EC.presence_of_element_located((By.ID, 'submitButtonPluto_29_u124l1n31_12_tw_')))
    subz = soup.find_all('nobr')
    innerHTML = browser.execute_script("return document.body.innerHTML")
    soup = bs.BeautifulSoup(innerHTML, 'lxml')
    try:
        while(soup.find('nobr').get_text().strip()==prevsubj):
            innerHTML = browser.execute_script("return document.body.innerHTML")
            soup = bs.BeautifulSoup(innerHTML, 'lxml')
        prevsubj = soup.find('nobr').get_text().strip()
    except:
        continue
    courlis = soup.find_all("tr", class_="courseResult")
    seclis = soup.find_all("a", class_="sectionExpand hide collapsibleCriteria enabled")
    try:
        seclis[0]
    except:
        continue
    x=[]
    flag = False
    cnt=0
    if(it%2==0):
        for i in courlis:
            courses = i.find_all('td')
            for info in courses:
                try:
                    if str(info.get_text().encode('utf-8').strip()) != '' and str(info.get_text().encode('utf-8').strip()) != 'sections' and str(info.get_text().encode('utf-8').strip()) != 'Loading section information...':
                        tempstr = str(info.get_text().encode('utf-8').strip())
                        if tempstr.find("CourseSearch Description") != -1:
                            flag=True
                            if(it%2==0):
                                x.append(str(info.get_text().encode('utf-8').strip()))
                                x.append('|')
                            browser2.get("https://public.my.wisc.edu/"+seclis[cnt]['href'])
                            cnt = cnt+1
                            innerHTMLx = browser2.execute_script("return document.body.innerHTML")
                            soupx = bs.BeautifulSoup(innerHTMLx, 'lxml')
                            secrow = soupx.find_all('tr')
                            for row in secrow:
                                section = row.find_all('td');flagtr=False
                                for infox in section:
                                    try:
                                        if str(infox.get_text().encode('utf-8').strip()) != "" and str(infox.get_text().encode('utf- 8').strip()) != "\u00a0":
                                            tempstr = str(infox.get_text().encode('utf-8').strip())
                                            try:
                                                int(tempstr)
                                            except ValueError:
                                                if(it%2==0):
                                                    x.append(str(infox.get_text().encode('utf-8').strip()));flagtr=True
                                            except Exception as e:
                                                ex = e
                                                #print ex
                                    except Exception as e:
                                        ex = e
                                        #print ex
                                if flagtr == True:
                                    x.append('|')
                        else:
                            if(it%2==0):
                                x.append(str(info.get_text().encode('utf-8').strip()))
                except Exception as e:
                    ex = e
                    #print ex
            if flag==True:
                if x != []:
                    d.append(x)
                    x=[]
            flag = False
with open('data.txt', 'w') as outfile:
    json.dump(d,outfile)
print json.dumps(d)
