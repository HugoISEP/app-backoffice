import { IUser } from '../../core/user/user.model';
import { IPosition } from 'app/shared/model/position.model';

export interface IMission {
  id?: number;
  name?: string;
  positions?: IPosition[];
}

export class Mission implements IMission {
  constructor(public id?: number, public name?: string, public user?: IUser) {}
}
