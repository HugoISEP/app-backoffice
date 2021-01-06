import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { IMission } from '../../shared/model/mission.model';
import { Observable } from 'rxjs';
import { createRequestOption, Pagination } from '../../shared/util/request-util';

type EntityResponseType = HttpResponse<IMission>;
type EntityArrayResponseType = HttpResponse<IMission[]>;

@Injectable({ providedIn: 'root' })
export class MissionService {
  public resourceUrl = SERVER_API_URL + 'api/mission';

  constructor(protected http: HttpClient) {}

  create(mission: IMission): Observable<EntityResponseType> {
    return this.http.post<IMission>(this.resourceUrl, mission, { observe: 'response' });
  }

  update(mission: IMission): Observable<EntityResponseType> {
    return this.http.put<IMission>(this.resourceUrl, mission, { observe: 'response' });
  }

  getById(id: number): Observable<EntityResponseType> {
    return this.http.get<IMission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAllByUser(req?: Pagination, searchTerm?: string): Observable<EntityArrayResponseType> {
    let options = createRequestOption(req);
    if (searchTerm) {
      options = options.set('searchTerm', searchTerm);
    }
    return this.http.get<IMission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getAll(): Observable<EntityArrayResponseType> {
    return this.http.get<IMission[]>(`${this.resourceUrl}/all`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
