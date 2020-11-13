import { Component, OnInit } from '@angular/core';
import { User } from '../../core/user/user.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../core/user/user.service';
import { ActivatedRoute } from '@angular/router';
import { ICompany } from '../../shared/model/company.model';
import { CompanyService } from '../company/company.service';

@Component({
  selector: 'jhi-user-management-update',
  templateUrl: './user-management-update.component.html',
})
export class UserManagementUpdateComponent implements OnInit {
  user!: User;
  company?: ICompany;
  authorities: string[] = [];
  isSaving = false;
  editForm = this.fb.group({
    id: [],
    firstName: ['', [Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    email: ['', [Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    activated: [],
    company: [],
  });

  constructor(
    private userService: UserService,
    private companyService: CompanyService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      if (user) {
        this.user = user;
        if (this.user.id === undefined) {
          this.user.activated = true;
        }
        this.companyService.getUserCompany().subscribe(company => {
          this.company = company.body!;
          this.editForm.controls['email'].setValidators([
            Validators.minLength(5),
            Validators.maxLength(254),
            Validators.email,
            Validators.pattern(`^[\\w-\\.]+@${this.company.emailTemplate}$`),
          ]);
          this.updateForm(user);
        });
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.updateUser(this.user);
    if (this.user.id !== undefined) {
      this.userService.update(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    } else {
      this.userService.create(this.user).subscribe(
        () => this.onSaveSuccess(),
        () => this.onSaveError()
      );
    }
  }

  private updateForm(user: User): void {
    this.editForm.patchValue({
      id: user.id,
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      activated: user.activated,
    });
  }

  private updateUser(user: User): void {
    user.firstName = this.editForm.get(['firstName'])!.value;
    user.lastName = this.editForm.get(['lastName'])!.value;
    user.email = this.editForm.get(['email'])!.value;
    user.activated = this.editForm.get(['activated'])!.value;
    user.company = this.company;
  }

  private onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  private onSaveError(): void {
    this.isSaving = false;
  }
}
