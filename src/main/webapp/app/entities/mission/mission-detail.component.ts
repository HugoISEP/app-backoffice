import { Component, OnInit } from '@angular/core';
import { IMission } from 'app/shared/model/mission.model';
import { ActivatedRoute } from '@angular/router';
import { IPosition } from 'app/shared/model/position.model';
import { PositionService } from 'app/entities/position/position.service';
import { PositionDeleteDialogComponent } from 'app/entities/position/position-delete-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { Subscription } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { MissionService } from 'app/entities/mission/mission.service';

@Component({
  selector: 'jhi-mission-detail',
  templateUrl: './mission-detail.component.html',
})
export class MissionDetailComponent implements OnInit {
  mission: IMission | null = null;
  eventSubscriber?: Subscription;

  constructor(
    protected missionService: MissionService,
    protected activatedRoute: ActivatedRoute,
    protected positionService: PositionService,
    protected modalService: NgbModal,
    protected eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mission }) => {
      this.mission = mission;
    });
    this.registerChangeInPositions();
  }

  registerChangeInPositions(): void {
    this.eventSubscriber = this.eventManager.subscribe('positionListModification', () => this.loadMission());
  }

  loadMission(): void {
    this.missionService.getById(this.mission?.id!).subscribe((response: HttpResponse<IMission>) => (this.mission = response.body));
  }

  delete(position: IPosition): void {
    const modalRef = this.modalService.open(PositionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.position = position;
  }

  getPositionId(index: number, position: IPosition): number {
    return position.id!;
  }

  togglePosition(position: IPosition): void {
    this.positionService.update({ ...position, status: !position.status }).subscribe(() => (position.status = !position.status));
  }

  previousState(): void {
    window.history.back();
  }
}
