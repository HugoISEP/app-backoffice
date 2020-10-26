import { Component, OnInit } from '@angular/core';
import { IMission } from 'app/shared/model/mission.model';
import { ActivatedRoute } from '@angular/router';
import { IPosition } from 'app/shared/model/position.model';
import { PositionService } from 'app/entities/position/position.service';
import { MissionDeleteDialogComponent } from 'app/entities/mission/mission-delete-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-mission-detail',
  templateUrl: './mission-detail.component.html',
})
export class MissionDetailComponent implements OnInit {
  mission: IMission | null = null;

  constructor(protected activatedRoute: ActivatedRoute, protected positionService: PositionService, protected modalService: NgbModal) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mission }) => {
      this.mission = mission;
    });
  }

  getPositionId(index: number, position: IPosition): number {
    return position.id!;
  }

  togglePosition(position: IPosition): void {
    position.status = !position.status;
    this.positionService.update(position).subscribe();
  }

  previousState(): void {
    window.history.back();
  }
}
