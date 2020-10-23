import { Component, OnDestroy, OnInit } from '@angular/core';
import { IMission } from 'app/shared/model/mission.model';
import { Subscription } from 'rxjs';
import { MissionService } from 'app/entities/mission/mission.service';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { MissionDeleteDialogComponent } from 'app/entities/mission/mission-delete-dialog.component';

@Component({
  selector: 'jhi-mission',
  templateUrl: './mission.component.html',
})
export class MissionComponent implements OnInit, OnDestroy {
  missions?: IMission[];
  eventSubscriber?: Subscription;

  constructor(protected missionService: MissionService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.missionService.getAllByUser().subscribe((response: HttpResponse<IMission[]>) => (this.missions = response.body || []));
  }

  getId(index: number, mission: IMission): number {
    return mission.id!;
  }

  delete(mission: IMission): void {
    const modalRef = this.modalService.open(MissionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.mission = mission;
  }

  registerChangeInMissions(): void {
    this.eventSubscriber = this.eventManager.subscribe('missionListModification', () => this.loadAll());
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInMissions();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }
}
