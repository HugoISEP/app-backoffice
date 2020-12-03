import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IJobType } from '../../shared/model/jobType.model';
import { JobTypeService } from '../jobType/jobType.service';
import { PositionService } from './position.service';
import { IPosition, Position } from '../../shared/model/position.model';
import { Language, defaultLanguage } from '../../shared/constants/language.constants';

@Component({
  selector: 'jhi-position-update',
  templateUrl: './position-update.component.html',
})
export class PositionUpdateComponent implements OnInit {
  isSaving = false;
  missionId?: number;
  jobTypes: IJobType[] = [];
  defaultLanguage = defaultLanguage;
  toggleDescriptionInput = false;
  allLanguages = Object.keys(Language)
    .filter(k => typeof Language[k as any] === 'string')
    .map(k => Language[k as any]);

  editForm = this.fb.group({
    id: [],
    duration: [null, [Validators.required, Validators.pattern('^[1-9]\\d*$')]],
    placesNumber: [null, [Validators.required, Validators.pattern('^[1-9]\\d*$')]],
    remuneration: [null, [Validators.required, Validators.pattern('^[0-9]\\d*([.,]?[0-9]\\d*)?$')]],
    status: [null, [Validators.required]],
    jobType: [null, [Validators.required]],
  });

  constructor(
    protected jobTypeService: JobTypeService,
    protected positionService: PositionService,
    protected activateRoute: ActivatedRoute,
    private fb: FormBuilder,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.missionId = this.route.snapshot.params['missionId'];
    this.allLanguages.map(l => {
      this.editForm.addControl(l, new FormControl('', Validators.required));
    });
    this.activateRoute.data.subscribe(({ position }) => {
      this.updateForm(position);
    });
    this.jobTypeService.getAllByUser().subscribe((res: HttpResponse<IJobType[]>) => (this.jobTypes = res.body || []));
  }

  getId(index: number, jobType: IJobType): any {
    return jobType.id;
  }

  updateForm(position: IPosition): void {
    this.editForm.patchValue({
      id: position.id,
      duration: position.duration,
      placesNumber: position.placesNumber,
      remuneration: position.remuneration,
      status: !!position.status,
      jobType: position.jobType,
    });
    this.allLanguages.map(l => {
      this.editForm.controls[l].setValue(position.descriptionTranslations![l]);
    });
  }

  private createForm(): IPosition {
    const dict: { [x: string]: string } = {};
    this.allLanguages.map(l => {
      dict[l] = this.editForm.get([l])!.value;
    });
    return {
      ...new Position(),
      id: this.editForm.get(['id'])!.value,
      duration: this.editForm.get(['duration'])!.value,
      placesNumber: this.editForm.get(['placesNumber'])!.value,
      remuneration: this.editForm.get(['remuneration'])!.value,
      descriptionTranslations: dict,
      status: this.editForm.get(['status'])!.value,
      jobType: this.editForm.get(['jobType'])!.value,
    };
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPosition>>): void {
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
    const position = this.createForm();
    if (position.id !== undefined) {
      this.subscribeToSaveResponse(this.positionService.update(position));
    } else {
      this.subscribeToSaveResponse(this.positionService.create(position, this.missionId!));
    }
  }
}
