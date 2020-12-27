import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { IJobType, JobType } from '../../shared/model/jobType.model';

@Component({
  selector: 'jhi-job-type-update',
  templateUrl: './job-type-update.component.html',
})
export class JobTypeUpdateComponent implements OnInit {
  isSaving = false;
  urls: string[] | null = null;
  fakeUrls = [
    'https://s3.eu-central-1.amazonaws.com/bootstrapbaymisc/blog/24_days_bootstrap/don_quixote.jpg',
    'https://s3.eu-central-1.amazonaws.com/bootstrapbaymisc/blog/24_days_bootstrap/as_I_lay.jpg',
    'https://s3.eu-central-1.amazonaws.com/bootstrapbaymisc/blog/24_days_bootstrap/things_fall_apart.jpg',
    'https://s3.eu-central-1.amazonaws.com/bootstrapbaymisc/blog/24_days_bootstrap/sheep-3.jpg',
  ];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    imageUrl: [null, [Validators.required]],
  });

  constructor(protected jobTypeService: JobTypeService, protected activateRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(({ jobType }) => {
      this.updateForm(jobType);
    });
    this.jobTypeService.getAllImages().subscribe(urls => {
      this.urls = this.fakeUrls;
    });
  }

  handleCheckbox(url: string): void {
    this.editForm.controls['imageUrl'].setValue(url);
  }

  getImageName(url: string): string {
    return url.split('/').pop()!.split('.')[0].replaceAll('_', ' ');
  }

  updateForm(jobType: IJobType): void {
    this.editForm.patchValue({
      id: jobType.id,
      name: jobType.name,
    });
  }

  private createForm(): IJobType {
    return {
      ...new JobType(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      imageUrl: this.editForm.get(['imageUrl'])!.value,
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
}
