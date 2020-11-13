export interface ICompany {
  id?: number;
  name?: string;
  emailTemplate?: string;
  totalUsers?: number;
}

export class Company implements ICompany {
  constructor(public id?: number, public name?: string, public emailTemplate?: string, public totalUsers?: number) {}
}
