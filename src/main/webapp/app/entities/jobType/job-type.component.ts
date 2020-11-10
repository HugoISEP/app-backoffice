import { Component, OnDestroy, OnInit } from '@angular/core';
import { combineLatest, Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { JobType } from '../../shared/model/jobType.model';
import { JobTypeDeleteDialogComponent } from './job-type-delete-dialog.component';
import { ITEMS_PER_PAGE } from '../../shared/constants/pagination.constants';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';

@Component({
  selector: 'jhi-job-type',
  templateUrl: './job-type.component.html',
})
export class JobTypeComponent implements OnInit, OnDestroy {
  jobTypes?: JobType[] | null = null;
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;

  constructor(
    protected jobTypeService: JobTypeService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerChangeInJobTypes();
    this.handleNavigation();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  loadAll(): void {
    this.jobTypeService
      .getAllByUser({
        page: this.page - 1,
        size: this.itemsPerPage,
      })
      .subscribe((response: HttpResponse<JobType[]>) => this.onSuccess(response.body, response.headers));
  }

  getId(index: number, jobType: JobType): number {
    return jobType.id!;
  }

  delete(jobType: JobType): void {
    const modalRef = this.modalService.open(JobTypeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.jobType = jobType;
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

  private onSuccess(jobTypes: JobType[] | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.jobTypes = jobTypes;
  }

  registerChangeInJobTypes(): void {
    this.eventSubscriber = this.eventManager.subscribe('jobTypeListModification', () => this.loadAll());
  }
}
