import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { IPosition } from '../../shared/model/position.model';
import { Observable } from 'rxjs';
import { IJobType } from '../../shared/model/jobType.model';
import { createRequestOption, Pagination } from '../../shared/util/request-util';

type EntityResponseType = HttpResponse<IJobType>;
type EntityArrayResponseType = HttpResponse<IJobType[]>;

@Injectable({ providedIn: 'root' })
export class JobTypeService {
  public resourceUrl = SERVER_API_URL + 'api/job-type';

  constructor(protected http: HttpClient) {}

  create(jobType: IJobType): Observable<EntityResponseType> {
    return this.http.post<IJobType>(`${this.resourceUrl}`, jobType, { observe: 'response' });
  }

  update(jobType: IJobType): Observable<EntityResponseType> {
    return this.http.put<IJobType>(this.resourceUrl, jobType, { observe: 'response' });
  }

  getById(id: number): Observable<EntityResponseType> {
    return this.http.get<IJobType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAllByUser(req?: Pagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPosition[]>(`${this.resourceUrl}`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
