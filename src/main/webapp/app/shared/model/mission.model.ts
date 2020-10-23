import { IUser } from '../../core/user/user.model';

export interface IMission {
  id?: number;
  name?: string;
  user?: IUser;
}

export class Mission implements IMission {
  constructor(public id?: number, public name?: string, public user?: IUser) {}
}
