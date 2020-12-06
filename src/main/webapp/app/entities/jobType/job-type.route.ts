import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { Authority } from '../../shared/constants/authority.constants';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { IJobType, JobType } from '../../shared/model/jobType.model';
import { JobTypeComponent } from './job-type.component';
import { JobTypeUpdateComponent } from './job-type-update.component';
import { JobTypeDetailComponent } from './job-type-detail.component';

@Injectable({ providedIn: 'root' })
export class JobTypeResolve implements Resolve<IJobType> {
  constructor(private service: JobTypeService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJobType> | Observable<never> {
    const id = route.params['jobTypeId'];
    if (id) {
      return this.service.getById(id).pipe(
        flatMap((jobType: HttpResponse<JobType>) => {
          if (jobType.body) {
            return of(jobType.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new JobType());
  }
}

export const jobTypeRoute: Routes = [
  {
    path: '',
    component: JobTypeComponent,
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Types de poste',
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':jobTypeId/view',
    component: JobTypeDetailComponent,
    resolve: {
      jobType: JobTypeResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Types de poste',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JobTypeUpdateComponent,
    resolve: {
      jobType: JobTypeResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Types de poste',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':jobTypeId/edit',
    component: JobTypeUpdateComponent,
    resolve: {
      jobType: JobTypeResolve,
    },
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Types de poste',
    },
    canActivate: [UserRouteAccessService],
  },
];
