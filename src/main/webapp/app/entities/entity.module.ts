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
      {
        path: 'company',
        loadChildren: () => import('./company/company.module').then(c => c.CompanyModule),
      },
      {
        path: 'user-management',
        loadChildren: () => import('./user-management/user-management.module').then(u => u.UserManagementModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class BackofficeJEEntityModule {}
