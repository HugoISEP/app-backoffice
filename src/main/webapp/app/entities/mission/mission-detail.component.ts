import { Component, OnInit } from '@angular/core';
import { IMission } from 'app/shared/model/mission.model';
import { ActivatedRoute } from '@angular/router';
import { IPosition } from 'app/shared/model/position.model';
import { PositionService } from 'app/entities/position/position.service';

@Component({
  selector: 'jhi-mission-detail',
  templateUrl: './mission-detail.component.html',
})
export class MissionDetailComponent implements OnInit {
  mission: IMission | null = null;

  constructor(protected activatedRoute: ActivatedRoute, protected positionService: PositionService) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mission }) => {
      this.mission = mission;
    });
  }

  getPositionId(index: number, position: IPosition): number {
    return position.id!;
  }

  togglePosition(position: IPosition): void {
    this.positionService.update(position).subscribe(() => (position.status = !position.status));
  }

  previousState(): void {
    window.history.back();
  }
}
