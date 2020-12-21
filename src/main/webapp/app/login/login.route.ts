import { Route } from '@angular/router';
import { LoginComponent } from './login.component';

export const LOGIN_PAGE_ROUTE: Route = {
  path: '',
  component: LoginComponent,
  data: {
    pageTitle: 'Connexion',
  },
};
