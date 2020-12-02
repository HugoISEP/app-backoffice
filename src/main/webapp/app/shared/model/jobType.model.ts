export interface IJobType {
  id?: number;
  nameTranslations?: { [language: string]: string };
  name?: string;
}

export class JobType implements IJobType {
  constructor(public id?: number, public nameTranslations?: { [language: string]: string }, public name?: string) {}
}
