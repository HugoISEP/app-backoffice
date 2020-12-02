import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { IJobType, JobType } from '../../shared/model/jobType.model';
import { Language } from '../../shared/constants/language.constants';

@Component({
  selector: 'jhi-job-type-update',
  templateUrl: './job-type-update.component.html',
})
export class JobTypeUpdateComponent implements OnInit {
  isSaving = false;
  toggleNameInput = false;
  allLanguages = Object.keys(Language)
    .filter(k => typeof Language[k as any] === 'string')
    .map(k => Language[k as any]);

  editForm = this.fb.group({
    id: [],
    //name: [null, [Validators.required]],
  });

  constructor(protected jobTypeService: JobTypeService, protected activateRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(({ jobType }) => {
      this.updateForm(jobType);
    });
    this.allLanguages.map(l => {
      this.editForm.addControl(l, new FormControl('', Validators.required));
    });
  }

  updateForm(jobType: IJobType): void {
    this.editForm.patchValue({
      id: jobType.id,
      name: jobType.name,
    });
    this.allLanguages.map(l => {
      this.editForm.addControl(l, new FormControl(jobType.nameTranslations![l], Validators.required));
    });
  }

  private createForm(): IJobType {
    const dict: { [x: string]: string } = {};
    let name = '';
    this.allLanguages.map(l => {
      if (l === 'FR') {
        //a modifier of course
        name = this.editForm.get([l])!.value;
      }
      dict[l] = this.editForm.get([l])!.value;
    });
    return {
      ...new JobType(),
      id: this.editForm.get(['id'])!.value,
      nameTranslations: dict,
      name,
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
