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
import { PositionUpdateComponent } from '../position/position-update.component';
import { IPosition, Position } from '../../shared/model/position.model';
import { PositionService } from '../position/position.service';

@Injectable({ providedIn: 'root' })
export class MissionResolve implements Resolve<IMission> {
  constructor(private service: MissionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMission> | Observable<never> {
    const id = route.params['missionId'];
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

@Injectable({ providedIn: 'root' })
export class PositionResolve implements Resolve<IPosition> {
  constructor(private service: PositionService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPosition> | Observable<never> {
    const id = route.params['positionId'];
    if (id) {
      return this.service.getById(id).pipe(
        flatMap((position: HttpResponse<Position>) => {
          if (position.body) {
            return of(position.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Position());
  }
}

export const missionRoute: Routes = [
  {
    path: '',
    component: MissionComponent,
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':missionId/view',
    component: MissionDetailComponent,
    resolve: {
      mission: MissionResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':missionId/position/:positionId/edit',
    component: PositionUpdateComponent,
    resolve: {
      position: PositionResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Positions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':missionId/position/new',
    component: PositionUpdateComponent,
    resolve: {
      position: PositionResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Positions',
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
      authorities: [Authority.MANAGER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':missionId/edit',
    component: MissionUpdateComponent,
    resolve: {
      mission: MissionResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Mes Missions',
    },
    canActivate: [UserRouteAccessService],
  },
];
