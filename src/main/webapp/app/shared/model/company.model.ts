export interface ICompany {
  id?: number;
  name?: string;
  createdAt?: Date;
  emailTemplate?: string;
  color?: string;
  websiteUrl?: string;
  imagePath?: string;
  totalUsers?: number;
}

export class Company implements ICompany {
  constructor(
    public id?: number,
    public name?: string,
    public createdAt?: Date,
    public emailTemplate?: string,
    public websiteUrl?: string,
    public imagePath?: string,
    public totalUsers?: number
  ) {}
}
