import { NgModule } from '@angular/core';
import { HugoIsepSharedModule } from '../../shared/shared.module';
import { RouterModule } from '@angular/router';
import { companyRoute } from './company.route';
import { CompanyComponent } from './company.component';
import { CompanyUpdateComponent } from './company-update.component';
import { CompanyDeleteDialogComponent } from './company-delete-dialog.component';
import { CompanyDetailComponent } from './company-detail.component';
import { ColorPickerModule } from 'ngx-color-picker';

@NgModule({
  imports: [HugoIsepSharedModule, ColorPickerModule, RouterModule.forChild(companyRoute)],
  declarations: [CompanyComponent, CompanyUpdateComponent, CompanyDetailComponent, CompanyDeleteDialogComponent],
  entryComponents: [CompanyDeleteDialogComponent],
})
export class CompanyModule {}
