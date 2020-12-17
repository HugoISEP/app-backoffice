export interface IJobType {
  id?: number;
  name?: string;
  createdAt?: Date;
}

export class JobType implements IJobType {
  constructor(public id?: number, public name?: string, public createdAt?: Date) {}
}
