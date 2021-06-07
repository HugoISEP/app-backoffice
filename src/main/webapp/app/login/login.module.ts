import { NgModule } from '@angular/core';
import { LOGIN_PAGE_ROUTE } from './login.route';
import { RouterModule } from '@angular/router';
import { BackofficeJESharedModule } from '../shared/shared.module';
import { LoginComponent } from './login.component';

@NgModule({
  imports: [BackofficeJESharedModule, RouterModule.forChild([LOGIN_PAGE_ROUTE])],
  declarations: [LoginComponent],
})
export class LoginModule {}
