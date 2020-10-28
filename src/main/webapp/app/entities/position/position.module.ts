import { positionRoute } from './position.route';
import { PositionDeleteDialogComponent } from './position-delete-dialog.component';
import { RouterModule } from '@angular/router';
import { HugoIsepSharedModule } from '../../shared/shared.module';
import { NgModule } from '@angular/core';

@NgModule({
  imports: [HugoIsepSharedModule, RouterModule.forChild(positionRoute)],
  declarations: [PositionDeleteDialogComponent],
  entryComponents: [PositionDeleteDialogComponent],
})
export class PositionModule {}
