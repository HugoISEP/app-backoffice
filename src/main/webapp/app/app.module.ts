import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { BackofficeJESharedModule } from 'app/shared/shared.module';
import { BackofficeJECoreModule } from 'app/core/core.module';
import { BackofficeJEAppRoutingModule } from './app-routing.module';
import { BackofficeJEHomeModule } from './home/home.module';
import { BackofficeJEEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
import { LoginModule } from 'app/login/login.module';

@NgModule({
  imports: [
    BrowserModule,
    BackofficeJESharedModule,
    BackofficeJECoreModule,
    BackofficeJEHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    LoginModule,
    BackofficeJEEntityModule,
    BackofficeJEAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class BackofficeJEAppModule {}
