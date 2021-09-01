import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {NavBarComponent} from './navbar/navbar.component';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { ModelsComponent } from './Security Models/models';
import { AnalyzeComponent } from './Analyze/analyze';
import { FormsModule } from '@angular/forms';
import { ArchitectureComponent } from './Architecture/architecture';
import { HttpClientModule } from '@angular/common/http';
import { ChartsModule } from 'ng2-charts';

const appRoutes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: '', component: HomeComponent},
  {path: 'models', component: ModelsComponent},
  {path: 'analyze', component: AnalyzeComponent}
];

@NgModule({
  declarations: [
    AppComponent, 
    NavBarComponent,
    AnalyzeComponent, 
    HomeComponent,
    ArchitectureComponent,
    ModelsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule, 
    AppRoutingModule, 
    RouterModule.forRoot(appRoutes),
    HttpClientModule,
    ChartsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
