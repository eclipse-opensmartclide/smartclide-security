import { Component } from '@angular/core';    
import { ChartDataSets, ChartType, RadialChartOptions, ChartOptions } from 'chart.js';
import { Label, ThemeService } from 'ng2-charts';
    

@Component({    
    selector:'app-architecture',    
    templateUrl:'./architecture.html'})    



export class ArchitectureComponent    
{   
    propertyScores: number[] = [];
    characteristicScores: number[] = [];
    
    data = {
        "CK": {
            "loc": 18951.0,
            "cbo": 0.21418394807661864,
            "lcom": 0.8621708616959527,
            "wmc": 0.1885916310484935
        },
        "PMD": {
            "Assignment": 0.0,
            "Logging": 0.0,
            "NullPointer": 0.42214131180412645,
            "MisusedFunctionality": 1.5302622552899583,
            "ResourceHandling": 0.0,
            "ExceptionHandling": 0.10553532795103161
        },
        "Sonarqube": {
            "insecure-conf": 0.0,
            "auth": 0.0,
            "ncloc": 8712.0,
            "weak-cryptography": 0.0,
            "vulnerabilities": 0.0,
            "dos": 0.4591368227731864,
            "sql-injection": 0.0
        },
        "Property Scores": {
            "Logging": 1.0,
            "NullPointer": 0.4980782909756667,
            "MisusedFunctionality": 0.35013660453021134,
            "insecure-conf": 1.0,
            "auth": 1.0,
            "lcom": 0.3775845606669905,
            "weak-cryptography": 1.0,
            "ExceptionHandling": 0.7699604483945572,
            "dos": 0.40087955220928406,
            "wmc": 0.1939803361341573,
            "sql-injection": 1.0,
            "Assignment": 1.0,
            "cbo": 0.334186655341201,
            "ResourceHandling": 1.0,
            "vulnerabilities": 1.0
        },
        "Characteristic Scores": {
            "Availability": 0.8950462030620642,
            "Confidentiality": 0.7099432952319228,
            "Integrity": 0.7522922353649535
        },
        "Security index": {
            "Security Index": 0.7857605778863135
        }
    };
    
    // RADIAL COMPONENT
    public radarChartOptions: RadialChartOptions = {
        responsive: true,
      };
      
      public radarChartLabels: Label[] = [];
      
       
      public radarChartData: ChartDataSets[] = [];
      public radarChartType: ChartType = 'radar';

    // BAR COMPONENT
      barChartOptions: ChartOptions = {
        responsive: true,
      };
      barChartLabels: Label[] = [];
      barChartType: ChartType = 'bar';
      barChartLegend = true;
      barChartPlugins = [];
    
      barChartData: ChartDataSets[] = [];
       
      constructor() { }
    
      ngOnInit() {
        this.radarChartData = [];  
        for(const key in this.data['Characteristic Scores']){
            this.radarChartLabels.push(key);
        } 

        for(const key in this.data['Property Scores']){
            this.barChartLabels.push(key);    
        }
        this.propertyScores.push(this.data['Property Scores'].Assignment, this.data['Property Scores'].ExceptionHandling, this.data['Property Scores'].Logging, this.data['Property Scores'].MisusedFunctionality, this.data['Property Scores'].NullPointer, this.data['Property Scores'].ResourceHandling, this.data['Property Scores'].auth, this.data['Property Scores'].cbo, this.data['Property Scores'].dos, this.data['Property Scores']['insecure-conf'], this.data['Property Scores'].lcom, this.data['Property Scores']['sql-injection'], this.data['Property Scores'].vulnerabilities, this.data['Property Scores']['weak-cryptography'], this.data['Property Scores'].wmc);
        this.characteristicScores.push(0, this.data['Characteristic Scores'].Availability, this.data['Characteristic Scores'].Confidentiality, this.data['Characteristic Scores'].Integrity);
        this.radarChartData.push({data: this.characteristicScores, label: 'Characteristics'});
        this.barChartData.push({data: this.propertyScores, label: 'Property Scores'});

      }
    }
