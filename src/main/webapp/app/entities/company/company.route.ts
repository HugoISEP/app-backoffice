import { ActivatedRouteSnapshot, Resolve, Router, Routes } from '@angular/router';
import { Authority } from '../../shared/constants/authority.constants';
import { UserRouteAccessService } from '../../core/auth/user-route-access-service';
import { Injectable } from '@angular/core';
import { EMPTY, Observable, of } from 'rxjs';
import { flatMap } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { Company, ICompany } from '../../shared/model/company.model';
import { CompanyService } from './company.service';
import { CompanyComponent } from './company.component';
import { CompanyUpdateComponent } from './company-update.component';
import { JobTypeResolve } from '../jobType/job-type.route';

@Injectable({ providedIn: 'root' })
export class CompanyResolve implements Resolve<ICompany> {
  constructor(private service: CompanyService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICompany> | Observable<never> {
    const id = route.params['companyId'];
    if (id) {
      return this.service.getById(id).pipe(
        flatMap((company: HttpResponse<Company>) => {
          if (company.body) {
            return of(company.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Company());
  }
}

export const companyRoute: Routes = [
  {
    path: '',
    component: CompanyComponent,
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Junior-Entreprises',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'own',
    component: CompanyUpdateComponent,
    data: {
      authorities: [Authority.MANAGER],
      pageTitle: 'Ma Junior-Entreprise',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':companyId/edit',
    component: CompanyUpdateComponent,
    resolve: {
      company: CompanyResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Junior-Entreprises: modification',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CompanyUpdateComponent,
    resolve: {
      company: CompanyResolve,
    },
    data: {
      authorities: [Authority.ADMIN],
      pageTitle: 'Junior-Entreprises: nouveau',
    },
    canActivate: [UserRouteAccessService],
  },
];
