import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ICompany } from '../../shared/model/company.model';
import { Authority } from '../../shared/constants/authority.constants';

@Injectable({ providedIn: 'root' })
export class NewUserValidator {
  public static newUserValidator(): any {
    return (formGroup: FormGroup) => {
      const emailControl = formGroup.get('email');
      const companyControl = formGroup.get('company');
      const authoritiesControl = formGroup.get('authorities');

      if (emailControl && companyControl && authoritiesControl) {
        const removeEmailSuffixError = () => {
          const emailControlErrors = { ...emailControl.errors };
          delete emailControlErrors['invalidEmailSuffix'];
          const finalEmailControlErrors = Object.keys(emailControlErrors).length > 0 ? emailControlErrors : null;
          emailControl.setErrors(finalEmailControlErrors);
        };

        const email: string = formGroup.get('email')!.value;
        const company: ICompany = formGroup.get('company')!.value;
        const authorities: string = formGroup.get('authorities')!.value;

        if (authorities === Authority.USER) {
          if (email.split('@')[1] === company.emailTemplate) {
            removeEmailSuffixError();
          } else {
            return emailControl.setErrors({ ...emailControl?.errors, ...{ invalidEmailSuffix: true } });
          }
        } else {
          removeEmailSuffixError();
        }
      }

      return null;
    };
  }
}
