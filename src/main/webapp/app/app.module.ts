import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { HugoIsepSharedModule } from 'app/shared/shared.module';
import { HugoIsepCoreModule } from 'app/core/core.module';
import { HugoIsepAppRoutingModule } from './app-routing.module';
import { HugoIsepHomeModule } from './home/home.module';
import { HugoIsepEntityModule } from './entities/entity.module';
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
    HugoIsepSharedModule,
    HugoIsepCoreModule,
    HugoIsepHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    LoginModule,
    HugoIsepEntityModule,
    HugoIsepAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class HugoIsepAppModule {}
