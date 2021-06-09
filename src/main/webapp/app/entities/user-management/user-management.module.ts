import { NgModule } from '@angular/core';
import { HugoIsepSharedModule } from '../../shared/shared.module';
import { RouterModule } from '@angular/router';
import { userManagementRoute } from './user-management.route';
import { UserManagementComponent } from './user-management.component';
import { UserManagementUpdateComponent } from './user-management-update.component';
import { UserManagementDeleteDialogComponent } from './user-management-delete-dialog.component';
import { UserManagementDetailComponent } from './user-management-detail.component';

@NgModule({
  imports: [HugoIsepSharedModule, RouterModule.forChild(userManagementRoute)],
  declarations: [
    UserManagementComponent,
    UserManagementUpdateComponent,
    UserManagementDeleteDialogComponent,
    UserManagementDetailComponent,
  ],
  entryComponents: [UserManagementDeleteDialogComponent],
})
export class UserManagementModule {}
