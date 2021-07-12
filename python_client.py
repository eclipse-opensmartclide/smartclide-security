import requests
import json

def create_characteristics_dict():
    characteristics= ['loc','lcom', 'cbo', 'wmc', 'Adjustability', 'ExceptionHandling', 'Assignment', 'Logging', 'NullPointer', 'ResourceHandling','MisusedFunctionality', 'complexity', 'code_smells', 'bugs', 'vulnerabilities', 'branch_coverage', 'buffer-overflow', 'sql-injection', 'dos', 'csrf', 'weak-cryptography', 'others', 'auth', 'insecure-conf', 'ncloc']
    thresholds = {}

    for char in characteristics:
        thresholds[char] = {'min': 1000000000000000000.0, 'average': 0.0, 'max': 0.0}
    
    return thresholds

def github_projects():

    r = requests.get('https://api.github.com/search/repositories?q=\"language:python\"&order=desc&sort=stars&per_page=100')
    list_url = []

    for repo in r.json()['items']:
        list_url.append(repo['html_url'])

    return list_url

def extract_values(r, thresholds):
    for tool in r.keys():
        for characteristic in r[tool].keys():
            if r[tool][characteristic] < thresholds[characteristic]['min']:
                thresholds[characteristic]['min'] = r[tool][characteristic]
            

            if r[tool][characteristic] > thresholds[characteristic]['max']:
                thresholds[characteristic]['max'] = r[tool][characteristic]
            

            thresholds[characteristic]['average'] += r[tool][characteristic]


if __name__ == "__main__":
    thresholds = create_characteristics_dict()
    urls = github_projects()
    urls.remove('https://github.com/world-of-open-source/MyHDL-Collections')

    properties = {"CK":{"lcom":[0, 0.125, 0.7], "cbo":[0.01, 0.207, 0.5], "wmc":[0.01, 0.207, 0.5]}, "PMD":{"Adjustability":[0, 1.58, 12.14], "ExceptionHandling":[0, 3.34, 11.62], "Assignment":[0, 3.34, 11.62], "Logging":[0, 3.34, 11.62], 'NullPointer':[0, 3.3, 11.62], 'ResourceHandling':[0, 3.3, 11.62],'MisusedFunctionality': [0, 3.3, 11.62]}}
    sonarqube = {"metricKeys":{"complexity":[0, 1.147, 9.067], "code_smells":[0, 0.5, 2], "bugs":[0, 1.2, 3.7], "vulnerabilities":[0, 1.147, 9.067], "branch_coverage":[0, 0.5, 2]}, "vulnerabilities": {"buffer-overflow":[0, 0.12, 0.7], "sql-injection":[0, 0.5, 2], "dos":[0, 1.58, 9.1], "csrf":[0, 3.3, 11.62], "weak-cryptography":[0, 3.1, 9.6], "others":[0, 1, 3], "auth":[0, 3.3, 11.62], "insecure-conf":[0, 3.1, 9.6]}}
    headers = {'content-type': 'application/json', }
    counter = 0


    for url in urls:
        r = requests.post('http://localhost:8080/code/pythonClient', files = {'url': url, 'properties': (None, json.dumps(properties), 'application/json'), 'sonarqube': (None, json.dumps(sonarqube), 'application/json')})
 
        if 'status' not in r.json() and 'NaN' not in r.json():
            print(r.json())
            extract_values(r.json(), thresholds)
            print('Project analyzed successfully.')
            counter += 1
        else:
            print('Project produced an error.')
        
        counter += 1

    for keys in thresholds.keys():
        thresholds[keys]['average'] /= counter

    print(thresholds)
    print(counter)


