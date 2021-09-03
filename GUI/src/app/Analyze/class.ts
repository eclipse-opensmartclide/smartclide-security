export interface Evaluation{
    CK: {loc:number, cbo:number, lcom:number, wmc: number};
    PMD: {Assignment: number, Logging: number, NullPointer: number, MisusedFunctionality: number, ResourceHandling: number, ExceptionHandling: number};
    Sonarqube: {auth: string, ncloc: string, vulnerabilities: string, dos: string, insecure_conf:number, sql_injection:number, weak_cryptography:number}
    Property_Scores: {loc:number, cbo:number, lcom:number, wmc: number, Assignment: number, Logging: number, NullPointer: number, MisusedFunctionality: number, ResourceHandling: number, ExceptionHandling: number, auth: number, ncloc: number, vulnerabilities: number, dos: number, insecure_conf:number, sql_injection:number, weak_cryptography:number}
    Characteristic_Scores: {Availability: number, Confidentiality: number, Integrity: number};
    Security_index: number;
}
