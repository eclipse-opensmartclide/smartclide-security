import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AnalyzeComponent } from './Analyze/analyze';
import { ArchitectureComponent } from './Architecture/architecture';
import { HomeComponent } from './home/home';
import { ModelsComponent } from './Security Models/models';

const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: '', component: HomeComponent},
  {path: 'models', component: ModelsComponent},
  {path: 'analyze', component: AnalyzeComponent},
  {path: 'architecture', component: ArchitectureComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
