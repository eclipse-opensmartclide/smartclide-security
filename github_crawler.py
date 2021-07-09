import requests
import json

r = requests.get('https://api.github.com/search/repositories?q=\"language:python\"&order=desc&sort=stars&per_page=100')
list_url = []

for repo in r.json()['items']:
    list_url.append(repo['html_url'])

print((list_url))
print(len(list_url))