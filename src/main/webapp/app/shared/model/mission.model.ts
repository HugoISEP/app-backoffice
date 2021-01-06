import { IPosition } from 'app/shared/model/position.model';

export interface BasicIMission {
  id?: number;
  name?: string;
  createdAt?: Date;
  projectManagerEmail?: string;
}

export interface IMission extends BasicIMission {
  positions?: IPosition[];
}

export class BasicMission implements BasicIMission {
  constructor(public id?: number, public name?: string, public projectManagerEmail?: string, public createdAt?: Date) {}
}

export class Mission extends BasicMission implements IMission {
  constructor(public positions?: IPosition[]) {
    super();
  }
}
