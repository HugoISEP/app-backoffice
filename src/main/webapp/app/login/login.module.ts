import { NgModule } from '@angular/core';
import { LOGIN_PAGE_ROUTE } from './login.route';
import { RouterModule } from '@angular/router';
import { HugoIsepSharedModule } from '../shared/shared.module';
import { LoginComponent } from './login.component';

@NgModule({
  imports: [HugoIsepSharedModule, RouterModule.forChild([LOGIN_PAGE_ROUTE])],
  declarations: [LoginComponent],
})
export class LoginModule {}
