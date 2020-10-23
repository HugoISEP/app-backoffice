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
import { MissionComponent } from './entities/mission/mission.component';
import { MissionDeleteDialogComponent } from './entities/mission/mission-delete-dialog.component';
import { MissionDetailComponent } from './entities/mission/mission-detail.component';
import { MissionUpdateComponent } from './entities/mission/mission-update.component';

@NgModule({
  imports: [
    BrowserModule,
    HugoIsepSharedModule,
    HugoIsepCoreModule,
    HugoIsepHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    HugoIsepEntityModule,
    HugoIsepAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class HugoIsepAppModule {}
