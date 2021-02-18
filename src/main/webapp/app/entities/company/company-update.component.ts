import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CompanyService } from 'app/entities/company/company.service';
import { Company, ICompany } from 'app/shared/model/company.model';

@Component({
  selector: 'jhi-company-update',
  templateUrl: './company-update.component.html',
})
export class CompanyUpdateComponent implements OnInit {
  ownCompanyUrl = '/company/own/update';
  isSaving = false;
  file: File | null = null;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    emailTemplate: [null, [Validators.required]],
    color: [null, [Validators.required, Validators.pattern('^#(?:[0-9a-fA-F]{3}){1,2}$')]],
    websiteUrl: [null, [Validators.pattern('(https?://)([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?')]],
    file: [null, [this.requiredIfNewCompany(), this.fileValidator()]],
  });

  constructor(
    protected companyService: CompanyService,
    protected activateRoute: ActivatedRoute,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.router.url === this.ownCompanyUrl) {
      this.companyService.getUserCompany().subscribe(company => {
        this.updateForm(company.body!);
      });
    } else {
      this.activateRoute.data.subscribe(({ company }) => {
        this.updateForm(company);
      });
    }
  }

  updateForm(company: ICompany): void {
    this.editForm.patchValue({
      id: company.id,
      name: company.name,
      emailTemplate: company.emailTemplate,
      websiteUrl: company.websiteUrl,
      color: company.color,
    });
  }

  private createForm(): ICompany {
    this.file = this.editForm.get(['file'])!.value;
    return {
      ...new Company(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      emailTemplate: this.editForm.get(['emailTemplate'])!.value,
      websiteUrl: this.editForm.get(['websiteUrl'])!.value,
      color: this.editForm.get(['color'])!.value,
    };
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  getId(index: number, company: ICompany): any {
    return company.id;
  }

  protected subscribeToSaveResponse(result: any): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  previousState(): void {
    if (this.router.url === '/company/own/update') {
      this.router.navigate(['/company/own/view']);
    } else {
      window.history.back();
    }
  }

  save(): void {
    this.isSaving = true;
    const company = this.createForm();
    if (company.id !== undefined) {
      this.subscribeToSaveResponse(this.companyService.update(company, this.editForm.get(['file'])!.value));
    } else {
      this.subscribeToSaveResponse(this.companyService.create(company, this.editForm.get(['file'])!.value));
    }
  }

  handleFile(e: Event): void {
    if ((e.target as HTMLInputElement).files) {
      this.editForm.controls['file'].setValue((e.target as HTMLInputElement).files![0]);
    } else {
      this.editForm.controls['file'].setValue(null);
    }
    this.editForm.controls['file']!.markAsTouched();
  }

  fileValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const urlSplit = this.router.url.split('/');
      return urlSplit[urlSplit.length - 1] === 'update'
        ? null
        : control.value?.type === 'image/png'
        ? null
        : { invalidFile: control.value };
    };
  }

  requiredIfNewCompany(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const urlSplit = this.router.url.split('/');
      return urlSplit[urlSplit.length - 1] === 'update' || control.value ? null : { required: true };
    };
  }
}
