import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { IJobType, JobType } from '../../shared/model/jobType.model';
import { Language, defaultLanguage } from '../../shared/constants/language.constants';

@Component({
  selector: 'jhi-job-type-update',
  templateUrl: './job-type-update.component.html',
})
export class JobTypeUpdateComponent implements OnInit {
  isSaving = false;
  toggleNameInput = false;
  defaultLanguage = defaultLanguage;
  allLanguages = Object.keys(Language)
    .filter(k => typeof Language[k as any] === 'string')
    .map(k => Language[k as any]);

  editForm = this.fb.group({
    id: [],
  });

  constructor(protected jobTypeService: JobTypeService, protected activateRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.allLanguages.map(l => {
      this.editForm.addControl(l, new FormControl('', Validators.required));
    });
    this.activateRoute.data.subscribe(({ jobType }) => {
      this.updateForm(jobType);
    });
  }

  updateForm(jobType: IJobType): void {
    this.editForm.patchValue({
      id: jobType.id,
    });
    this.allLanguages.map(l => {
      this.editForm.controls[l].setValue(jobType.nameTranslations![l]);
    });
  }

  private createForm(): IJobType {
    const dict: { [x: string]: string } = {};
    this.allLanguages.map(l => {
      dict[l] = this.editForm.get([l])!.value;
    });
    return {
      ...new JobType(),
      id: this.editForm.get(['id'])!.value,
      nameTranslations: dict,
    };
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  getId(index: number, jobType: IJobType): any {
    return jobType.id;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobType>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jobType = this.createForm();
    if (jobType.id !== undefined) {
      this.subscribeToSaveResponse(this.jobTypeService.update(jobType));
    } else {
      this.subscribeToSaveResponse(this.jobTypeService.create(jobType));
    }
  }
}
