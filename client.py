import requests
import json

def create_characteristics_dict():
    characteristics = ['Assignment', 'Logging', 'NullPointer', 'MisusedFunctionality', 'ResourceHandling', 'Adjustability', 'ExceptionHandling', 'loc', 'cbo', 'wmc', 'lcom', 'bugs', 'complexity', 'code_smells', 'ncloc', 'weak-cryptography', 'dos', 'csrf', 'others', 'buffer-overflow']
    thresholds = {}

    for char in characteristics:
        thresholds[char] = {'min': 1000000000000000000.0, 'average': 0.0, 'max': 0.0}
    
    return thresholds

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
    urls = ['https://github.com/baomidou/dynamic-datasource-spring-boot-starter', 'https://github.com/gazanas/FilterMapDemo-Spring']

    properties = {"CK":{"lcom":[0, 0.125, 0.7], "cbo":[0.01, 0.207, 0.5], "wmc":[0.01, 0.207, 0.5]}, "PMD":{"Adjustability":[0, 1.58, 12.14], "ExceptionHandling":[0, 3.34, 11.62], "Assignment":[0, 3.34, 11.62], "Logging":[0, 3.34, 11.62], 'NullPointer':[0, 3.3, 11.62], 'ResourceHandling':[0, 3.3, 11.62],'MisusedFunctionality': [0, 3.3, 11.62]}}
    sonarqube = {"metricKeys":{"complexity":[0, 1.147, 9.067], "code_smells":[0, 0.5, 2], "bugs":[0, 1.2, 3.7]}, "vulnerabilities": {"buffer-overflow":[0, 0.12, 0.7], "dos":[0, 1.58, 9.1], "csrf":[0, 3.3, 11.62], "weak-cryptography":[0, 3.1, 9.6], "others":[0, 1, 3]}}
    headers = {'content-type': 'application/json', }

    for url in urls:
        r = requests.post('http://localhost:8080/code/javaClient', files = {'url': url, 'properties': (None, json.dumps(properties), 'application/json'), 'sonarqube': (None, json.dumps(sonarqube), 'application/json')})
        extract_values(r.json(), thresholds)

    for keys in thresholds.keys():
        thresholds[keys]['average'] /= len(urls) 

    print(thresholds)


