import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { IJobType } from '../../shared/model/jobType.model';
import { JobTypeService } from './jobType.service';

@Component({
  selector: 'jhi-job-type-delete-dialog',
  templateUrl: './job-type-delete-dialog.component.html',
})
export class JobTypeDeleteDialogComponent {
  jobType?: IJobType;

  constructor(protected jobTypeService: JobTypeService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.jobTypeService.delete(id).subscribe(() => {
      this.eventManager.broadcast('jobTypeListModification');
      this.activeModal.close();
    });
  }
}
