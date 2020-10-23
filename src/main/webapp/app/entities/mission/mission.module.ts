import { NgModule } from '@angular/core';
import { HugoIsepSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { MissionComponent } from './mission.component';
import { MissionDetailComponent } from './mission-detail.component';
import { MissionUpdateComponent } from './mission-update.component';
import { MissionDeleteDialogComponent } from './mission-delete-dialog.component';
import { missionRoute } from './mission.route';

@NgModule({
  imports: [HugoIsepSharedModule, RouterModule.forChild(missionRoute)],
  declarations: [MissionComponent, MissionDetailComponent, MissionUpdateComponent, MissionDeleteDialogComponent],
  entryComponents: [MissionDeleteDialogComponent],
})
export class MissionModule {}
