export interface ICompany {
  id?: number;
  name?: string;
  emailTemplate?: string;
}

export class Company implements ICompany {
  constructor(public id?: number, public name?: string, public emailTemplate?: string) {}
}
