import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { Authority } from '../../shared/constants/authority.constants';
import { MissionComponent } from './mission.component';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { MissionDetailComponent } from './mission-detail.component';
import { MissionUpdateComponent } from './mission-update.component';
import { Injectable } from '@angular/core';
import { IMission, Mission } from '../../shared/model/mission.model';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { MissionService } from './mission.service';

@Injectable({ providedIn: 'root' })
export class MissionResolve implements Resolve<IMission> {
  constructor(private service: MissionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMission> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.getById(id).pipe(
        flatMap((mission: HttpResponse<Mission>) => {
          if (mission.body) {
            return of(mission.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Mission());
  }
}

export const missionRoute: Routes = [
  {
    path: '',
    component: MissionComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MissionDetailComponent,
    resolve: {
      mission: MissionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MissionUpdateComponent,
    resolve: {
      mission: MissionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MissionUpdateComponent,
    resolve: {
      mission: MissionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
];
