import { Injectable } from '@angular/core';
import { SERVER_API_URL } from '../../app.constants';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IDocument } from '../../core/user/user.model';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  public resourceUrl = SERVER_API_URL + 'api/document/user/';

  constructor(protected http: HttpClient) {}

  getByUser(id: number): Observable<IDocument[]> {
    return this.http.get<IDocument[]>(this.resourceUrl + id);
  }
}
