import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { IMission, Mission } from 'app/shared/model/mission.model';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { PositionService } from 'app/entities/position/position.service';
import { IPosition, Position } from 'app/shared/model/position.model';

@Component({
  selector: 'jhi-position-update',
  templateUrl: './position-update.component.html',
})
export class PositionUpdateComponent implements OnInit {
  isSaving = false;
  position: IPosition | null = null;
  mission: IMission | null = null;

  editForm = this.fb.group({
    id: [],
    duration: [null, [Validators.required]],
    description: [null, [Validators.required]],
    status: [null, [Validators.required]],
    createdAt: [],
  });

  constructor(protected positionService: PositionService, protected activateRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(({ position, mission }) => {
      this.updateForm(position);
      this.position = position;
      this.mission = mission;
    });
  }

  updateForm(position: IPosition): void {
    this.editForm.patchValue({
      id: position.id,
      duration: position.duration,
      description: position.description,
      status: position.status,
      createdAt: position.createdAt,
    });
  }

  private createForm(): IPosition {
    return {
      ...new Position(),
      id: this.editForm.get(['id'])!.value,
      duration: this.editForm.get(['duration'])!.value,
      description: this.editForm.get(['description'])!.value,
      status: this.editForm.get(['status'])!.value,
    };
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  getId(index: number, mission: IMission): any {
    return mission.id;
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
      this.subscribeToSaveResponse(this.positionService.create(position, this.mission?.id!));
    }
  }
}
