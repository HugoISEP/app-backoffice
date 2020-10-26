export interface IJobType {
  id?: number;
  name?: string;
}

export class JobType implements IJobType {
  constructor(public id?: number, public name?: string) {}
}
