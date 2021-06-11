import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { IPosition } from '../../shared/model/position.model';
import { Observable } from 'rxjs';
import { createRequestOption, Pagination } from 'app/shared/util/request-util';

type EntityResponseType = HttpResponse<IPosition>;
type EntityArrayResponseType = HttpResponse<IPosition[]>;

@Injectable({ providedIn: 'root' })
export class PositionService {
  public resourceUrl = SERVER_API_URL + 'api/position';

  constructor(protected http: HttpClient) {}

  create(position: IPosition, missionId: number): Observable<EntityResponseType> {
    return this.http.post<IPosition>(`${this.resourceUrl}/mission/${missionId}`, position, { observe: 'response' });
  }

  update(position: IPosition): Observable<EntityResponseType> {
    return this.http.put<IPosition>(this.resourceUrl, position, { observe: 'response' });
  }

  getById(id: number): Observable<EntityResponseType> {
    return this.http.get<IPosition>(`${this.resourceUrl}/full/${id}`, { observe: 'response' });
  }

  getAllByMission(missionId: number): Observable<EntityArrayResponseType> {
    return this.http.get<IPosition[]>(`${this.resourceUrl}/mission/${missionId}`, { observe: 'response' });
  }

  getAll(): Observable<EntityArrayResponseType> {
    return this.http.get<IPosition[]>(`${this.resourceUrl}/all`, { observe: 'response' });
  }

  getAllActive(req?: Pagination, searchTerm?: string): Observable<EntityArrayResponseType> {
    let options = createRequestOption(req);
    if (searchTerm) {
      options = options.set('searchTerm', searchTerm);
    }
    return this.http.get<IPosition[]>(`${this.resourceUrl}/active`, { params: options, observe: 'response' });
  }

  sendNotification(id: number): Observable<string> {
    return this.http.post<string>(`${this.resourceUrl}/${id}/notification`, { observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
