import { ICompany } from '../../shared/model/company.model';
import { IJobType } from '../../shared/model/jobType.model';

export interface IUser {
  id?: any;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated?: boolean;
  langKey?: string;
  authorities?: string[];
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  password?: string;
  company?: ICompany;
  documents?: IDocument[];
}

export class User implements IUser {
  constructor(
    public id?: any,
    public login?: string,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public activated?: boolean,
    public langKey?: string,
    public authorities?: string[],
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public password?: string,
    public company?: ICompany,
    public documents?: IDocument[]
  ) {}
}

export interface IDocument {
  id: any;
  type: string;
  fileUrl: string;
  fileName: string;
}

export class Document {
  constructor(public id?: any, public type?: string, public fileUrl?: string, public fileName?: string) {}
}
