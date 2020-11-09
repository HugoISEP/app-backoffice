import { Component, OnInit } from '@angular/core';
import { IMission } from 'app/shared/model/mission.model';
import { MissionService } from 'app/entities/mission/mission.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ICompany } from 'app/shared/model/company.model';
import { CompanyService } from 'app/entities/company/company.service';

@Component({
  selector: 'jhi-company-delete-dialog',
  templateUrl: './company-delete-dialog.component.html',
})
export class CompanyDeleteDialogComponent {
  company?: ICompany;

  constructor(protected companyService: CompanyService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.companyService.delete(id).subscribe(() => {
      this.eventManager.broadcast('companyListModification');
      this.activeModal.close();
    });
  }
}
