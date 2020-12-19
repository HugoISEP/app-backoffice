import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { navbarRoute } from './layouts/navbar/navbar.route';
import { Authority } from 'app/shared/constants/authority.constants';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { DEBUG_INFO_ENABLED } from 'app/app.constants';

const LAYOUT_ROUTES = [navbarRoute, ...errorRoute];

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: '',
          redirectTo: 'login',
          pathMatch: 'full',
        },
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'home',
          loadChildren: () => import('./home/home.module').then(m => m.HugoIsepHomeModule),
        },
        {
          path: 'login',
          loadChildren: () => import('./login/login.module').then(l => l.LoginModule),
        },
        ...LAYOUT_ROUTES,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class HugoIsepAppRoutingModule {}
