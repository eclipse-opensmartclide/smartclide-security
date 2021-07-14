# smartclide-security
A Static Analysis Web Service implemented in Spring Boot for the security evaluation of SmartCLIDE services. The web services accepts HTTP requests and returns as a response a JSON consists of the property scores for each static analyzer and the security index.
The HTTP request is constructed as follows:
In the endpoint /code/github a POST request should arrive with the follow properties:
the url of the project's repository to be analyzed
a JSON with the name properties, containing metrics alongside with their thresholds and security Characteristics with their weights.
a JSON sonarqube, addresing to sonarqube platform picking metrics and vulrnerabilites from their with their thresholds.
the language of the project to be analyzed(Maven or Python).




A response example follows bellow:

{
    "CK": {
        "loc": 1281.0,
        "cbo": 0.234192037470726,
        "lcom": 0.4277907884465261,
        "wmc": 0.2498048399687744
    },
    "PMD": {
        "Assignment": 0.0,
        "Logging": 0.0,
        "NullPointer": 0.0,
        "MisusedFunctionality": 0.0,
        "ResourceHandling": 0.0,
        "ExceptionHandling": 0.0
    },
    "Sonarqube": {
        "insecure-conf": 0.0,
        "auth": 0.0,
        "ncloc": 10630.0,
        "weak-cryptography": 0.09407337723424271,
        "vulnerabilities": 0.0,
        "dos": 0.09407337723424271,
        "sql-injection": 0.0
    },
    "Property Scores": {
        "Logging": 1.0,
        "NullPointer": 1.0,
        "MisusedFunctionality": 1.0,
        "insecure-conf": 1.0,
        "auth": 1.0,
        "lcom": 0.44819609498402424,
        "weak-cryptography": 0.2655583721715151,
        "ExceptionHandling": 1.0,
        "dos": 0.48411806895745363,
        "wmc": 0.05894804750578893,
        "sql-injection": 1.0,
        "Assignment": 1.0,
        "cbo": 0.31546996644705605,
        "ResourceHandling": 1.0,
        "vulnerabilities": 1.0
    },
    "Characteristic Scores": {
        "Availability": 0.8261789962132463,
        "Confidentiality": 0.7013051719496822,
        "Integrity": 0.7312961271185272
    },
    "Security index": {
        "Security Index": 0.7529267650938185
    }
}
