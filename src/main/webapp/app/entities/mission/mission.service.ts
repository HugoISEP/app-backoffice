import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { IMission } from '../../shared/model/mission.model';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { map } from 'rxjs/operators';

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

  getAllByUser(): Observable<EntityArrayResponseType> {
    return this.http
      .get<IMission[]>(this.resourceUrl, { observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  getAll(): Observable<EntityArrayResponseType> {
    return this.http.get<IMission[]>(`${this.resourceUrl}/all`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((mission: IMission) => {
        mission.positions?.forEach(position => {
          position.createdAt = position.createdAt ? moment(position.createdAt) : undefined;
        });
      });
    }
    return res;
  }
}
