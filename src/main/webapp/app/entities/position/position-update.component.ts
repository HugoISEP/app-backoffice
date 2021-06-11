import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { IJobType } from '../../shared/model/jobType.model';
import { JobTypeService } from '../jobType/jobType.service';
import { PositionService } from './position.service';
import { IPosition, Position } from '../../shared/model/position.model';
import { UserService } from '../../core/user/user.service';
import { IUser } from '../../core/user/user.model';

@Component({
  selector: 'jhi-position-update',
  templateUrl: './position-update.component.html',
  styleUrls: ['./position-update.component.scss'],
})
export class PositionUpdateComponent implements OnInit {
  isSaving = false;
  missionId?: number;
  jobTypes: IJobType[] = [];

  //Users
  users: IUser[] = [];
  public userCtrl: FormControl = new FormControl();
  public userFilterCtrl: FormControl = new FormControl();

  editForm = this.fb.group({
    id: [],
    duration: [null, [Validators.required, Validators.pattern('^[1-9]\\d*$')]],
    placesNumber: [null, [Validators.required, Validators.pattern('^[1-9]\\d*$')]],
    remuneration: [null, [Validators.pattern('^[0-9]\\d*([.,]?[0-9]\\d*)?$')]],
    description: [null, [Validators.required, Validators.maxLength(500)]],
    status: [true, [Validators.required]],
    jobType: [null, [Validators.required]],
    comment: [],
    mark: [],
  });

  constructor(
    protected jobTypeService: JobTypeService,
    protected positionService: PositionService,
    protected userService: UserService,
    protected activateRoute: ActivatedRoute,
    private fb: FormBuilder,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.missionId = this.route.snapshot.params['missionId'];
    this.activateRoute.data.subscribe(({ position }) => {
      if (position.id) {
        this.updateForm(position);
      }
    });
    this.jobTypeService.getAllByUser().subscribe((res: HttpResponse<IJobType[]>) => (this.jobTypes = res.body || []));
    this.userService
      .getUsersByManager(
        {
          page: 0,
          size: 10,
        },
        ''
      )
      .subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));

    this.userFilterCtrl.valueChanges.subscribe(() => {
      this.userService
        .getUsersByManager(
          {
            page: 0,
            size: 10,
          },
          this.userFilterCtrl.value
        )
        .subscribe((res: HttpResponse<IUser[]>) => (this.users = res.body || []));
    });
  }

  getJobId(index: number, jobType: IJobType): any {
    return jobType.id;
  }

  getUserId(index: number, user: IUser): any {
    return user.id;
  }

  updateForm(position: IPosition): void {
    this.editForm.patchValue({
      id: position.id,
      duration: position.duration,
      placesNumber: position.placesNumber,
      remuneration: position.remuneration,
      description: position.description,
      status: position.status,
      jobType: position.jobType,
      mark: position.mark,
      comment: position.comment,
    });
    this.userCtrl.patchValue(position.user);
  }

  private createForm(): IPosition {
    return {
      ...new Position(),
      id: this.editForm.get(['id'])!.value,
      duration: this.editForm.get(['duration'])!.value,
      placesNumber: this.editForm.get(['placesNumber'])!.value,
      remuneration: this.editForm.get(['remuneration'])!.value,
      description: this.editForm.get(['description'])!.value,
      status: this.editForm.get(['status'])!.value,
      jobType: this.editForm.get(['jobType'])!.value,
      user: this.userCtrl.value,
      mark: this.editForm.get(['mark'])!.value,
      comment: this.editForm.get(['comment'])!.value,
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
    if (position.id !== null) {
      this.subscribeToSaveResponse(this.positionService.update(position));
    } else {
      this.subscribeToSaveResponse(this.positionService.create(position, this.missionId!));
    }
  }

  compareFn(u1: IUser, u2: IUser): boolean {
    return u1 && u2 ? u1.id === u2.id : u1 === u2;
  }
}
