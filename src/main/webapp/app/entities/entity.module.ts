import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'mission',
        loadChildren: () => import('./mission/mission.module').then(m => m.MissionModule),
      },
      {
        path: 'job-type',
        loadChildren: () => import('./jobType/job-type.module').then(j => j.JobTypeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class HugoIsepEntityModule {}
