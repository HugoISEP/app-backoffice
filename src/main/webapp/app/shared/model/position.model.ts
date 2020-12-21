import { BasicIMission, IMission } from './mission.model';
import { Moment } from 'moment';
import { JobType } from 'app/shared/model/jobType.model';

export interface IPosition {
  id?: number;
  duration?: number;
  placesNumber?: number;
  remuneration?: number;
  description?: string;
  status?: boolean;
  createdAt?: Moment;
  jobType?: JobType;
  mission?: BasicIMission;
}

export class Position implements IPosition {
  constructor(
    public id?: number,
    public duration?: number,
    public placesNumber?: number,
    public remuneration?: number,
    public description?: string,
    public status?: boolean,
    public createdAt?: Moment,
    public jobType?: JobType,
    public mission?: BasicIMission
  ) {}
}
