import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { CompanyService } from 'app/entities/company/company.service';
import { Company, ICompany } from 'app/shared/model/company.model';

@Component({
  selector: 'jhi-company-update',
  templateUrl: './company-update.component.html',
})
export class CompanyUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    emailTemplate: [null, [Validators.required]],
  });

  constructor(
    protected companyService: CompanyService,
    protected activateRoute: ActivatedRoute,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(({ company }) => {
      this.updateForm(company);
    });
    if (this.router.url === '/company/own') {
      this.companyService.getUserCompany().subscribe(company => {
        this.updateForm(company.body!);
      });
    }
  }

  updateForm(company: ICompany): void {
    this.editForm.patchValue({
      id: company.id,
      name: company.name,
      emailTemplate: company.emailTemplate,
    });
  }

  private createForm(): ICompany {
    return {
      ...new Company(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      emailTemplate: this.editForm.get(['emailTemplate'])!.value,
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICompany>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  previousState(): void {
    if (this.router.url !== '/company/own') {
      window.history.back();
    }
  }

  save(): void {
    this.isSaving = true;
    const company = this.createForm();
    if (company.id !== undefined) {
      this.subscribeToSaveResponse(this.companyService.update(company));
    } else {
      this.subscribeToSaveResponse(this.companyService.create(company));
    }
  }
}
