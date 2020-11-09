import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ICompany } from '../../shared/model/company.model';

type EntityResponseType = HttpResponse<ICompany>;
type EntityArrayResponseType = HttpResponse<ICompany[]>;

@Injectable({ providedIn: 'root' })
export class CompanyService {
  public resourceUrl = SERVER_API_URL + 'api/company';

  constructor(protected http: HttpClient) {}

  create(company: ICompany): Observable<EntityResponseType> {
    return this.http.post<ICompany>(this.resourceUrl, company, { observe: 'response' });
  }

  update(company: ICompany): Observable<EntityResponseType> {
    return this.http.put<ICompany>(this.resourceUrl, company, { observe: 'response' });
  }

  getById(id: number): Observable<EntityResponseType> {
    return this.http.get<ICompany>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAll(): Observable<EntityArrayResponseType> {
    return this.http.get<ICompany[]>(`${this.resourceUrl}/all`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
