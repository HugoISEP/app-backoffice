import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { IPosition, Position } from '../../shared/model/position.model';
import { PositionService } from './position.service';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { Authority } from '../../shared/constants/authority.constants';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { MissionResolve } from '../mission/mission.route';
import { PositionUpdateComponent } from './position-update.component';

export const positionRoute: Routes = [
  {
    path: ':positionId/mission/:missionId/edit',
    component: PositionUpdateComponent,
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
    path: ':positionId/mission/:missionId/new',
    component: PositionUpdateComponent,
    resolve: {
      mission: MissionResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Mes Positions',
    },
    canActivate: [UserRouteAccessService],
  },
];
