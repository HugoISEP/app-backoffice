import { Route } from '@angular/router';

import { HomeComponent } from './home.component';
import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';

export const HOME_ROUTE: Route = {
  path: '',
  component: HomeComponent,
  data: {
    authorities: [Authority.ADMIN, Authority.MANAGER],
    pageTitle: 'JE Consultants',
    defaultSort: 'id,asc',
  },
  canActivate: [UserRouteAccessService],
};
