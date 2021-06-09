import { ActivatedRouteSnapshot, Resolve, Routes } from '@angular/router';
import { UserManagementComponent } from './user-management.component';
import { Authority } from '../../shared/constants/authority.constants';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { UserManagementUpdateComponent } from './user-management-update.component';
import { Injectable } from '@angular/core';
import { IDocument, IUser, User } from '../../core/user/user.model';
import { UserService } from '../../core/user/user.service';
import { Observable, of } from 'rxjs';
import { UserManagementDetailComponent } from './user-management-detail.component';
import { DocumentService } from '../document/document.service';

@Injectable({ providedIn: 'root' })
export class UserManagementResolve implements Resolve<IUser> {
  constructor(private service: UserService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUser> {
    const id = route.params['userId'];
    if (id) {
      return this.service.find(id);
    }
    return of(new User());
  }
}

@Injectable({ providedIn: 'root' })
export class DocumentResolve implements Resolve<IDocument[]> {
  constructor(private service: DocumentService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDocument[]> {
    const id = route.params['userId'];
    if (id) {
      return this.service.getByUser(id);
    }
    return of([]);
  }
}

export const userManagementRoute: Routes = [
  {
    path: '',
    component: UserManagementComponent,
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Utilisateurs',
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':userId/edit',
    component: UserManagementUpdateComponent,
    resolve: {
      user: UserManagementResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Utilisateurs: modification',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':userId/detail',
    component: UserManagementDetailComponent,
    resolve: {
      user: UserManagementResolve,
      documents: DocumentResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Utilisateurs: détail',
    },
    canActivate: [UserRouteAccessService],
  },

  {
    path: 'new',
    component: UserManagementUpdateComponent,
    resolve: {
      user: UserManagementResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Utilisateurs: nouveau',
    },
    canActivate: [UserRouteAccessService],
  },
];
