import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpResponse } from '@angular/common/http';
import { JobTypeService } from './jobType.service';
import { IJobType } from '../../shared/model/jobType.model';
import { JobTypeDeleteDialogComponent } from './job-type-delete-dialog.component';

@Component({
  selector: 'jhi-job-type',
  templateUrl: './job-type.component.html',
})
export class JobTypeComponent implements OnInit, OnDestroy {
  jobTypes?: IJobType[];
  eventSubscriber?: Subscription;

  constructor(protected jobTypeService: JobTypeService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.jobTypeService.getAllByUser().subscribe((response: HttpResponse<IJobType[]>) => (this.jobTypes = response.body || []));
  }

  getId(index: number, jobType: IJobType): number {
    return jobType.id!;
  }

  delete(jobType: IJobType): void {
    const modalRef = this.modalService.open(JobTypeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.jobType = jobType;
  }

  registerChangeInJobTypes(): void {
    this.eventSubscriber = this.eventManager.subscribe('jobTypeListModification', () => this.loadAll());
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInJobTypes();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }
}
