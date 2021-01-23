import { NgModule } from '@angular/core';
import { HugoIsepSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { MissionComponent } from './mission.component';
import { MissionDetailComponent } from './mission-detail.component';
import { MissionUpdateComponent } from './mission-update.component';
import { MissionDeleteDialogComponent } from './mission-delete-dialog.component';
import { missionRoute } from './mission.route';
import { PositionUpdateComponent } from '../position/position-update.component';
import { PositionDeleteDialogComponent } from '../position/position-delete-dialog.component';
import { NotificationSenderDialogComponent } from '../position/notification-sender-dialog.component';

@NgModule({
  imports: [HugoIsepSharedModule, RouterModule.forChild(missionRoute)],
  declarations: [
    MissionComponent,
    MissionDetailComponent,
    MissionUpdateComponent,
    MissionDeleteDialogComponent,
    PositionUpdateComponent,
    PositionDeleteDialogComponent,
    NotificationSenderDialogComponent,
  ],
  entryComponents: [MissionDeleteDialogComponent, PositionDeleteDialogComponent, NotificationSenderDialogComponent],
})
export class MissionModule {}
