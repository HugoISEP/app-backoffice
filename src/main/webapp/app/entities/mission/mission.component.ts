import { Component, OnDestroy, OnInit } from '@angular/core';
import { IMission, Mission } from 'app/shared/model/mission.model';
import { combineLatest, Subscription } from 'rxjs';
import { MissionService } from 'app/entities/mission/mission.service';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { MissionDeleteDialogComponent } from 'app/entities/mission/mission-delete-dialog.component';
import { ITEMS_PER_PAGE } from '../../shared/constants/pagination.constants';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';

@Component({
  selector: 'jhi-mission',
  templateUrl: './mission.component.html',
})
export class MissionComponent implements OnInit, OnDestroy {
  missions: Mission[] | null = null;
  eventSubscriber?: Subscription;
  searchTerm = '';
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;

  constructor(
    protected missionService: MissionService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.eventSubscriber = this.eventManager.subscribe('missionListModification', () => this.loadAll());
    this.handleNavigation();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  getId(index: number, mission: Mission): number {
    return mission.id!;
  }

  delete(mission: IMission): void {
    const modalRef = this.modalService.open(MissionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.mission = mission;
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      this.page = page !== null ? +page : 1;
      this.loadAll();
    }).subscribe();
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
      },
    });
  }

  private onSuccess(missions: Mission[] | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.missions = missions;
  }

  loadAll(): void {
    this.missionService
      .getAllByUser(
        {
          page: this.page - 1,
          size: this.itemsPerPage,
        },
        this.searchTerm
      )
      .subscribe((response: HttpResponse<Mission[]>) => this.onSuccess(response.body, response.headers));
  }
}
