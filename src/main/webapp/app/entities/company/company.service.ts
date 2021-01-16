import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ICompany } from '../../shared/model/company.model';
import { createRequestOption, Pagination } from '../../shared/util/request-util';

type EntityResponseType = HttpResponse<ICompany>;
type EntityArrayResponseType = HttpResponse<ICompany[]>;

@Injectable({ providedIn: 'root' })
export class CompanyService {
  public resourceUrl = SERVER_API_URL + 'api/company';

  constructor(protected http: HttpClient) {}

  create(company: ICompany, file: File): Observable<EntityResponseType> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('company', JSON.stringify(company));
    return this.http.post<ICompany>(this.resourceUrl, formData, { observe: 'response' });
  }

  update(company: ICompany): Observable<EntityResponseType> {
    return this.http.put<ICompany>(this.resourceUrl, company, { observe: 'response' });
  }

  getUserCompany(): Observable<EntityResponseType> {
    return this.http.get<ICompany>(`${this.resourceUrl}/user`, { observe: 'response' });
  }

  getById(id: number): Observable<EntityResponseType> {
    return this.http.get<ICompany>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAll(): Observable<EntityArrayResponseType> {
    return this.http.get<ICompany[]>(this.resourceUrl, { observe: 'response' });
  }

  getAllPaginated(req?: Pagination, searchTerm?: string): Observable<EntityArrayResponseType> {
    let options = createRequestOption(req);
    if (searchTerm) {
      options = options.set('searchTerm', searchTerm);
    }
    return this.http.get<ICompany[]>(`${this.resourceUrl}/all`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFileUrl(id: number): Observable<HttpResponse<string>> {
    return this.http.get<string>(`${this.resourceUrl}/${id}/file`, { observe: 'response' });
  }
}
