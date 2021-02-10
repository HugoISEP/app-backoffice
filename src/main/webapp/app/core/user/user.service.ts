import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Pagination } from 'app/shared/util/request-util';
import { IUser } from './user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  public resourceUrl = SERVER_API_URL + 'api/users';

  constructor(private http: HttpClient) {}

  create(user: IUser): Observable<IUser> {
    return this.http.post<IUser>(this.resourceUrl, user);
  }

  update(user: IUser): Observable<IUser> {
    return this.http.put<IUser>(this.resourceUrl, user);
  }

  find(login: string): Observable<IUser> {
    return this.http.get<IUser>(`${this.resourceUrl}/${login}`);
  }

  query(req?: Pagination, searchTerm?: string): Observable<HttpResponse<IUser[]>> {
    let options = createRequestOption(req);
    if (searchTerm) {
      options = options.set('searchTerm', searchTerm);
    }
    return this.http.get<IUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  getUsersByManager(req?: Pagination, searchTerm?: string): Observable<HttpResponse<IUser[]>> {
    let options = createRequestOption(req);
    if (searchTerm) {
      options = options.set('searchTerm', searchTerm);
    }
    return this.http.get<IUser[]>(`${this.resourceUrl}/manager`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<{}> {
    return this.http.delete(`${this.resourceUrl}/${id}`);
  }

  authorities(): Observable<string[]> {
    return this.http.get<string[]>(SERVER_API_URL + 'api/users/authorities');
  }
}
