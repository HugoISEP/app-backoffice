import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { IJobType, JobType } from '../../shared/model/jobType.model';
import { icons } from '../../shared/constants/icons.constant';

@Component({
  selector: 'jhi-job-type-update',
  templateUrl: './job-type-update.component.html',
})
export class JobTypeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    icon: [null, [Validators.required, this.iconValidator()]],
  });

  constructor(protected jobTypeService: JobTypeService, protected activateRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(({ jobType }) => {
      this.updateForm(jobType);
    });
  }

  updateForm(jobType: IJobType): void {
    this.editForm.patchValue({
      id: jobType.id,
      name: jobType.name,
      icon: jobType.icon,
    });
  }

  private createForm(): IJobType {
    return {
      ...new JobType(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      icon: this.editForm.get(['icon'])!.value,
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
    const mission = this.createForm();
    if (mission.id !== undefined) {
      this.subscribeToSaveResponse(this.jobTypeService.update(mission));
    } else {
      this.subscribeToSaveResponse(this.jobTypeService.create(mission));
    }
  }

  iconValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null =>
      icons.includes(control.value) ? null : { invalidIcon: control.value };
  }
}
