import { Component, OnInit } from '@angular/core';
import { MissionService } from 'app/entities/mission/mission.service';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, Validators } from '@angular/forms';
import { IMission, Mission } from 'app/shared/model/mission.model';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'jhi-mission-update',
  templateUrl: './mission-update.component.html',
})
export class MissionUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    projectManagerEmail: [null, [Validators.required, Validators.email]],
  });

  constructor(protected missionService: MissionService, protected activateRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(({ mission }) => {
      this.updateForm(mission);
    });
  }

  updateForm(mission: IMission): void {
    this.editForm.patchValue({
      id: mission.id,
      name: mission.name,
      projectManagerEmail: mission.projectManagerEmail,
    });
  }

  private createForm(): IMission {
    return {
      ...new Mission(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      projectManagerEmail: this.editForm.get(['projectManagerEmail'])!.value,
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

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMission>>): void {
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
      this.subscribeToSaveResponse(this.missionService.update(mission));
    } else {
      this.subscribeToSaveResponse(this.missionService.create(mission));
    }
  }
}
