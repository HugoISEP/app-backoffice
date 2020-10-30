import { NgModule } from '@angular/core';
import { HugoIsepSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { jobTypeRoute } from './job-type.route';
import { JobTypeComponent } from './job-type.component';
import { JobTypeDetailComponent } from './job-type-detail.component';
import { JobTypeUpdateComponent } from './job-type-update.component';
import { JobTypeDeleteDialogComponent } from './job-type-delete-dialog.component';

@NgModule({
  imports: [HugoIsepSharedModule, RouterModule.forChild(jobTypeRoute)],
  declarations: [JobTypeComponent, JobTypeDetailComponent, JobTypeUpdateComponent, JobTypeDeleteDialogComponent],
  entryComponents: [JobTypeDeleteDialogComponent],
})
export class JobTypeModule {}
