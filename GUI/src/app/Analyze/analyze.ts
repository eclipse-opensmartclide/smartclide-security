import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';    
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { Evaluation } from './class';
import { ChartDataSets, ChartType, RadialChartOptions, ChartOptions } from 'chart.js';
import { Label, ThemeService } from 'ng2-charts';
    
        
@Component({    
    selector:'app-analyze',    
    templateUrl:'./analyze.html'    
})    



export class AnalyzeComponent    
{
    isLoading = false;
    isFetched = false;
    propertyScores: number[] = [];
    characteristicScores: number[] = [];


    properties = {"CK":{"lcom":[0,0.10910936800871021,3.1849529780564265],"cbo":[0.017050298380221655,0.03692993475020107,0.5714285714285714],"wmc":[0.13793103448275862,0.04986595433654195,0.2765273311897106]},"PMD":{"ExceptionHandling":[0,0.22938518010164352,12.987012987012987],"Assignment":[0,0.11160028050045478,7.6923076923076925],"Logging":[0,0.05692917472098835,6.8493150684931505],"NullPointer":[0,0.32358608981534065,25.966183574879228],"ResourceHandling":[0,2.201831659093579,166.66666666666666],"MisusedFunctionality":[0,0.13732179935769162,4.784688995215311]},"Characteristics":{"Confidentiality":[0.005,0.005,0.005,0.1,0.1,0.1,0.01,0.01,0.01,0.1,0.1,0.005,0.2,0.15,0.1],"Integrity":[0.01,0.005,0.005,0.1,0.15,0.01,0.01,0.01,0.01,0.15,0.15,0.01,0.16,0.21,0.01],"Availability":[0.005,0.005,0.01,0.1,0.01,0.01,0.2,0.3,0.01,0.01,0.01,0.3,0.01,0.01,0.01]}}; 
    sonarqube = {"metricKeys":{"vulnerabilities":[0,0.09848484848484848,4]},"vulnerabilities":{"sql-injection":[0,0.013234192551328933,1.5479876160990713],"dos":[0,0.024419175132769335,2.2172949002217295],"weak-cryptography":[0,0.0015070136414874827,0.1989258006763477],"auth":[0,0.024207864640426638,3.0959752321981426],"insecure-conf":[0,0.7356100591012389,32.05128205128205]}};


    constructor(private http: HttpClient, private router: Router){}
    
    onSubmit(form: NgForm){
        this.isLoading = true;
        let fd = new FormData();
        const blob = new Blob([JSON.stringify(this.properties)], {type: "application/json"});
        fd.append('url', form.value.projectURL);
        fd.append('language', form.value.language);
        fd.append('properties', blob);
        const blob2 = new Blob([JSON.stringify(this.sonarqube)], {type: "application/json"});
        fd.append('sonarqube', blob2);
        this.http.post<Evaluation>('http://localhost:8080/smartclide/analyze', fd, {responseType: 'json'})
        .subscribe(responseData => {
            this.isFetched = true;
            this.isLoading = false;
            this.onDisplay(responseData);
            for(const key in responseData){
                console.log(key);
                console.log(responseData.Property_Scores);
                console.log(responseData);
            }
        });
    }

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

      onDisplay(data: Evaluation){
        this.radarChartData = [];  
        for(const key in data['Characteristic_Scores']){
            this.radarChartLabels.push(key);
        } 

        for(const key in data['Property_Scores']){
            this.barChartLabels.push(key);    
        }
        this.propertyScores.push(data.Property_Scores.Assignment, data['Property_Scores'].ExceptionHandling, data['Property_Scores'].Logging, data['Property_Scores'].MisusedFunctionality, data['Property_Scores'].NullPointer, data['Property_Scores'].ResourceHandling, data['Property_Scores'].auth, data['Property_Scores'].cbo, data['Property_Scores'].dos, data['Property_Scores'].lcom, data['Property_Scores'].vulnerabilities, data['Property_Scores'].wmc);
        this.characteristicScores.push(0, data['Characteristic_Scores'].Availability, data['Characteristic_Scores'].Confidentiality, data['Characteristic_Scores'].Integrity);
        this.radarChartData.push({data: this.characteristicScores, label: 'Characteristics'});
        this.barChartData.push({data: this.propertyScores, label: 'Property Scores'});
      }
} 