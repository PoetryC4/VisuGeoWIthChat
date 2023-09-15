from selenium import webdriver
from fastapi import FastAPI
import uvicorn
from bs4 import BeautifulSoup
from urllib.parse import urlparse

app = FastAPI()
clicked_element_content = None
driver = None


def get_element_content_on_click(url):
    # 使用Chrome浏览器，确保你已经下载并放置了对应版本的chromedriver可执行文件
    global driver
    driver = webdriver.Chrome()

    try:
        # 打开网页
        driver.get(url)

        print(driver.execute_script("a=1;a"))
        # 注册点击事件的JavaScript代码
        # Chrome在每次不同的console中执行代码会进到不同的VM中，这里间接建立了一个<p></p>来承接内容
        script = """
        src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"
        
        var para = document.createElement("p");
	    para.style.display = "none";
	    para.id = "WAILA";
        document.body.appendChild(para);
        var clickedContent = null;
        var elements = document.getElementsByTagName("*");
        for (var i = 0; i < elements.length; i++) {
            elements[i].addEventListener("keydown", function(e) {
                if(e.keyCode === 32) {
                    clickedContent = e.target.innerHTML;
                    para.innerHTML=clickedContent;
                    console.log(clickedContent)
                }
            });
        }
        $(document).ready(function(){
            const bling = $("<div>在需要监听的部分按下空格以更新IP列表</div>");
            $('body').prepend(bling);
            bling.css({
                "position":"absolute",
                "zIndex":55,
                "width":"100%",
                "position":'absolute',
                "fontSize":40+'px',
                "opacity":1,
                "userSelect":'none',
                "margin":"150px auto",
            })
            bling.css({
                'opacity': '0',
            })
            bling.animate({
                'opacity': '0.8',
            }, 200)
            bling.animate({
                'opacity': '0',
            }, 200)
            bling.animate({
                'opacity': '0.8',
            }, 200)
            bling.animate({
                'opacity': '0',
            }, 200)
            setTimeout(() => {
                bling.remove();
            },800)
            $(window).on('click', function(event){
                const scrollX = window.scrollX;
                const scrollY = window.scrollY;
                const character = $("<div></div>");
                character.text(Math.random()>0.5?'1':'0')
                $('body').append(character);
                character.css({
                    "color":Math.random()>0.5?"#00FF2C":"#FF1C00",
                    "zIndex":50,
                    "width":50*1 + "px",
                    "position":'absolute',
                    "fontSize":30+'px',
                    "opacity":1,
                    "userSelect":'none',
                    "left": event.clientX - 15 + scrollX +'px',
                    "top": event.clientY - 6 + scrollY +'px',
                })
                character.animate({
                    "top": event.clientY - 6 + scrollY - 60 +'px',
                    "opacity": "0",
                },600);
                setTimeout(() => {
                    character.remove();
                },600)
            });
        });
        """
        # 注入JavaScript代码
        driver.execute_script(script)

    except Exception as e:
        print("发生异常:", str(e))


def get_host(input: str):
    # 解析成文档对象
    soup = BeautifulSoup(input, 'html.parser')  # 文档对象
    # 非法URL 1
    invalidLink1 = '#'
    # 非法URL 2
    invalidLink2 = 'javascript:void(0)'
    # 集合
    result = {}
    # 计数器
    mycount = 0
    # 查找文档中所有a标签
    for k in soup.find_all('a'):
        # print(k)
        # 查找href标签
        link = k.get('href')
        host = urlparse(link).netloc
        # 过滤没找到的
        if (link is not None):
            mycount = mycount + 1
            # 过滤非法链接
            if link == invalidLink1:
                pass
            elif link == invalidLink2:
                pass
            elif link.find("javascript:") != -1:
                pass
            else:
                # print(mycount,link)
                if (host in result.values()):
                    continue
                result[str(mycount)] = host
    return {"count": mycount, "result": result}


@app.get("/info/get_by_id/id={id}")
async def get_by_id(id: str):
    global driver
    if driver is None:
        return "N"
    else:
        els = driver.execute_script("return document.getElementById('WAILA').innerHTML")

        soup = BeautifulSoup(els, 'html.parser')  # 文档对象
        # 非法URL 1
        invalidLink1 = '#'
        # 非法URL 2
        invalidLink2 = 'javascript:void(0)'

        target_a = soup.find_all('a')[int(id)]

        href = ""
        txt = ""
        if (target_a.get('href') != None):
            href = target_a.get('href')
        if (target_a.get_text() != None):
            txt = target_a.get_text()

        return{"href": href, "txt": txt}


@app.get("/url={url}")
async def open_url(url: str):
    replaced_url = url.replace('&slash;', '/')
    global driver
    if driver is not None:
        # 关闭浏览器
        driver.quit()
        driver = None
    get_element_content_on_click("https://" + replaced_url)
    return replaced_url


@app.get("/info/hosts")
async def get_hosts():
    global clicked_element_content
    global driver
    if driver is None:
        return "N"
    else:
        if (clicked_element_content == driver.execute_script("return document.getElementById('WAILA').innerHTML")):
            return "N"
        else:
            clicked_element_content = driver.execute_script("return document.getElementById('WAILA').innerHTML")
            return get_host(clicked_element_content)


@app.get("/close")
async def close():
    global driver
    if driver is None:
        # 关闭浏览器
        driver.quit()
        driver = None
    return "ok"


if __name__ == "__main__":
    uvicorn.run(app=app,
                host="127.0.0.1",
                port=6066,
                workers=1)
