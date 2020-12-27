export interface IJobType {
  id?: number;
  name?: string;
  imageUrl?: string;
  createdAt?: Date;
}

export class JobType implements IJobType {
  constructor(public id?: number, public name?: string, public imageUrl?: string, public createdAt?: Date) {}
}
